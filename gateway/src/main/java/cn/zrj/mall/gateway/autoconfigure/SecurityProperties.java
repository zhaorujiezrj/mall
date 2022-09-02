package cn.zrj.mall.gateway.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/8/31
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private boolean enabled;

    private String refreshTokenUrl;

    private List<String> ignoreUrls;
}
