package cn.zrj.mall.order.executor;

import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.entity.OmsOrder;
import cn.zrj.mall.order.service.OmsOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Component
@Slf4j
public class OrderCloseExecutor implements OrderMessageExecutor {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RocketMQConsumerProperties properties;
    @Autowired
    private OmsOrderService omsOrderService;

    @Override
    public boolean check(MessageExt messageExt) {
        return properties.getOrderCreateTag().equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public void executor(String content) throws JsonProcessingException {
        OmsOrder omsOrder = objectMapper.readValue(content, OmsOrder.class);
        log.info("orderCancel:{}", omsOrder);

        if (omsOrderService.orderClose(omsOrder.getOrderSn())) {
            //todo 系统关闭超时支付的订单，还需要回退库存
        }
    }
}
