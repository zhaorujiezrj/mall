package cn.zrj.mall.order.pay.service.impl;

import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import cn.zrj.mall.order.pay.service.BasePayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
@Slf4j
public abstract class AbstractBasePayServiceImpl implements BasePayService {

    @Override
    public <T> T doPay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum) {
        return null;
    }

    @Override
    public void doClose(String outTradeNo) {

    }

    @Override
    public void doRefund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason) {

    }

    @Override
    public <T> T doPayQuery(String outTradeNo) {
        return null;
    }

    @Override
    public <T> T doRefundQuery(String outRefundNo) {
        return null;
    }

    @Override
    public <T> T doPayCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return null;
    }

    @Override
    public <T> T doRefundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return null;
    }
}
