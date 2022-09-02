package cn.zrj.mall.order.config;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/8/28
 */
@Configuration
@Slf4j
public class RocketMQProducerConfig {

    private final RocketMQProducerProperties properties;

    public RocketMQProducerConfig(RocketMQProducerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer(properties.getGroup());
            producer.setNamesrvAddr(properties.getNameServerAddr());
            producer.setVipChannelEnabled(false);
            producer.start();
            log.info("rocketmq 生产者启动成功");
            return producer;
        } catch (Exception e) {
            log.error("rocketmq 生产者启动失败，原因：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

}
