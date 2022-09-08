package cn.zrj.mall.order.pay.result;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
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

    public boolean isSignVerified() {
        return signVerified;
    }

    public void setSignVerified(boolean signVerified) {
        this.signVerified = signVerified;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(String gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    public String getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(String gmtRefund) {
        this.gmtRefund = gmtRefund;
    }
}
