package cn.zrj.mall.order.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/8/31
 */
@Configuration
@ConfigurationProperties(prefix = "rocketmq.producer")
@Data
public class RocketMQProducerProperties {

    private String nameServerAddr;
    private String group;
    private String topic;
    private String orderCreateTag;
    private String orderPayTag;
    private String orderRefundTag;

}
