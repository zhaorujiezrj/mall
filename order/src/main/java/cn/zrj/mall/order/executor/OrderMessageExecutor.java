package cn.zrj.mall.order.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author zhaorujie
 * @date 2022/8/26
 */
public interface OrderMessageExecutor{
    /**
     * tags比较
     * @param messageExt
     * @return
     */
    boolean check(MessageExt messageExt);

    /**
     * 执行消息处理
     * @param content
     * @return
     */
    ConsumeConcurrentlyStatus executor(String content) throws JsonProcessingException;
}
