package cn.zrj.mall.order.pay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@ConfigurationProperties(prefix = "wx.pay")
@Configuration
@Primary
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
    /**
     * 小程序appid
     */
    private String appid;

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateCertPath() {
        return privateCertPath;
    }

    public void setPrivateCertPath(String privateCertPath) {
        this.privateCertPath = privateCertPath;
    }

    public String getApiV3Key() {
        return apiV3Key;
    }

    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }

    public String getCertSerialNo() {
        return certSerialNo;
    }

    public void setCertSerialNo(String certSerialNo) {
        this.certSerialNo = certSerialNo;
    }

    public Boolean getSandboxEnabled() {
        return sandboxEnabled;
    }

    public void setSandboxEnabled(Boolean sandboxEnabled) {
        this.sandboxEnabled = sandboxEnabled;
    }

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
