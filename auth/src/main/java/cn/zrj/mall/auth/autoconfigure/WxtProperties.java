package cn.zrj.mall.auth.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Configuration
@ConfigurationProperties(prefix = "wx")
@Data
public class WxtProperties {

    private String appId;

    private String secret;
}
