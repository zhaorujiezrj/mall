package cn.zrj.mall.order.pay.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import cn.zrj.mall.order.pay.service.BasePayService;
import cn.zrj.mall.order.pay.service.PayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
@Service
public class PayServiceImpl implements PayService {

    private final ApplicationContext applicationContext;

    public PayServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T pay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum) {
        return getPayService(payTypeEnum).doPay(oldOutTradeNo, outTradeNo, payAmount, description, openid, payTypeEnum);
    }

    @Override
    public void close(String outTradeNo, PayTypeEnum payTypeEnum) {
        getPayService(payTypeEnum).doClose(outTradeNo);
    }

    @Override
    public void refund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason, PayTypeEnum payTypeEnum) {
        getPayService(payTypeEnum).doRefund(outTradeNo, oldOutRefundNo, outRefundNo, refundAmount, reason);
    }

    @Override
    public <T> T payQuery(String outTradeNo, PayTypeEnum payTypeEnum) {
        return getPayService(payTypeEnum).doPayQuery(outTradeNo);
    }

    @Override
    public <T> T refundQuery(String outRefundNo, PayTypeEnum payTypeEnum) {
        return getPayService(payTypeEnum).doRefundQuery(outRefundNo);
    }

    @Override
    public String generateNo(Long memberId, PayTypeEnum payTypeEnum) {
        BasePayService payService = getPayService(payTypeEnum);
        String mechanism;
        if (payService instanceof WxPayServiceImpl) {
            mechanism = "wx";
        } else if (payService instanceof AlipayServiceImpl) {
            mechanism = "ali";
        } else {
            mechanism = "other";
        }
        return generate(mechanism, memberId);
    }

    @Override
    public <T> T payCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers, PayTypeEnum payTypeEnum) {
        return getPayService(payTypeEnum).doPayCallbackNotify(request, data, headers);
    }

    @Override
    public <T> T refundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers, PayTypeEnum payTypeEnum) {
        return getPayService(payTypeEnum).doRefundCallbackNotify(request, data, headers);
    }

    private BasePayService getPayService(PayTypeEnum payTypeEnum) {
        Assert.isTrue(Objects.nonNull(payTypeEnum), "暂不支持改支付类型！");
        Assert.isTrue(StringUtils.isNotBlank(payTypeEnum.getBeanName()), "暂不支持改支付类型！");
        return (BasePayService) applicationContext.getBean(payTypeEnum.getBeanName());
    }

    private String generate(String mechanism, Long memberId) {
        // 用户id前补零保证五位，对超出五位的保留后五位
        String userIdFilledZero = String.format("%05d", memberId);
        String fiveDigitsUserId = userIdFilledZero.substring(userIdFilledZero.length() - 5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        // 在前面加上wxo（weixin order）等前缀是为了人工可以快速分辨订单号是下单还是退款、来自哪家支付机构等
        // 将时间格式化（yyyyMMddHHmmss）+3位随机数+五位id组成商户订单号，规则参考自<a href="https://tech.meituan.com/2016/11/18/dianping-order-db-sharding.html">大众点评</a>
        return mechanism + dateTime + RandomUtil.randomNumbers(3) + fiveDigitsUserId ;
    }
}
