package cn.zrj.mall.order.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
@Data
public class WxPayProperties {
    /**
     * 微信支付服务商号
     */
    private String mchId;
    /**
     * 微信支付商户密钥
     */
    private String mchKey;
    /**
     * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对项删除
     */
    private String subAppId;
    /**
     * 服务商模式下的子商户号，普通模式请不要配置，请在配置文件中将对项删除
     */
    private String subMchId;
    /**
     * api client_key.p12 文件的绝对路径或者以classpath:开头的类路径
     */
    private String keyPath;
    /**
     * api client_key.pem 证书文件的绝对路径或者以classpath:开头的类路径
     */
    private String privateKeyPath;
    /**
     * api client_cert.pem 证书文件的绝对路径或者以classpath:开头的类路径
     */
    private String privateCertPath;
    /**
     * apiV3 秘钥值
     */
    private String apiV3Key;
    /**
     * apiV3 证书序列号值
     */
    private String certSerialNo;
    /**
     * 是否为沙箱环境
     */
    private Boolean sandboxEnabled;
    /**
     * 支付通知url
     */
    private String payNotifyUrl;
    /**
     * 退款通知url
     */
    private String refundNotifyUrl;
}
