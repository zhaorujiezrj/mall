package cn.zrj.mall.order.config;

import cn.zrj.mall.order.autoconfigure.AlipayProperties;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@Configuration
public class AlipayConfiguration {

    private AlipayProperties alipayProperties;

    public AlipayConfiguration(AlipayProperties alipayProperties) {
        this.alipayProperties = alipayProperties;
    }

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(alipayProperties.getGatewayUrl(), alipayProperties.getAppId(),
                alipayProperties.getPrivateKey(), "json", alipayProperties.getCharset(),
                alipayProperties.getAlipayPublicKey(), alipayProperties.getSignType());
    }
}
