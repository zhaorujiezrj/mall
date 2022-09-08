package cn.zrj.mall.order.pay.service.impl;

import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
@Data
public class AlipayNotifyResponse {
    /**
     * 签名校验
     */
    private boolean signVerified;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 支付宝订单号
     */
    private String tradeNo;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 交易金额
     */
    private String totalAmount;
    private String totalFee;
    /**
     * 支付时间
     */
    private String gmtPayment;
    /**
     * 退款金额
     */
    private String refundFee;
    /**
     * 退款时间
     */
    private String gmtRefund;
}
