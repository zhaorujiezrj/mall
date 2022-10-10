package cn.zrj.mall.order.pay.core;

import cn.hutool.core.util.RandomUtil;
import cn.zrj.mall.order.pay.enums.PayOrgType;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author zhaorujie
 * @date 2022/10/9
 */
public abstract class AbstractPayService implements PayService {

    protected PayOrgType organizationType;

    protected AbstractPayService(PayOrgType organizationType) {
        this.organizationType = organizationType;
    }

    @Override
    public PayOrgType getOrganizationType() {
        return this.organizationType;
    }

    @Override
    public <T> T pay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum) {
        Assert.isTrue(StringUtils.isBlank(outTradeNo), "商户单号不能为空！");
        Assert.notNull(payAmount, "订单价格不能为空！");
        Assert.notNull(payTypeEnum, "支付类型不能为空！");
        return this.doPay(oldOutTradeNo, outTradeNo, payAmount, description, openid, payTypeEnum);
    }

    protected abstract <T> T doPay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum);

    @Override
    public void close(String outTradeNo) {
        Assert.isTrue(StringUtils.isBlank(outTradeNo), "商户单号不能为空！");
        this.doClose(outTradeNo);
    }

    protected abstract void doClose(String outTradeNo);

    @Override
    public void refund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason) {
        Assert.isTrue(StringUtils.isBlank(outTradeNo), "商户单号不能为空！");
        Assert.isTrue(StringUtils.isBlank(outRefundNo), "商户退款单号不能为空！");
        Assert.notNull(refundAmount, "退款价格不能为空！");
        this.doRefund(outRefundNo, oldOutRefundNo, outRefundNo, refundAmount, reason);
    }

    protected abstract void doRefund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason);

    @Override
    public <T> T payQuery(String outTradeNo) {
        Assert.isTrue(StringUtils.isBlank(outTradeNo), "商户单号不能为空！");
        return this.doPayQuery(outTradeNo);
    }

    protected abstract <T> T doPayQuery(String outTradeNo);

    @Override
    public <T> T refundQuery(String outRefundNo) {
        Assert.isTrue(StringUtils.isBlank(outRefundNo), "商户退款单号不能为空！");
        return this.doRefundQuery(outRefundNo);
    }

    protected abstract <T> T doRefundQuery(String outRefundNo);

    @Override
    public String generateNo(Long memberId, PayTypeEnum payTypeEnum) {
        String mechanism;
        if (payTypeEnum.getPayOrgType().equals(PayOrgType.WX)) {
            mechanism = "wx";
        } else if (payTypeEnum.getPayOrgType().equals(PayOrgType.ALI)) {
            mechanism = "ali";
        } else {
            mechanism = "other";
        }
        return generate(mechanism, memberId);
    }

    @Override
    public <T> T payCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return this.doPayCallbackNotify(request, data, headers);
    }

    protected abstract <T> T doPayCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);

    @Override
    public <T> T refundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return this.doRefundCallbackNotify(request, data, headers);
    }

    protected abstract <T> T doRefundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);

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
