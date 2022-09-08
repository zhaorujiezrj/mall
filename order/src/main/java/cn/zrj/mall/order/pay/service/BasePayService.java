package cn.zrj.mall.order.pay.service;

import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
public interface BasePayService {

    <T> T doPay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum);

    void doClose(String outTradeNo);

    void doRefund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason);

    <T> T doPayQuery(String outTradeNo);

    <T> T doRefundQuery(String outRefundNo);

    <T> T doPayCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);

    <T> T doRefundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers);
}
