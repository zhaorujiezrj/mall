package cn.zrj.mall.order.pay.service;

import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
public interface PayService {
    /**
     * 支付
     * @param oldOutTradeNo 旧商户单号
     * @param outTradeNo 商户单号
     * @param payAmount 支付金额（单位：分）
     * @param description 支付描述
     * @param openid 微信的openId（小程序，公众号等）
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return
     */
    <T> T pay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum);

    /**
     * 关闭订单
     * @param outTradeNo 商户单号
     * @param payTypeEnum 支付类型枚举对象
     */
    void close(String outTradeNo, PayTypeEnum payTypeEnum);

    /**
     * 退款
     * @param outTradeNo 商户单号
     * @param oldOutRefundNo 旧的商户退款单号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额（单位：分）
     * @param reason 退款原因
     * @param payTypeEnum 支付类型枚举对象
     */
    void refund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason, PayTypeEnum payTypeEnum);

    /**
     * 支付单查询
     * @param outTradeNo 商户单号
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return
     */
    <T> T payQuery(String outTradeNo, PayTypeEnum payTypeEnum);

    /**
     * 退款单查询
     * @param outRefundNo 商户退款单号
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return
     */
    <T> T refundQuery(String outRefundNo, PayTypeEnum payTypeEnum);

    /**
     * 生产商户单号和退款单号
     * @param memberId 用户id
     * @param payTypeEnum 支付类型枚举对象
     * @return
     */
    String generateNo(Long memberId, PayTypeEnum payTypeEnum);

    /**
     * 支付回调通知
     * @param request http请求（支付宝）
     * @param data 加密数据（微信）
     * @param headers 请求头（微信）
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return
     */
    <T> T payCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers, PayTypeEnum payTypeEnum);

    /**
     * 退款回调通知
     * @param request http请求（支付宝）
     * @param data 加密数据（微信）
     * @param headers 请求头（微信）
     * @param payTypeEnum 支付类型枚举对象
     * @param <T> 返回类型
     * @return
     */
    <T> T refundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers, PayTypeEnum payTypeEnum);
}