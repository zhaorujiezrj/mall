package cn.zrj.mall.order.pay.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Slf4j
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayConfiguration {

    private final WxPayProperties properties;

    public WxPayConfiguration(WxPayProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxPayService() {
        if (StringUtils.isBlank(properties.getMchId())) {
            log.error("微信商户号配置无效!");
            return new WxPayServiceImpl();
        }
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setMchId(StringUtils.trimToNull(properties.getMchId()));
        wxPayConfig.setMchKey(StringUtils.trimToNull(properties.getMchKey()));
        wxPayConfig.setSubAppId(StringUtils.trimToNull(properties.getSubAppId()));
        wxPayConfig.setSubMchId(StringUtils.trimToNull(properties.getSubMchId()));
        wxPayConfig.setKeyPath(StringUtils.trimToNull(properties.getKeyPath()));
        wxPayConfig.setApiV3Key(StringUtils.trimToNull(properties.getApiV3Key()));
        wxPayConfig.setCertSerialNo(StringUtils.trimToNull(properties.getCertSerialNo()));
        wxPayConfig.setPrivateKeyPath(StringUtils.trimToNull(properties.getPrivateKeyPath()));
        wxPayConfig.setPrivateCertPath(StringUtils.trimToNull(properties.getPrivateCertPath()));
        // 可以指定是否使用沙箱环境
        wxPayConfig.setUseSandboxEnv(BooleanUtils.toBoolean(properties.getSandboxEnabled()));

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }
}
