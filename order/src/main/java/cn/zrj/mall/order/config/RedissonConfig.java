package cn.zrj.mall.order.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@Configuration
@ConfigurationProperties(prefix = "redisson")
@Data
public class RedissonConfig {

    private String address;

    private String password;

    private Integer database;

    private Integer minIdle;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(address);
        singleServerConfig.setDatabase(database);
        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }
        singleServerConfig.setConnectionMinimumIdleSize(minIdle);
        return Redisson.create(config);
    }
}
