package cn.zrj.mall.order.executor;

import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.dto.WxPayNotifyRequestDto;
import cn.zrj.mall.order.service.OmsOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaorujie
 * @date 2022/8/26
 */
@Component
@Slf4j
public class OrderPayNotifyExecutor implements OrderMessageExecutor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RocketMQConsumerProperties properties;
    @Autowired
    private OmsOrderService omsOrderService;

    @Override
    public boolean check(MessageExt messageExt) {
        return properties.getOrderPayTag().equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public void executor(String content) throws JsonProcessingException {
        WxPayNotifyRequestDto request = objectMapper.readValue(content, WxPayNotifyRequestDto.class);
        omsOrderService.wxPayCallbackNotify(request.getNotifyData(), request.getHeaders());
    }
}
