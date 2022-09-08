package cn.zrj.mall.order.service;

import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.pay.enums.PayTypeEnum;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpHeaders;

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
     * 支付宝回调通知（下单和退款是同一个回调接口，只是回调的参数不一样）
     * @param request
     * @return
     */
    String alipayCallbackNotify(HttpServletRequest request) throws AlipayApiException;

    /**
     * 微信支付回调通知
     * @param data 回调加密数据
     * @param headers 签名请求头
     * @return
     */
    void wxPayCallbackNotify(String data, HttpHeaders headers);

    /**
     * 微信退款回调通知
     * @param data 回调加密数据
     * @param headers 签名请求头
     * @return
     */
    void wxRefundCallbackNotify(String data, HttpHeaders headers);

    /**
     * 订单退款
     * @param orderSn 订单编码
     * @param status 订单状态
     * @return
     */
    void refund(String orderSn, Integer status);

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

    /**
     * 订单取消
     * @param orderSn
     */
    boolean orderClose(String orderSn);
}
