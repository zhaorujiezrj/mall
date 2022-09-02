package cn.zrj.mall.order.config;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.listener.OrderMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaorujie
 * @date 2022/8/28
 */
@Configuration
@Slf4j
public class RocketMQConsumerConfig {

    private final RocketMQConsumerProperties properties;

    private final OrderMessageListener orderMessageListener;

    public RocketMQConsumerConfig(RocketMQConsumerProperties properties,
                                  OrderMessageListener orderMessageListener) {
        this.properties = properties;
        this.orderMessageListener = orderMessageListener;
    }

    /**
     * 消费者一个topic只能绑定一个监听器
     * @return
     */
    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getGroup());
            consumer.setNamesrvAddr(properties.getNameServerAddr());
            /**
             * 设置consumer第一次启动是从队列头部开始还是队列尾部开始
             * 如果不是第一次启动，那么按照上次消费的位置继续消费
             */
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.registerMessageListener(orderMessageListener);
            //设备批量消息最多拉取1条
            consumer.setConsumeMessageBatchMaxSize(1);

            consumer.subscribe(properties.getTopic(), "*");
            consumer.start();
            log.info("consumer 消费者启动成功 groupName={}, topics={}", properties.getGroup(), properties.getTopic());

            return consumer;
        } catch (Exception e) {
            log.error("rocketmq 消费者启动失败，原因：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }
}
