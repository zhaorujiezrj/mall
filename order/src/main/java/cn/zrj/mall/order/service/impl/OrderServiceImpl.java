package cn.zrj.mall.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.security.UserContextHolder;
import cn.zrj.mall.common.core.util.BusinessNoUtils;
import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.enums.AlipayTradeStatusEnum;
import cn.zrj.mall.order.enums.OrderStatusEnum;
import cn.zrj.mall.order.pay.service.impl.AlipayNotifyResponse;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import cn.zrj.mall.order.feign.MemberFeignClient;
import cn.zrj.mall.order.mapper.OrderMapper;
import cn.zrj.mall.order.pay.service.PayService;
import cn.zrj.mall.order.service.OrderService;
import cn.zrj.mall.order.util.RocketMQUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RocketMQProducerProperties properties;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private MemberFeignClient memberFeignClient;
    @Autowired
    private PayService payService;


    @Override
    public void createOrder() {
        Order order = new Order()
                .setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER))
                        .setMemberId(76L);
        try {
            SendResult send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), order, null);
            log.info("send1={}", send);

            order.setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER));
            send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), order, 5);
            log.info("send2={}", send);
        } catch (Exception e) {
            log.error("MQ消息发送失败，原因：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T pay(String orderSn, Integer payType) {
        PayTypeEnum payTypeEnum = PayTypeEnum.getValue(payType);
        if (payTypeEnum == null) {
            throw new BusinessException("系统暂不支持该支付方式!");
        }
        Order order = getOrderByOrderSn(orderSn);
        Assert.isTrue(Objects.equals(OrderStatusEnum.PENDING_PAYMENT.getCode(), order.getStatus()), "订单不可支付，请刷新查看订单状态");

        RLock lock = redissonClient.getLock(RedisConstants.ORDER_SN_PREFIX + orderSn);
        try {
            lock.lock();
            T result = this.pay(order, payTypeEnum);
            //TODO 支付成功后要扣库存
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T pay(Order order, PayTypeEnum payTypeEnum) {
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outTradeNo = payService.generateNo(memberId, payTypeEnum);
        String oldOutTradeNo = order.getOutTradeNo();
        log.info("商户单号为：{}", outTradeNo);
        //更新支付类型
        order.setPayType(payTypeEnum.getValue());
        order.setOutTradeNo(outTradeNo);
        this.updateById(order);

        String openid = null;
        if (Objects.equals(payTypeEnum, PayTypeEnum.WX_JSAPI)) {
            openid = memberFeignClient.getOpenidById(memberId).getData();
        }
        return payService.pay(oldOutTradeNo, outTradeNo, order.getPayAmount(), "赅买-订单编号" + order.getOrderSn(), openid, payTypeEnum);
    }

    @Override
    public String alipayCallbackNotify(HttpServletRequest request) {
        AlipayNotifyResponse response = payService.payCallbackNotify(request, null, null, PayTypeEnum.ALI_WAP);
        if (response.isSignVerified()) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOutTradeNo, response.getOutTradeNo());
            Order order = this.getOne(wrapper);
            if (Objects.isNull(order)) {
                log.error("找不到对应的商户单号：{}", response.getOutTradeNo());
                return "success";
            }
            //如果有退款金额就是退款通知
            if (StringUtils.isNotBlank(response.getRefundFee())) {
                //如果是已退款了就不用再处理了
                if ((Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_SUCCESS.getValue())
                        || Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_FINISHED.getValue()))
                        && !Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    order.setRefundId(response.getTradeNo());
                    order.setStatus(OrderStatusEnum.REFUNDED.getCode());
                    this.updateById(order);
                }
            } else {
                //支付通知
                if ((Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_SUCCESS.getValue())
                        || Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_FINISHED.getValue()))
                        && Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    order.setStatus(OrderStatusEnum.PAYED.getCode());
                    order.setTransactionId(response.getTradeNo());
                    order.setPayTime(DateUtil.parseDateTime(response.getGmtPayment()));
                    this.updateById(order);
                }
            }
            return "success";
        } else {
            log.error("支付宝验签失败,商户单号:[{}]", response.getOutTradeNo());
            return "failure";
        }
    }

    @Override
    public void wxPayCallbackNotify(String data, HttpHeaders headers) {
        log.info("开始处理支付结果通知");
        WxPayOrderNotifyV3Result.DecryptNotifyResult result = payService.payCallbackNotify(null, data, headers, PayTypeEnum.WX_JSAPI);
        log.debug("支付通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = this.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("无法找到对应的支付订单，商户单号：【{}】", result.getOutTradeNo());
            return;
        }
        if (Objects.equals(order.getStatus(), OrderStatusEnum.PAYED.getCode())) {
            log.error("订单已完成支付，无需继续处理，【商户单号：{}】", result.getOutTradeNo());
            return;
        }
        if (Objects.equals(result.getTradeState(), WxPayConstants.WxpayTradeStatus.SUCCESS)) {
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            order.setTransactionId(result.getTransactionId());
            order.setPayTime(DateUtil.parseDateTime(result.getSuccessTime()));
            this.updateById(order);
            log.info("微信支付成功，【商户单号：{}】", result.getOutTradeNo());
        }
    }

    @Override
    public void wxRefundCallbackNotify(String data, HttpHeaders headers) {
        log.info("开始处理退款结果通知");
        WxPayRefundNotifyV3Result.DecryptNotifyResult result = payService.refundCallbackNotify(null, data, headers, PayTypeEnum.WX_JSAPI);
        log.debug("退款通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = this.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("微信退款无法找到对应的订单，【商户订单号：{},退款号：{}】", result.getOutTradeNo(), result.getOutRefundNo());
            return;
        }
        if (Objects.equals(result.getRefundStatus(), WxPayConstants.RefundStatus.SUCCESS)) {
            order.setStatus(OrderStatusEnum.REFUNDED.getCode());
            order.setRefundId(result.getRefundId());
            this.updateById(order);
            log.info("微信退款成功，【单号：{}】", result.getOutTradeNo());
        }
    }

    @Override
    public void refund(String orderSn, Integer status) {
        Order order = getOrderByOrderSn(orderSn);
        Assert.isTrue(!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode()), "该订单未支付，无需退款！");
        Assert.isTrue(!Objects.equals(order.getStatus(), OrderStatusEnum.REFUNDED.getCode()), "该订单已退款！");
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        PayTypeEnum payTypeEnum = PayTypeEnum.getValue(order.getPayType());
        String outRefundNo = payService.generateNo(memberId, payTypeEnum);
        String oldOutRefundNo = order.getOutRefundNo();
        // 如果用户的信誉积分很高就直接退款，否则就需要管理员审核后才能退款
        if (status == 1) {
            //订单价格小于20块就直接退款
            if (order.getPayAmount() < 100L * 20) {
                payService.refund(order.getOutTradeNo(), order.getOutRefundNo(), outRefundNo, order.getPayAmount(), "用户申请退款", payTypeEnum);
            } else {
                order.setStatus(OrderStatusEnum.APPLY_REFUND.getCode());
                this.updateById(order);
            }
        } else {
            payService.refund(order.getOutTradeNo(), oldOutRefundNo, outRefundNo, order.getPayAmount(), "用户申请退款", payTypeEnum);
        }
    }

    @Override
    public <T> T payQuery(String orderSn) {
        Order order = getOrderByOrderSn(orderSn);
        Assert.isTrue(StringUtils.isNotBlank(order.getOutTradeNo()), "该订单不存在商户单号！");
        return payService.payQuery(order.getOutTradeNo(), PayTypeEnum.getValue(order.getPayType()));
    }

    @Override
    public <T> T refundQuery(String orderSn) {
        Order order = getOrderByOrderSn(orderSn);
        Assert.isTrue(StringUtils.isNotBlank(order.getOutTradeNo()), "该订单不存在商户单号！");
        Assert.isTrue(StringUtils.isNotBlank(order.getOutRefundNo()), "该订单不存在商户退款单号单号！");
        return payService.refundQuery(order.getOutRefundNo(), PayTypeEnum.getValue(order.getPayType()));
    }

    @Override
    public boolean orderClose(String orderSn) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderSn, orderSn);
        Order order = this.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("订单不存在，无需关闭，[订单编号：{}]", orderSn);
            return false;
        }
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
            log.error("订单状态不是待支付的状态，无需关闭，[订单编号：{}]", orderSn);
            return false;
        }
        if (Objects.equals(order.getStatus(), OrderStatusEnum.AUTO_CANCEL.getCode())) {
            log.error("订单已关闭，无需再处理，[订单编号：{}]", orderSn);
            return false;
        }
        if (StringUtils.isNotBlank(order.getOutTradeNo())) {
            payService.close(order.getOutTradeNo(), PayTypeEnum.getValue(order.getPayType()));
        }
        order.setStatus(OrderStatusEnum.AUTO_CANCEL.getCode());
        return this.updateById(order);
    }


    private Order getOrderByOrderSn(String orderSn) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderSn, orderSn);
        return Optional.ofNullable(this.getOne(wrapper)).orElseThrow(() -> new BusinessException("订单不存在！"));
    }
}
