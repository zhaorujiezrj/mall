package cn.zrj.mall.order.pay.core;

import cn.zrj.mall.order.pay.enums.PayOrgType;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
public interface PayService {
    /**
     * 获取支付机构类型
     * @return
     */
    PayOrgType getOrganizationType();

    /**
     * 支付
     * @param oldOutTradeNo 旧商户单号
     * @param outTradeNo 商户单号
     * @param payAmount 支付金额（单位：分）
     * @param description 支付描述
     * @param openid 微信的openId（小程序，公众号等）
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return 返回是一个object对象，具体返回值请看实现
     */
    <T> T pay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum);

    /**
     * 关闭订单
     * @param outTradeNo 商户单号
     */
    void close(String outTradeNo);

    /**
     * 退款
     * @param outTradeNo 商户单号
     * @param oldOutRefundNo 旧的商户退款单号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额（单位：分）
     * @param reason 退款原因
     */
    void refund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason);

    /**
     * 支付单查询
     * @param outTradeNo 商户单号
     * @param <T> 返回类型 微信：WxPayOrderQueryV3Result，支付宝：AlipayTradeQueryResponse
     * @return 返回是一个object对象，具体返回值请看实现
     */
    <T> T payQuery(String outTradeNo);

    /**
     * 退款单查询
     * @param outRefundNo 商户退款单号
     * @param <T> 返回类型 微信：WxPayRefundQueryV3Result，支付宝：AlipayTradeFastpayRefundQueryResponse
     * @return 返回是一个object对象，具体返回值请看实现
     */
    <T> T refundQuery(String outRefundNo);

    /**
     * 生成商户单号和退款单号
     * @param memberId 用户id
     * @param payTypeEnum 支付类型枚举对象
     * @return 返回单号
     */
    String generateNo(Long memberId, PayTypeEnum payTypeEnum);

    /**
     * 支付回调通知
     * @param request http请求（支付宝）
     * @param data 加密数据（微信）
     * @param headers 请求头（微信）
     * @param <T> 返回类型 微信：WxPayOrderNotifyV3Result.DecryptNotifyResult，支付宝：AlipayNotifyResponse
     * @return 返回是一个object对象，具体返回值请看实现
     */
    <T> T payCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);

    /**
     * 退款回调通知
     * @param request http请求（支付宝）
     * @param data 加密数据（微信）
     * @param headers 请求头（微信）
     * @param <T> 返回类型 微信：WxPayRefundNotifyV3Result.DecryptNotifyResult，支付宝：AlipayNotifyResponse
     * @return 返回是一个object对象，具体返回值请看实现
     */
    <T> T refundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);
}
