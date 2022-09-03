package cn.zrj.mall.order.listener;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.executor.OrderMessageExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Slf4j
@Component
public class OrderMessageListener implements MessageListenerConcurrently{

    private final List<OrderMessageExecutor> executors;

    public OrderMessageListener(Collection<OrderMessageExecutor> executors) {
        this.executors = new ArrayList<>(executors);
    }

    /**
     * 默认msg只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
     * 不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     * @param list 消息集合
     * @param consumeConcurrentlyContext 消费者上下文
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        String msgId = null;
        try {
            if (CollectionUtils.isEmpty(list)) {
                log.info("MQ接收到的消息为空!");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

            MessageExt messageExt = list.get(0);
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            msgId = messageExt.getMsgId();
            String content = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            log.info("MQ消息【topic:{}, tag:{}, msgId={}, content:{}】", topic, tags, msgId, content);

            for (OrderMessageExecutor executor : executors) {
                if (executor.check(messageExt)) {
                    return executor.executor(content);
                }
            }
            log.error("未找到匹配的tags！");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (IOException e) {
            log.error("MQ消息无法解析:" + msgId, e);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
