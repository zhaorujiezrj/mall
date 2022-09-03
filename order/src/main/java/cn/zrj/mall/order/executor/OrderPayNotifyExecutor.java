package cn.zrj.mall.order.executor;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.autoconfigure.RocketMQConsumerProperties;
import cn.zrj.mall.order.dto.WxPayNotifyRequestDto;
import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.enums.OrderStatusEnum;
import cn.zrj.mall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;


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
    private WxPayService wxPayService;
    @Autowired
    private OrderService orderService;

    @Override
    public boolean check(MessageExt messageExt) {
        return properties.getOrderPayTag().equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public ConsumeConcurrentlyStatus executor(String content) throws JsonProcessingException {
        log.info("开始处理支付结果通知");
        WxPayNotifyRequestDto request = objectMapper.readValue(content, WxPayNotifyRequestDto.class);
        WxPayOrderNotifyV3Result.DecryptNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyV3Result(request.getNotifyData(), request.getSignatureHeader()).getResult();
        } catch (WxPayException e) {
            log.error("微信支付通知解密失败，原因：{}", e.getMessage());
            throw new BusinessException("微信支付通知解密失败");
        }
        log.debug("支付通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = orderService.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("无法找到对应的支付订单，商户单号：【{}】", result.getOutTradeNo());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (Objects.equals(order.getStatus(), OrderStatusEnum.PAYED.getCode())) {
            log.error("订单已完成支付，无需继续处理，【商户单号：{}】", result.getOutTradeNo());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (Objects.equals(result.getTradeState(), WxPayConstants.WxpayTradeStatus.SUCCESS)) {
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            order.setTransactionId(result.getTransactionId());
            order.setPayTime(new Date());
            orderService.updateById(order);
            log.info("微信支付成功，【单号：{}】", result.getOutTradeNo());
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
