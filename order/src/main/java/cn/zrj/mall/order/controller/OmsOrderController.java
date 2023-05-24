package cn.zrj.mall.order.controller;

import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.order.service.OmsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Tag(name = "订单管理")
@RequestMapping("/app-api/v1/order")
@RestController
public class OmsOrderController {

    @Autowired
    private OmsOrderService omsOrderService;

    @Operation(summary = "订单创建")
    @PostMapping("/create")
    public Result<Void> createOrder() {
        omsOrderService.createOrder();
        return Result.success();
    }

    @Operation(summary = "订单支付")
    @PostMapping("/pay")
    public <T> Result<T> pay(String orderSn, Integer payType) {
       return Result.success(omsOrderService.pay(orderSn, payType));
    }

    @Operation(summary = "订单退款")
    @PostMapping("refund")
    public Result<Void> refund(@Parameter(ref = "订单编码") String orderNo, @Parameter(ref = "状态 0=申请退款 1=同意") Integer status) {
        omsOrderService.refund(orderNo, status);
        return Result.success();
    }
}
