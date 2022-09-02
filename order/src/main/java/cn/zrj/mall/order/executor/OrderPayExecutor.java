package cn.zrj.mall.order.executor;


import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author zhaorujie
 * @date 2022/8/26
 */
@Component
@Slf4j
public class OrderPayExecutor implements OrderMessageExecutor {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RocketMQConsumerProperties properties;

    @Override
    public boolean check(MessageExt messageExt) {
        return properties.getOrderPayTag().equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public ConsumeConcurrentlyStatus executor(String content) throws JsonProcessingException {
        Order order = objectMapper.readValue(content, Order.class);
        log.info("{}", order);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
