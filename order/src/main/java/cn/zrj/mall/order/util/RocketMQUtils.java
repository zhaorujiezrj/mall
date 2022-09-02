package cn.zrj.mall.order.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/1
 */
@Component
public class RocketMQUtils {

    private static DefaultMQProducer producer;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RocketMQUtils(DefaultMQProducer producer) {
        RocketMQUtils.producer = producer;
    }

    public static SendResult send(Message message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        return producer.send(message);
    }

    public static SendResult send(String topics, String tags, Object content) throws JsonProcessingException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        if (Objects.isNull(content)) return null;
        return send(buildMessage(topics, tags, content, null));
    }

    public static SendResult send(String topics, String tags, Object content, Integer level) throws JsonProcessingException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        if (Objects.isNull(content)) return null;
        return send(buildMessage(topics, tags, content, level));
    }

    public static Message buildMessage(String topics, String tags, Object content, Integer level) throws JsonProcessingException {
        byte[] data = null;
        if (Objects.nonNull(content)) {
            String str = content instanceof String ? (String) content : OBJECT_MAPPER.writeValueAsString(content);
            data = str.getBytes(StandardCharsets.UTF_8);
        }
        Message message = new Message(topics, tags, data);
        if (level != null && level >= 0 && level <= 18) {
            message.setDelayTimeLevel(level);
        }
        return message;
    }
}
