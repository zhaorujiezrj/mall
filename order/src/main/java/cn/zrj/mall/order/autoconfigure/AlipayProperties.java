package cn.zrj.mall.order.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@ConfigurationProperties(prefix = "ali.pay")
@Data
@Configuration
public class AlipayProperties {
    /**
     * 应用id，你的AppId，收款账号既是你的APPID对应支付宝账号
     */
    private String appId;
    /**
     * 商户私钥，你的pkcs8格式RSA2私钥
     */
    private String privateKey;
    /**
     * 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
     */
    private String alipayPublicKey;
    /**
     *  服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     *  支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
     */
    private String notifyUrl;
    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 同步通知，支付成功，一般跳转到成功页
     */
    private String returnUrl;
    /**
     * 签名方式
     */
    private String signType;
    /**
     * 字符编码格式
     */
    private String charset;
    /**
     * 订单超时时间
     */
    private String timeout;
    /**
     * 支付宝网关； https://openapi.alipaydev.com/gateway.do
     */
    private String gatewayUrl;
}
