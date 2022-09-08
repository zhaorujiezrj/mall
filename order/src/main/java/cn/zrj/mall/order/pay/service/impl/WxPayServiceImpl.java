package cn.zrj.mall.order.pay.service.impl;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.autoconfigure.WxPayProperties;
import cn.zrj.mall.order.pay.constant.BeanNameConstants;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumMap;
import java.util.Objects;


/**
 * @author zhaorujie
 * @date 2022/9/7
 */
@Slf4j
@Service(BeanNameConstants.WX)
public class WxPayServiceImpl extends AbstractBasePayServiceImpl {

    private final WxPayService wxPayService;

    private final WxPayProperties wxPayProperties;

    public WxPayServiceImpl(WxPayService wxPayService,
                            WxPayProperties wxPayProperties) {
        this.wxPayService = wxPayService;
        this.wxPayProperties = wxPayProperties;
    }

    private static final EnumMap<PayTypeEnum, TradeTypeEnum> TRADE_TYPE_MAP = new EnumMap<>(PayTypeEnum.class);

    static {
        TRADE_TYPE_MAP.put(PayTypeEnum.WX_JSAPI, TradeTypeEnum.JSAPI);
        TRADE_TYPE_MAP.put(PayTypeEnum.WX_APP, TradeTypeEnum.APP);
        TRADE_TYPE_MAP.put(PayTypeEnum.WX_H5, TradeTypeEnum.H5);
        TRADE_TYPE_MAP.put(PayTypeEnum.WX_NATIVE, TradeTypeEnum.NATIVE);
    }

    @Override
    public <T> T doPay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum) {
        //如果已经有oldOutTradeNo了就先进行关单
        if (StringUtils.isNotBlank(oldOutTradeNo)) {
            WxPayOrderQueryV3Result result = doPayQuery(outTradeNo);
            Assert.isTrue(!Objects.equals(result.getTradeState(), WxPayConstants.WxpayTradeStatus.SUCCESS), "该订单已支付！");

            doClose(oldOutTradeNo);
        }
        //发起微信支付
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request()
                .setOutTradeNo(outTradeNo)
                .setAppid(wxPayProperties.getAppid())
                .setNotifyUrl(wxPayProperties.getPayNotifyUrl())
                .setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(Math.toIntExact(payAmount)))
                .setDescription(description);

        //jsapi支付才需要openId
        if (StringUtils.isNotBlank(openid)) {
            wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(openid));
        }
        try {
            return wxPayService.createOrderV3(TRADE_TYPE_MAP.get(payTypeEnum), wxPayUnifiedOrderV3Request);
        } catch (WxPayException e) {
            log.error("微信下单异常,[商户单号：{}, 原因：{}]", outTradeNo, e.getMessage(), e);
            throw new BusinessException("微信下单异常！");
        }
    }

    @Override
    public void doClose(String outTradeNo) {
        try {
            wxPayService.closeOrderV3(outTradeNo);
            log.debug("微信关单成功,商户单号：{}", outTradeNo);
        } catch (WxPayException e) {
            log.error("微信关单异常，原因：{}", e.getMessage(), e);
            throw new BusinessException("微信关单异常");
        }
    }

    @Override
    public void doRefund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason) {
        if (StringUtils.isNotBlank(oldOutRefundNo)) {
            WxPayRefundQueryV3Result result = doRefundQuery(oldOutRefundNo);
            Assert.isTrue(!Objects.equals(result.getStatus(), WxPayConstants.WxpayTradeStatus.SUCCESS), "该订单已退款，请刷新");
        }

        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setAmount(new WxPayRefundV3Request.Amount().setTotal(refundAmount.intValue()));
        request.setNotifyUrl(wxPayProperties.getRefundNotifyUrl());
        request.setOutTradeNo(outTradeNo);
        request.setReason(reason);

        request.setOutRefundNo(outRefundNo);
        try {
            wxPayService.refundV3(request);
        } catch (WxPayException e) {
            log.error("微信退款发生异常，[商户订单号：{}]", outTradeNo, e);
            throw new BusinessException("微信退款发生异常");
        }
    }

    @Override
    public <T> T doPayQuery(String outTradeNo) {
        try {
            WxPayOrderQueryV3Result result = wxPayService.queryOrderV3(new WxPayOrderQueryV3Request().setOutTradeNo(outTradeNo));
            return (T) result;
        } catch (WxPayException e) {
            log.error("微信订单查询发生异常，[商户单号：{}]", outTradeNo, e);
            throw new BusinessException("微信订单查询发生异常");
        }
    }

    @Override
    public <T> T doRefundQuery(String outRefundNo) {
        try {
            WxPayRefundQueryV3Result result = wxPayService.refundQueryV3(outRefundNo);
            return (T) result;
        } catch (WxPayException e) {
            log.error("微信订单查询发生异常，[商户退款单号：{}]", outRefundNo, e);
            throw new BusinessException("微信订单查询发生异常");
        }
    }

    @Override
    public <T> T doPayCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        try {
            WxPayOrderNotifyV3Result.DecryptNotifyResult result = wxPayService.parseOrderNotifyV3Result(data, this.getSignatureHeader(headers)).getResult();
            return (T) result;
        } catch (WxPayException e) {
            log.error("微信支付通知解密失败，原因：{}", e.getMessage());
            throw new BusinessException("微信支付通知解密失败");
        }
    }

    @Override
    public <T> T doRefundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        try {
            WxPayRefundNotifyV3Result.DecryptNotifyResult result = wxPayService.parseRefundNotifyV3Result(data, this.getSignatureHeader(headers)).getResult();
            return (T) result;
        } catch (WxPayException e) {
            log.error("微信退款通知解密失败，原因：{}", e.getMessage());
            throw new BusinessException("微信退款通知解密失败");
        }
    }

    private SignatureHeader getSignatureHeader(HttpHeaders headers) {
        com.github.binarywang.wxpay.bean.notify.SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(headers.getFirst("Wechatpay-Signature"));
        signatureHeader.setNonce(headers.getFirst("Wechatpay-Nonce"));
        signatureHeader.setSerial(headers.getFirst("Wechatpay-Serial"));
        signatureHeader.setTimeStamp(headers.getFirst("Wechatpay-Timestamp"));
        return signatureHeader;
    }
}
