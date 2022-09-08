package cn.zrj.mall.order.controller;

import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
@Api(tags = "订单管理")
@RequestMapping("/app-api/v1/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单创建")
    @PostMapping("/create")
    public Result<Void> createOrder() {
        orderService.createOrder();
        return Result.success();
    }

    @ApiOperation("订单支付")
    @PostMapping("/pay")
    public <T> Result<T> pay(String orderSn, Integer payType) {
       return Result.success(orderService.pay(orderSn, payType));
    }

    @ApiOperation("订单退款")
    @PostMapping("refund")
    public Result<Void> refund(@ApiParam("订单编码") String orderNo, @ApiParam("状态 0=申请退款 1=同意") Integer status) {
        orderService.refund(orderNo, status);
        return Result.success();
    }
}
