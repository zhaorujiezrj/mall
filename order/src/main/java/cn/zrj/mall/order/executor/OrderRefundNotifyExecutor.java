package cn.zrj.mall.order.executor;

import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.dto.WxPayNotifyRequestDto;
import cn.zrj.mall.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Component
@Slf4j
public class OrderRefundNotifyExecutor implements OrderMessageExecutor{

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private OrderService orderService;
    @Autowired
    private RocketMQConsumerProperties properties;

    @Override
    public boolean check(MessageExt messageExt) {
        return properties.getOrderRefundTag().equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public void executor(String content) throws JsonProcessingException {
        WxPayNotifyRequestDto request = objectMapper.readValue(content, WxPayNotifyRequestDto.class);
        orderService.wxRefundCallbackNotify(request.getNotifyData(), request.getHeaders());
    }
}
