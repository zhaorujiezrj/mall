package cn.zrj.mall.order.service.impl;

import cn.zrj.mall.common.core.enums.BusinessTypeEnum;
import cn.zrj.mall.common.core.util.BusinessNoUtils;
import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.mapper.OrderMapper;
import cn.zrj.mall.order.service.OrderService;
import cn.zrj.mall.order.util.RocketMQUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RocketMQProducerProperties properties;


    @Override
    public void createOrder() {
        Order order = new Order()
                .setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER))
                        .setMemberId(76L);
        try {
            SendResult send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), order, null);
            log.info("send1={}", send);

            order.setOrderSn(BusinessNoUtils.generate(BusinessTypeEnum.ORDER));
            send = RocketMQUtils.send(properties.getTopic(), properties.getOrderCreateTag(), order, 5);
            log.info("send2={}", send);
        } catch (Exception e) {
            log.error("MQ消息发送失败，原因：{}", e.getMessage());
        }
    }
}
