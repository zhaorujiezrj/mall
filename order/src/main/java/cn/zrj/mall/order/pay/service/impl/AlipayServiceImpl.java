package cn.zrj.mall.order.pay.service.impl;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.autoconfigure.AlipayProperties;
import cn.zrj.mall.order.pay.constant.BeanNameConstants;
import cn.zrj.mall.order.pay.enums.AliTradeTypeEnum;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
@Service(BeanNameConstants.ALI)
@Slf4j
public class AlipayServiceImpl extends AbstractBasePayServiceImpl {

    private final AlipayClient alipayClient;

    private final AlipayProperties alipayProperties;

    public AlipayServiceImpl(AlipayClient alipayClient,
                             AlipayProperties alipayProperties) {
        this.alipayClient = alipayClient;
        this.alipayProperties = alipayProperties;
    }

    private static final EnumMap<PayTypeEnum, AliTradeTypeEnum> TRADE_TYPE_MAP = new EnumMap<>(PayTypeEnum.class);

    static {
        TRADE_TYPE_MAP.put(PayTypeEnum.ALI_APP, AliTradeTypeEnum.APP);
        TRADE_TYPE_MAP.put(PayTypeEnum.ALI_WAP, AliTradeTypeEnum.WAP);
        TRADE_TYPE_MAP.put(PayTypeEnum.ALI_PC, AliTradeTypeEnum.PC);
    }

    @Override
    public <T> T doPay(String oldOutTradeNo, String outTradeNo, Long payAmount, String description, String openid, PayTypeEnum payTypeEnum) {
        //如果已经有outTradeNo了就先进行关单
        if (StringUtils.isNotBlank(oldOutTradeNo)) {
            AlipayTradeQueryResponse queryResponse = doPayQuery(oldOutTradeNo);
            Assert.isTrue(!Objects.equals("TRADE_SUCCESS", queryResponse.getTradeStatus()), "该订单已支付");
            Assert.isTrue(!Objects.equals("TRADE_FINISHED", queryResponse.getTradeStatus()), "该订单已支付");

            doClose(oldOutTradeNo);
        }

        //把订单金额转为元（分->元）
        BigDecimal totalAmount = new BigDecimal(String.valueOf(payAmount)).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        //发起支付宝下单
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(totalAmount.toString());
        model.setSubject(description);

        try {
            AliTradeTypeEnum aliTradeTypeEnum = TRADE_TYPE_MAP.get(payTypeEnum);
            //app支付
            if (Objects.equals(aliTradeTypeEnum, AliTradeTypeEnum.APP)) {
                AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
                request.setNotifyUrl(alipayProperties.getNotifyUrl());
                request.setReturnUrl(alipayProperties.getReturnUrl());
                request.setBizModel(model);
                AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
                if (!response.isSuccess()) {
                    log.error("支付宝APP下单失败，【商户单号：{}，原因：{}，{}】", response.getOutTradeNo(), response.getMsg(), response.getSubMsg());
                    throw new BusinessException("支付宝APP支付失败");
                }
                return (T) response;
            }
            //手机网站支付
            else if (Objects.equals(aliTradeTypeEnum, AliTradeTypeEnum.WAP)) {
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                request.setNotifyUrl(alipayProperties.getNotifyUrl());
                request.setReturnUrl(alipayProperties.getReturnUrl());
                request.setBizModel(model);
                AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
                if (!response.isSuccess()) {
                    log.error("支付宝手机网站下单失败，【商户单号：{}，原因：{}，{}】", response.getOutTradeNo(), response.getMsg(), response.getSubMsg());
                    throw new BusinessException("支付宝手机网站下单支付失败");
                }
                //会跳到支付页面
                return (T) response.getBody();
            }
            //电脑PC支付
            else if(Objects.equals(aliTradeTypeEnum, AliTradeTypeEnum.PC)) {
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setNotifyUrl(alipayProperties.getNotifyUrl());
                request.setReturnUrl(alipayProperties.getReturnUrl());
                request.setBizModel(model);
                AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
                if (!response.isSuccess()) {
                    log.error("支付宝电脑PC下单失败，【商户单号：{}，原因：{}，{}】", response.getOutTradeNo(), response.getMsg(), response.getSubMsg());
                    throw new BusinessException("支付宝电脑PC支付失败");
                }
                //会跳到支付页面
                return (T) response.getBody();
            } else {
                throw new BusinessException("未匹配到相应的支付宝支付类型！");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝下单异常，【商户单号：{}，原因：{}，{}】", outTradeNo, e.getMessage(), e);
            throw new BusinessException("支付宝支付发送异常！");
        }
    }

    @Override
    public void doClose(String outTradeNo) {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(outTradeNo);
        request.setBizModel(model);

        try {
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            log.debug("支付宝关闭订单响应：{}",response.toString());
        } catch (AlipayApiException e) {
            log.error("支付宝关闭订单异常，商户单号：{}", outTradeNo, e);
            throw new BusinessException("支付宝关闭订单异常");
        }
    }

    @Override
    public void doRefund(String outTradeNo, String oldOutRefundNo, String outRefundNo, Long refundAmount, String reason) {

        if (StringUtils.isNotBlank(oldOutRefundNo)) {
            AlipayTradeFastpayRefundQueryResponse response = doRefundQuery(oldOutRefundNo);
            Assert.isTrue(!Objects.equals("REFUND_SUCCESS", response.getRefundStatus()), "该订单已退款！");
        }

        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo(outTradeNo);
        BigDecimal totalAmount = new BigDecimal(String.valueOf(refundAmount)).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        refundModel.setRefundAmount(totalAmount.toString());
        refundModel.setOutRequestNo(outRefundNo);
        refundModel.setRefundReason(reason);

        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizModel(refundModel);
        refundRequest.setNotifyUrl(alipayProperties.getNotifyUrl());

        try {
            AlipayTradeRefundResponse response = alipayClient.execute(refundRequest);
            log.debug("支付宝退款响应：{}", response.toString());
        } catch (AlipayApiException e) {
            log.error("支付宝退款发生异常，[商户单号：{}]", outTradeNo, e);
            throw new BusinessException("支付宝退款发生异常");
        }
    }

    @Override
    public <T> T doPayQuery(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        request.setBizModel(model);
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            return (T) response;
        } catch (AlipayApiException e) {
            log.error("支付宝下单查询发生异常，[商户单号：{}]", outTradeNo, e);
            throw new BusinessException("支付宝下单查询发生异常");
        }
    }

    @Override
    public <T> T doRefundQuery(String outRefundNo) {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutRequestNo(outRefundNo);
        request.setBizModel(model);
        try {
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            return (T) response;
        } catch (AlipayApiException e) {
            log.error("支付宝订单查询发生异常，[商户退款单号：{}]", outRefundNo, e);
            throw new BusinessException("支付宝订单查询发生异常");
        }
    }

    @Override
    public <T> T doPayCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return callbackNotify(request);
    }

    @Override
    public <T> T doRefundCallbackNotify(HttpServletRequest request, String data, HttpHeaders headers) {
        return callbackNotify(request);
    }

    private <T> T callbackNotify(HttpServletRequest request) {
        Map<String, String> params = this.sign(request);
        log.info("支付宝回调参数：[{}]", params);
        try {
            AlipayNotifyResponse response = new AlipayNotifyResponse();
            boolean signVerified = signVerified(params);
            response.setSignVerified(signVerified);
            if(signVerified) {
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

                response.setOutTradeNo(outTradeNo);
                response.setTradeNo(tradeNo);
                response.setTradeStatus(tradeStatus);
                response.setTotalAmount(totalAmount);
                response.setTotalFee(totalFee);
                response.setGmtPayment(gmtPayment);
                response.setRefundFee(refundFee);
                response.setGmtRefund(gmtRefund);
            }
            return (T) response;
        } catch (AlipayApiException e) {
            log.error("支付宝签名校验异常，原因：{}", e.getMessage(), e);
            throw new BusinessException("支付宝签名校验异常");
        }
    }

    private Map<String, String> sign(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        requestParams.forEach((key, values) -> {
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(key, valueStr);
        });
        return params;
    }

    private boolean signVerified(Map<String, String> params) throws AlipayApiException {
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        return AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), alipayProperties.getCharset(), "RSA2");
    }
}
