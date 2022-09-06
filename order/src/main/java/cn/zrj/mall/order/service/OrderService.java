package cn.zrj.mall.order.service;

import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.enums.PayTypeEnum;
import cn.zrj.mall.order.vo.WxPayNotifyResponseVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
public interface OrderService extends IService<Order> {
    /**
     * 创建订单
     */
    void createOrder();

    /**
     * 订单支付
     * @param orderSn 订单编号
     * @param payType 支付类型
     * @param <T>
     * @return
     */
    <T> T pay(String orderSn, Integer payType);

    /**
     * 订单支付
     * @param order 订单实体
     * @param payTypeEnum 支付类型
     * @param <T> 返回的实体类型
     * @return
     */
    <T> T pay(Order order, PayTypeEnum payTypeEnum);

    /**
     * 微信支付
     * @param order 订单实体
     * @param payTypeEnum 支付类型
     * @param tradeTypeEnum 微信支付类型
     * @param <T> 返回的实体类型
     * @return
     */
    <T> T wxPay(Order order, PayTypeEnum payTypeEnum, TradeTypeEnum tradeTypeEnum);

    /**
     * 支付宝下单
     * @param order 订单实体
     * @return
     */
    AlipayTradePagePayResponse aliPay(Order order);

    /**
     * 支付宝回调通知（下单和退款是同一个回调接口，只是回调的参数不一样）
     * @param request
     * @return
     */
    String alipayCallbackNotify(HttpServletRequest request) throws AlipayApiException;

    /**
     * 微信支付回调通知
     * @param data 回调加密数据
     * @param signatureHeader 签名请求头
     * @return
     */
    WxPayNotifyResponseVo wxPayCallbackNotify(String data, SignatureHeader signatureHeader);

    /**
     * 微信退款回调通知
     * @param data 回调加密数据
     * @param signatureHeader 签名请求头
     * @return
     */
    WxPayNotifyResponseVo wxRefundCallbackNotify(String data, SignatureHeader signatureHeader);

    /**
     * 订单退款
     * @param orderSn 订单编码
     * @param status 订单状态
     * @param <T> 返回实体类型
     * @return
     */
    <T> T refund(String orderSn, Integer status);

    /**
     * 支付查询
     * @param orderSn
     * @param <T>
     * @return
     */
    <T> T payQuery(String orderSn);

    /**
     * 退款查询
     * @param orderSn
     * @param <T>
     * @return
     */
    <T> T refundQuery(String orderSn);
}
