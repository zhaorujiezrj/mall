package cn.zrj.mall.order.controller;

import cn.zrj.mall.common.result.Result;
import cn.zrj.mall.order.service.OrderService;
import io.swagger.annotations.Api;
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

    @PostMapping("/create")
    public Result<Void> createOrder() {
        orderService.createOrder();
        return Result.success();
    }
}
