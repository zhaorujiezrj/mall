package cn.zrj.mall.order.controller;

import cn.zrj.mall.order.service.OrderService;
import com.alipay.api.AlipayApiException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@Api(tags = "支付宝回调通知")
@RestController
@RequestMapping("/app-api/v1/alipay/callback")
@Slf4j
public class AlipayCallbackController {

    @Autowired
    private OrderService orderService;

    /**
     * 支付宝下单和退款的回调接口都是同一个
     * @param request
     * @return 返回success，返回 failure会再次回调该接口
     */
    @ApiIgnore
    @PostMapping("/order/notify")
    public String alipayCallbackNotify(HttpServletRequest request) throws AlipayApiException {
        return orderService.alipayCallbackNotify(request);
    }
}
