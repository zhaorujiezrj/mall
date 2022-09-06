package cn.zrj.mall.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import cn.zrj.mall.common.core.enums.OutRefundNoTypeEnum;
import cn.zrj.mall.common.core.enums.OutTradeNoTypeEnum;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.security.UserContextHolder;
import cn.zrj.mall.common.core.util.BusinessNoUtils;
import cn.zrj.mall.order.autoconfigure.AlipayProperties;
import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import cn.zrj.mall.order.autoconfigure.WxPayProperties;
import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.enums.AlipayStatusEnum;
import cn.zrj.mall.order.enums.OrderStatusEnum;
import cn.zrj.mall.order.enums.PayTypeEnum;
import cn.zrj.mall.order.feign.MemberFeignClient;
import cn.zrj.mall.order.mapper.OrderMapper;
import cn.zrj.mall.order.service.OrderService;
import cn.zrj.mall.order.util.RocketMQUtils;
import cn.zrj.mall.order.vo.WxPayNotifyResponseVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
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
    private WxPayService wxPayService;
    @Autowired
    private WxPayProperties wxPayProperties;
    @Autowired
    private MemberFeignClient memberFeignClient;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayProperties alipayProperties;


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
    public <T> T pay(String orderNo, Integer payType) {
        PayTypeEnum payTypeEnum = PayTypeEnum.getValue(payType);
        if (payTypeEnum == null) {
            throw new BusinessException("系统暂不支持该支付方式!");
        }
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderSn, orderNo);
        Order order = getOne(wrapper);
        Assert.isTrue(order != null, "订单不存在！");
        Assert.isTrue(Objects.equals(OrderStatusEnum.PENDING_PAYMENT.getCode(), order.getStatus()), "订单不可支付，请刷新查看订单状态");

        RLock lock = redissonClient.getLock(RedisConstants.ORDER_SN_PREFIX + orderNo);
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
        T result = null;
        switch (payTypeEnum) {
            case WX_JSAPI:
                result = wxPay(order, payTypeEnum, TradeTypeEnum.JSAPI);
                break;
            case WX_APP:
                result = wxPay(order, payTypeEnum, TradeTypeEnum.APP);
                break;
            case WX_H5:
                result = wxPay(order, payTypeEnum, TradeTypeEnum.H5);
                break;
            case WX_NATIVE:
                result = wxPay(order, payTypeEnum, TradeTypeEnum.NATIVE);
                break;
            case ALIPAY:
                result = (T) aliPay(order);
                break;
            case BALANCE:
                break;
            default:
                throw new BusinessException("暂不支持该类型的支付，请重新选择！");
        }
        return result;
    }

    @Override
    public <T> T wxPay(Order order, PayTypeEnum payTypeEnum, TradeTypeEnum tradeTypeEnum) {
        //如果已经有outTradeNo了就先进行关单
        if (StringUtils.isNotBlank(order.getOutTradeNo())) {
            try {
                wxPayService.closeOrderV3(order.getOutTradeNo());
            } catch (WxPayException e) {
                log.error("微信关单失败，原因：{}", e.getMessage());
                throw new BusinessException("微信关单异常");
            }
        }
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outTradeNo = BusinessNoUtils.generateOutTradeNo(OutTradeNoTypeEnum.WX_PAY, memberId);
        log.info("商户单号为：{}", outTradeNo);
        //更新支付类型
        order.setPayType(payTypeEnum.getValue());
        order.setOutTradeNo(outTradeNo);
        this.updateById(order);

        //发起微信支付
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request()
                .setOutTradeNo(outTradeNo)
                .setAppid(wxPayProperties.getAppid())
                .setNotifyUrl(wxPayProperties.getPayNotifyUrl())
                .setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(Math.toIntExact(order.getPayAmount())))
                .setDescription("赅买-订单编号" + order.getOrderSn());

        //jsapi支付才需要openId
        if (Objects.equals(tradeTypeEnum,TradeTypeEnum.JSAPI)) {
            //获取用户的openid
            String openid = memberFeignClient.getOpenidById(memberId).getData();
            wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(openid));
        }
        try {
            return wxPayService.createOrderV3(tradeTypeEnum, wxPayUnifiedOrderV3Request);
        } catch (WxPayException e) {
            log.error("微信下单异常,[商户单号：{}, 原因：{}]", outTradeNo, e.getMessage(), e);
            throw new BusinessException("微信下单异常！");
        }
    }

    @Override
    public AlipayTradePagePayResponse aliPay(Order order) {
        //把订单金额转为元（分->元）
        BigDecimal payAmount = new BigDecimal(String.valueOf(order.getPayAmount())).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        //如果已经有outTradeNo了就先进行关单
        if (StringUtils.isNotBlank(order.getOutTradeNo())) {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(order.getOutTradeNo());
            model.setRefundAmount(payAmount.toString());
            request.setBizContent(JSONUtil.toJsonStr(model));
            try {
                AlipayTradeRefundResponse response = alipayClient.pageExecute(request);
                if (!response.isSuccess()) {
                    log.error("支付宝关闭异常,【商户单号：{}, 原因：{},{}】", order.getOrderSn(), response.getMsg(), response.getSubMsg());
                    throw new BusinessException("支付宝关闭异常！");
                }
            } catch (AlipayApiException e) {
                log.error("支付宝关单失败，原因：{}", e.getMessage());
                throw new BusinessException("支付宝关单异常");
            }
        }

        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outTradeNo = BusinessNoUtils.generateOutTradeNo(OutTradeNoTypeEnum.ALIPAY, memberId);
        log.info("商户单号为：{}", outTradeNo);

        order.setOutRefundNo(outTradeNo);
        order.setPayType(PayTypeEnum.ALIPAY.getValue());
        this.updateById(order);

        //发起支付宝下单
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        payRequest.setNotifyUrl(alipayProperties.getNotifyUrl());
        payRequest.setReturnUrl(alipayProperties.getReturnUrl());
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(payAmount.toString());
        model.setSubject("赅买-订单编号" + order.getOrderSn());

        payRequest.setBizModel(model);
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(payRequest);
            if (!response.isSuccess()) {
                log.error("支付宝下单失败，【商户单号：{}，原因：{}，{}】", response.getOutTradeNo(), response.getMsg(), response.getSubMsg());
                throw new BusinessException("支付宝支付失败");
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("支付宝下单异常，【商户单号：{}，原因：{}，{}】", order.getOutTradeNo(), e.getMessage(), e);
            throw new BusinessException("支付宝支付发送异常！");
        }
    }

    @Override
    public String alipayCallbackNotify(HttpServletRequest request) throws AlipayApiException {
        Map<String, String> params = sign(request);
        log.info("支付宝回调参数：[{}]", params);
        boolean signVerified = signVerified(params);
        //商户订单号
        String outTradeNo = params.get("out_trade_no");
        //支付宝订单号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        //交易金额
        String totalAmount = params.get("total_amount");
        String totalFee = params.get("total_fee");
        //支付时间
        String gmtPayment = params.get("gmt_payment");
        //退款金额
        String refundFee = params.get("refund_fee");
        //退款时间
        String gmtRefund = params.get("gmt_refund");
        if (signVerified) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOutTradeNo, outTradeNo);
            Order order = this.getOne(wrapper);
            if (Objects.isNull(order)) {
                log.error("找不到对应的商户单号：{}", outTradeNo);
                return "success";
            }
            //如果有退款金额就是退款通知
            if (StringUtils.isNotBlank(refundFee)) {
                //如果是已退款了就不用再处理了
                if (Objects.equals(tradeStatus, AlipayStatusEnum.TRADE_SUCCESS.getValue())
                        && !Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    order.setRefundId(tradeNo);
                    order.setStatus(OrderStatusEnum.REFUNDED.getCode());
                    this.updateById(order);
                }
            } else {
                //支付通知
                if (Objects.equals(tradeStatus, AlipayStatusEnum.TRADE_SUCCESS.getValue())
                        && Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
                    order.setStatus(OrderStatusEnum.PAYED.getCode());
                    order.setTransactionId(tradeNo);
                    order.setPayTime(DateUtil.parseDateTime(gmtPayment));
                    this.updateById(order);
                }
            }
            return "success";
        } else {
            log.error("支付宝验签失败,商户单号:[{}]", outTradeNo);
            return "failure";
        }
    }

    @Override
    public WxPayNotifyResponseVo wxPayCallbackNotify(String data, SignatureHeader signatureHeader) {
        WxPayNotifyResponseVo wxPayNotifyResponseVo = new WxPayNotifyResponseVo();
        WxPayOrderNotifyV3Result.DecryptNotifyResult result;
        try {
            result = wxPayService.parseOrderNotifyV3Result(data, signatureHeader).getResult();
        } catch (WxPayException e) {
            log.error("微信支付通知解密失败，原因：{}", e.getMessage());
            wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.FAIL)
                    .setMessage("失败");
            return wxPayNotifyResponseVo;
        }
        log.debug("支付通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = this.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("无法找到对应的支付订单，商户单号：【{}】", result.getOutTradeNo());
            wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
            return wxPayNotifyResponseVo;
        }
        if (Objects.equals(order.getStatus(), OrderStatusEnum.PAYED.getCode())) {
            log.error("订单已完成支付，无需继续处理，【商户单号：{}】", result.getOutTradeNo());
            wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
            return wxPayNotifyResponseVo;
        }
        if (Objects.equals(result.getTradeState(), WxPayConstants.WxpayTradeStatus.SUCCESS)
                && Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            order.setTransactionId(result.getTransactionId());
            order.setPayTime(DateUtil.parseDateTime(result.getSuccessTime()));
            this.updateById(order);
            log.info("微信支付成功，【商户单号：{}】", result.getOutTradeNo());
        }
        wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.SUCCESS)
                .setMessage("成功");
        return wxPayNotifyResponseVo;
    }

    @Override
    public WxPayNotifyResponseVo wxRefundCallbackNotify(String data, SignatureHeader signatureHeader) {
        WxPayNotifyResponseVo wxPayNotifyResponseVo = new WxPayNotifyResponseVo();
        WxPayRefundNotifyV3Result.DecryptNotifyResult result;
        try {
            result = wxPayService.parseRefundNotifyV3Result(data, signatureHeader).getResult();
        } catch (WxPayException e) {
            log.error("微信退款通知解密失败，原因：{}", e.getMessage());
            wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.FAIL)
                    .setMessage("失败");
            return wxPayNotifyResponseVo;
        }
        log.debug("退款通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = this.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("微信退款无法找到对应的订单，【商户订单号：{},退款号：{}】", result.getOutTradeNo(), result.getOutRefundNo());
            wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
            return wxPayNotifyResponseVo;
        }
        if (Objects.equals(result.getRefundStatus(), WxPayConstants.RefundStatus.SUCCESS)) {
            order.setStatus(OrderStatusEnum.REFUNDED.getCode());
            order.setRefundId(result.getRefundId());
            this.updateById(order);
            log.info("微信退款成功，【单号：{}】", result.getOutTradeNo());
        }
        wxPayNotifyResponseVo.setCode(WxPayConstants.ResultCode.SUCCESS)
                .setMessage("成功");
        return wxPayNotifyResponseVo;
    }

    @Override
    public <T> T refund(String orderSn, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderSn, orderSn);
        Order order = Optional.ofNullable(this.getOne(wrapper)).orElseThrow(() -> new BusinessException("订单不存在！"));
        Assert.isTrue(!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode()), "该订单未支付，无需退款！");
        Assert.isTrue(!Objects.equals(order.getStatus(), OrderStatusEnum.REFUNDED.getCode()), "该订单已退款！");
        // 如何用户的信誉积分很高就直接退款，否则就需要管理员审核后才能退款
        if (status == 1) {
            //订单价格小于20块就直接退款
            if (order.getPayAmount() < 100L * 20) {
                return payRefund(order);
            } else {
                order.setStatus(OrderStatusEnum.APPLY_REFUND.getCode());
                this.updateById(order);
                return null;
            }
        } else {
            return payRefund(order);
        }
    }

    private <T> T payRefund(Order order) {
        T result = null;
        switch (Objects.requireNonNull(PayTypeEnum.getValue(order.getPayType()))) {
            case WX_JSAPI:
            case WX_APP:
            case WX_H5:
            case WX_NATIVE:
                result = (T) this.wxPayRefund(order);
                break;
            case ALIPAY:
                result = (T) this.alipayRefund(order);
                break;
            case BALANCE:
                break;
            default:
                throw new BusinessException("不支持该类型的退款！");
        }
        return result;
    }

    private WxPayRefundV3Result wxPayRefund(Order order) {
        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setAmount(new WxPayRefundV3Request.Amount().setTotal(Math.toIntExact(order.getPayAmount())));
        request.setNotifyUrl(wxPayProperties.getRefundNotifyUrl());
        request.setOutTradeNo(order.getOutTradeNo());
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outRefundNo = BusinessNoUtils.generateOutRefundNo(OutRefundNoTypeEnum.WX_REFUND, memberId);
        request.setOutRefundNo(outRefundNo);

        order.setOutRefundNo(outRefundNo);
        this.updateById(order);
        try {
            return wxPayService.refundV3(request);
        } catch (WxPayException e) {
            log.error("微信退款发生异常，[商户订单号：{}]", order.getOutTradeNo(), e);
            throw new BusinessException("微信退款发生异常");
        }
    }

    private AlipayTradeRefundResponse alipayRefund(Order order) {
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo(order.getOutTradeNo());
        BigDecimal refundAmount = new BigDecimal(String.valueOf(order.getPayAmount())).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        refundModel.setRefundAmount(refundAmount.toString());
        Long memberId = UserContextHolder.getPayloadToken().getMemberId();
        String outRefundNo = BusinessNoUtils.generateOutRefundNo(OutRefundNoTypeEnum.ALI_REFUND, memberId);
        refundModel.setOutRequestNo(outRefundNo);

        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizModel(refundModel);
        refundRequest.setNotifyUrl(alipayProperties.getNotifyUrl());

        order.setOutRefundNo(outRefundNo);
        this.updateById(order);
        try {
            return alipayClient.pageExecute(refundRequest);
        } catch (AlipayApiException e) {
            log.error("支付宝退款发生异常，[商户单号：{}]", order.getOutTradeNo(), e);
            throw new BusinessException("支付宝退款发生异常");
        }

    }

    private Map<String, String> sign(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        return params;
    }

    private boolean signVerified(Map<String, String> params) throws AlipayApiException {
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        return AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), alipayProperties.getCharset(), "RSA2");
    }
}
