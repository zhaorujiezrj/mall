package cn.zrj.mall.order.pay.constant;

/**
 * @author zhaorujie
 * @date 2022/9/8
 */
public class BeanNameConstants {

    private BeanNameConstants() {
        throw new IllegalStateException("BeanNameConstants class");
    }

    /**
     * 微信支付的实现类bean名称
     */
    public static final String WX = "WxPayService";

    /**
     * 支付宝支付的实现类bean名称
     */
    public static final String ALI = "AlipayService";

    /**
     * 其他支付的实现类bean名称
     */
    public static final String OTHER = null;
}
