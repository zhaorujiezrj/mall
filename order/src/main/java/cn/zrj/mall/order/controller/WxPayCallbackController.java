package cn.zrj.mall.order.controller;

import cn.zrj.mall.order.autoconfigure.RocketMQProducerProperties;
import cn.zrj.mall.order.dto.WxPayNotifyRequestDto;
import cn.zrj.mall.order.service.OmsOrderService;
import cn.zrj.mall.order.util.RocketMQUtils;
import cn.zrj.mall.order.vo.WxPayNotifyResponseVo;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Tag(name = "微信支付回调通知")
@RestController
@RequestMapping("/app-api/v1/wx-pay/callback")
@Slf4j
public class WxPayCallbackController {

    @Autowired
    private RocketMQProducerProperties properties;
    @Autowired
    private OmsOrderService omsOrderService;

    /**
     * 微信下单支付结果回调
     *
     * @param notifyData 加密数据
     * @param headers    请求头
     * @return {"code": "SUCCESS", "message": "成功"}
     */
    @Hidden
    @PostMapping("order/pay/notify/v3")
    public WxPayNotifyResponseVo wxPayCallbackNotify(@RequestBody String notifyData,
                                                     @RequestHeader HttpHeaders headers) {
        try {
            omsOrderService.wxPayCallbackNotify(notifyData, headers);
            return new WxPayNotifyResponseVo()
                    .setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
        } catch (Exception e) {
            return new WxPayNotifyResponseVo()
                    .setCode(WxPayConstants.ResultCode.FAIL)
                    .setMessage("失败");
        }
    }

    /**
     * 微信退款结果回调
     *
     * @param notifyData 加密数据
     * @param headers    请求头
     * @return {"code": "SUCCESS", "message": "成功"}
     */
    @Hidden
    @PostMapping("order/refund/notify/v3")
    public WxPayNotifyResponseVo wxRefundCallbackNotify(@RequestBody String notifyData,
                                                          @RequestHeader HttpHeaders headers) {
        try {
            omsOrderService.wxRefundCallbackNotify(notifyData, headers);
            return new WxPayNotifyResponseVo()
                    .setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
        }catch (Exception e) {
            return new WxPayNotifyResponseVo()
                    .setCode(WxPayConstants.ResultCode.FAIL)
                    .setMessage("失败");
        }
    }


    /**
     * 微信下单支付结果回调,MQ处理
     *
     * @param notifyData 加密数据
     * @param headers    请求头
     * @return {"code": "SUCCESS", "message": "成功"}
     */
    @Hidden
    @PostMapping("order/pay/notify/v3/mq")
    public WxPayNotifyResponseVo wxPayNotifyCallbackMQ(@RequestBody String notifyData,
                                                     @RequestHeader HttpHeaders headers) throws Exception {
        return wxPayNotifyResponse(notifyData, headers, properties.getTopic(), properties.getOrderPayTag());
    }

    /**
     * 微信退款结果回调,MQ处理
     *
     * @param notifyData 加密数据
     * @param headers    请求头
     * @return {"code": "SUCCESS", "message": "成功"}
     */
    @Hidden
    @PostMapping("order/refund/notify/v3/mq")
    public WxPayNotifyResponseVo wxRefundNotifyCallbackMQ(@RequestBody String notifyData,
                                                        @RequestHeader HttpHeaders headers) throws Exception {
        return wxPayNotifyResponse(notifyData, headers, properties.getTopic(), properties.getOrderRefundTag());
    }

    private WxPayNotifyResponseVo wxPayNotifyResponse(String notifyData, HttpHeaders headers, String topics, String tags) throws Exception {
        WxPayNotifyRequestDto requestDto = new WxPayNotifyRequestDto();
        requestDto.setNotifyData(notifyData);
        requestDto.setHeaders(headers);
        //发送MQ消息进行处理，防止回调过程网络出现波动导致失败
        SendResult result = RocketMQUtils.send(topics, tags, requestDto);
        if (result != null && result.getSendStatus() == SendStatus.SEND_OK) {
            return new WxPayNotifyResponseVo()
                    .setCode(WxPayConstants.ResultCode.SUCCESS)
                    .setMessage("成功");
        }
        return new WxPayNotifyResponseVo()
                .setCode(WxPayConstants.ResultCode.FAIL)
                .setMessage("失败");
    }

}
