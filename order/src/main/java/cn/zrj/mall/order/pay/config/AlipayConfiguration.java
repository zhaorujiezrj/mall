package cn.zrj.mall.order.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@Configuration
@ConditionalOnClass(DefaultAlipayClient.class)
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayConfiguration {

    private final AlipayProperties alipayProperties;

    public AlipayConfiguration(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(alipayProperties.getGatewayUrl(), alipayProperties.getAppId(),
                alipayProperties.getPrivateKey(), "json", alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(), alipayProperties.getSignType());
    }
}
