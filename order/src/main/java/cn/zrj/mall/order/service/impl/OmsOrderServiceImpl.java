package cn.zrj.mall.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.security.UserContextHolder;
import cn.zrj.mall.common.core.util.BusinessNoUtils;
import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import cn.zrj.mall.order.entity.OmsOrder;
import cn.zrj.mall.order.pay.core.PayService;
import cn.zrj.mall.order.pay.enums.AlipayTradeStatusEnum;
import cn.zrj.mall.order.enums.OrderStatusEnum;
import cn.zrj.mall.order.pay.enums.PayOrgType;
import cn.zrj.mall.order.pay.result.AlipayNotifyResponse;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import cn.zrj.mall.order.feign.MemberFeignClient;
import cn.zrj.mall.order.mapper.OmsOrderMapper;
import cn.zrj.mall.order.service.OmsOrderService;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Service
@Slf4j
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements OmsOrderService {


    private final Map<PayOrgType, PayService> map;

    public OmsOrderServiceImpl(List<PayService> payServiceList) {
        this.map = payServiceList.stream().collect(Collectors.toMap(PayService::getOrganizationType, v -> v));
    }

    @Autowired
    private RocketMQProducerProperties properties;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private MemberFeignClient memberFeignClient;
//    @Autowired
//    private PayService payService;




    @Override
    public void createOrder() {
        OmsOrder omsOrder = new OmsOrder()
                .setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER))
                        .setMemberId(76L);
        try {
            SendResult send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), omsOrder, null);
            log.info("send1={}", send);

            omsOrder.setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER));
            send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), omsOrder, 5);
            log.info("send2={}", send);
        } catch (Exception e) {
            log.error("MQ消息发送失败，原因：{}", e.getMessage());
        }
    }

    @Override
    public <T> T pay(String orderSn, Integer payType) {
        PayTypeEnum payTypeEnum = PayTypeEnum.getByPayType(payType);
        Assert.notNull(payTypeEnum, "系统暂不支持该支付方式!");
        OmsOrder omsOrder = getOrderByOrderSn(orderSn);
        Assert.isTrue(Objects.equals(OrderStatusEnum.PENDING_PAYMENT.getCode(), omsOrder.getStatus()), "订单不可支付，请刷新查看订单状态");

        RLock lock = redissonClient.getLock(RedisConstants.ORDER_SN_PREFIX + orderSn);
        try {
            lock.lock();
            return this.pay(omsOrder, payTypeEnum);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T pay(OmsOrder omsOrder, PayTypeEnum payTypeEnum) {
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outTradeNo = map.get(payTypeEnum.getPayOrgType())
                .generateNo(memberId, payTypeEnum);
        String oldOutTradeNo = omsOrder.getOutTradeNo();
        log.info("商户单号为：{}", outTradeNo);
        //更新支付类型
        omsOrder.setPayType(payTypeEnum.getPayType());
        omsOrder.setOutTradeNo(outTradeNo);
        this.updateById(omsOrder);

        String openid = null;
        if (Objects.equals(payTypeEnum, PayTypeEnum.WX_JSAPI)) {
            openid = memberFeignClient.getOpenidById(memberId).getData();
        }
        return map.get(payTypeEnum.getPayOrgType()).pay(oldOutTradeNo, outTradeNo, omsOrder.getPayAmount(), "赅买-订单编号" + omsOrder.getOrderSn(), openid, payTypeEnum);
    }

    @Override
    public String alipayCallbackNotify(HttpServletRequest request) {
        AlipayNotifyResponse response = map.get(PayOrgType.ALI).payCallbackNotify(request, null, null);
        if (response.isSignVerified()) {
            LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OmsOrder::getOutTradeNo, response.getOutTradeNo());
            OmsOrder omsOrder = this.getOne(wrapper);
            if (Objects.isNull(omsOrder)) {
                log.error("找不到对应的商户单号：{}", response.getOutTradeNo());
                return "success";
            }
            //如果有退款金额就是退款通知
            if (StringUtils.isNotBlank(response.getRefundFee())) {
                //如果是已退款了就不用再处理了
                if ((Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_SUCCESS.getValue())
                        || Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_FINISHED.getValue()))
                        && !Objects.equals(omsOrder.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    omsOrder.setRefundId(response.getTradeNo());
                    omsOrder.setStatus(OrderStatusEnum.REFUNDED.getCode());
                    this.updateById(omsOrder);
                }
            } else {
                //支付通知
                if ((Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_SUCCESS.getValue())
                        || Objects.equals(response.getTradeStatus(), AlipayTradeStatusEnum.TRADE_FINISHED.getValue()))
                        && Objects.equals(omsOrder.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    omsOrder.setStatus(OrderStatusEnum.PAYED.getCode());
                    omsOrder.setTransactionId(response.getTradeNo());
                    omsOrder.setPayTime(DateUtil.parseDateTime(response.getGmtPayment()));
                    this.updateById(omsOrder);
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
        WxPayOrderNotifyV3Result.DecryptNotifyResult result = map.get(PayOrgType.WX).payCallbackNotify(null, data, headers);
        log.debug("支付通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getOutTradeNo, result.getOutTradeNo());
        OmsOrder omsOrder = this.getOne(wrapper);
        if (Objects.isNull(omsOrder)) {
            log.error("无法找到对应的支付订单，商户单号：【{}】", result.getOutTradeNo());
            return;
        }
        if (Objects.equals(omsOrder.getStatus(), OrderStatusEnum.PAYED.getCode())) {
            log.error("订单已完成支付，无需继续处理，【商户单号：{}】", result.getOutTradeNo());
            return;
        }
        if (Objects.equals(result.getTradeState(), WxPayConstants.WxpayTradeStatus.SUCCESS)) {
            omsOrder.setStatus(OrderStatusEnum.PAYED.getCode());
            omsOrder.setTransactionId(result.getTransactionId());
            omsOrder.setPayTime(DateUtil.parseDateTime(result.getSuccessTime()));
            this.updateById(omsOrder);
            log.info("微信支付成功，【商户单号：{}】", result.getOutTradeNo());
        }
    }

    @Override
    public void wxRefundCallbackNotify(String data, HttpHeaders headers) {
        log.info("开始处理退款结果通知");
        WxPayRefundNotifyV3Result.DecryptNotifyResult result = map.get(PayOrgType.WX).refundCallbackNotify(null, data, headers);
        log.debug("退款通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getOutTradeNo, result.getOutTradeNo());
        OmsOrder omsOrder = this.getOne(wrapper);
        if (Objects.isNull(omsOrder)) {
            log.error("微信退款无法找到对应的订单，【商户订单号：{},退款号：{}】", result.getOutTradeNo(), result.getOutRefundNo());
            return;
        }
        if (Objects.equals(result.getRefundStatus(), WxPayConstants.RefundStatus.SUCCESS)) {
            omsOrder.setStatus(OrderStatusEnum.REFUNDED.getCode());
            omsOrder.setRefundId(result.getRefundId());
            this.updateById(omsOrder);
            log.info("微信退款成功，【单号：{}】", result.getOutTradeNo());
        }
    }

    @Override
    public void refund(String orderSn, Integer status) {
        OmsOrder omsOrder = getOrderByOrderSn(orderSn);
        Assert.isTrue(!Objects.equals(omsOrder.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode()), "该订单未支付，无需退款！");
        Assert.isTrue(!Objects.equals(omsOrder.getStatus(), OrderStatusEnum.REFUNDED.getCode()), "该订单已退款！");
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        PayTypeEnum payTypeEnum = PayTypeEnum.getByPayType(omsOrder.getPayType());
        String outRefundNo = map.get(payTypeEnum.getPayOrgType()).generateNo(memberId, payTypeEnum);
        String oldOutRefundNo = omsOrder.getOutRefundNo();
        // 如果用户的信誉积分很高就直接退款，否则就需要管理员审核后才能退款
        if (status == 1) {
            //订单价格小于20块就直接退款
            if (omsOrder.getPayAmount() < 100L * 20) {
                map.get(payTypeEnum.getPayOrgType()).refund(omsOrder.getOutTradeNo(), omsOrder.getOutRefundNo(), outRefundNo, omsOrder.getPayAmount(), "用户申请退款");
            } else {
                omsOrder.setStatus(OrderStatusEnum.APPLY_REFUND.getCode());
                this.updateById(omsOrder);
            }
        } else {
            map.get(payTypeEnum.getPayOrgType()).refund(omsOrder.getOutTradeNo(), oldOutRefundNo, outRefundNo, omsOrder.getPayAmount(), "用户申请退款");
        }
    }

    @Override
    public <T> T payQuery(String orderSn) {
        OmsOrder omsOrder = getOrderByOrderSn(orderSn);
        Assert.isTrue(StringUtils.isNotBlank(omsOrder.getOutTradeNo()), "该订单不存在商户单号！");
        return map.get(PayTypeEnum.getByPayType(omsOrder.getPayType()).getPayOrgType()).payQuery(omsOrder.getOutTradeNo());
    }

    @Override
    public <T> T refundQuery(String orderSn) {
        OmsOrder omsOrder = getOrderByOrderSn(orderSn);
        Assert.isTrue(StringUtils.isNotBlank(omsOrder.getOutTradeNo()), "该订单不存在商户单号！");
        Assert.isTrue(StringUtils.isNotBlank(omsOrder.getOutRefundNo()), "该订单不存在商户退款单号单号！");
        PayTypeEnum payType = PayTypeEnum.getByPayType(omsOrder.getPayType());
        return map.get(payType.getPayOrgType()).refundQuery(omsOrder.getOutRefundNo());
    }

    @Override
    public boolean orderClose(String orderSn) {
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getOrderSn, orderSn);
        OmsOrder omsOrder = this.getOne(wrapper);
        if (Objects.isNull(omsOrder)) {
            log.error("订单不存在，无需关闭，[订单编号：{}]", orderSn);
            return false;
        }
        if (!Objects.equals(omsOrder.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
            log.error("订单状态不是待支付的状态，无需关闭，[订单编号：{}]", orderSn);
            return false;
        }
        if (Objects.equals(omsOrder.getStatus(), OrderStatusEnum.AUTO_CANCEL.getCode())) {
            log.error("订单已关闭，无需再处理，[订单编号：{}]", orderSn);
            return false;
        }
        if (StringUtils.isNotBlank(omsOrder.getOutTradeNo())) {
            PayTypeEnum payType = PayTypeEnum.getByPayType(omsOrder.getPayType());
            map.get(payType.getPayOrgType()).close(omsOrder.getOutTradeNo());
        }
        omsOrder.setStatus(OrderStatusEnum.AUTO_CANCEL.getCode());
        return this.updateById(omsOrder);
    }


    private OmsOrder getOrderByOrderSn(String orderSn) {
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getOrderSn, orderSn);
        return Optional.ofNullable(this.getOne(wrapper)).orElseThrow(() -> new BusinessException("订单不存在！"));
    }
}
