package cn.zrj.payment.channel;

/**
 * @author zhaorujie
 * @version v1.0
 * @date 2023/4/23
 **/
public interface PaymentService {
    /**
     * 获取支付渠道
     * @return
     */
    String getPayChannel();

    boolean preCheck();

    Object pay();
}
