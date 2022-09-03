package cn.zrj.mall.order.executor;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.order.dto.WxPayNotifyRequestDto;
import cn.zrj.mall.order.entity.Order;
import cn.zrj.mall.order.enums.OrderStatusEnum;
import cn.zrj.mall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Component
@Slf4j
public class OrderRefundNotifyExecutor implements OrderMessageExecutor{

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private OrderService orderService;

    @Override
    public boolean check(MessageExt messageExt) {
        return "order-refund-tag".equalsIgnoreCase(messageExt.getTags());
    }

    @Override
    public ConsumeConcurrentlyStatus executor(String content) throws JsonProcessingException {
        log.info("开始处理退款结果通知");
        WxPayNotifyRequestDto request = objectMapper.readValue(content, WxPayNotifyRequestDto.class);
        WxPayRefundNotifyV3Result.DecryptNotifyResult result = null;
        try {
            result = wxPayService.parseRefundNotifyV3Result(request.getNotifyData(), request.getSignatureHeader()).getResult();
        } catch (WxPayException e) {
            log.error("微信退款通知解密失败，原因：{}", e.getMessage());
            throw new BusinessException("微信退款通知解密失败");
        }
        log.debug("退款通知解密成功：[{}]", result.toString());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOutTradeNo, result.getOutTradeNo());
        Order order = orderService.getOne(wrapper);
        if (Objects.isNull(order)) {
            log.error("微信退款无法找到对应的订单，【商户订单号：{},退款号：{}】", result.getOutTradeNo(), result.getOutRefundNo());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (Objects.equals(result.getRefundStatus(), WxPayConstants.RefundStatus.SUCCESS)) {
            order.setStatus(OrderStatusEnum.REFUNDED.getCode());
            order.setRefundId(result.getRefundId());
            orderService.updateById(order);
            log.info("微信退款成功，【单号：{}】", result.getOutTradeNo());
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
