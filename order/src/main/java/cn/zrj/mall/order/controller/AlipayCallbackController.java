package cn.zrj.mall.order.controller;

import cn.zrj.mall.order.service.OmsOrderService;
import com.alipay.api.AlipayApiException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@Tag(name = "支付宝回调通知")
@RestController
@RequestMapping("/app-api/v1/alipay/callback")
@Slf4j
public class AlipayCallbackController {

    @Autowired
    private OmsOrderService omsOrderService;

    /**
     * 支付宝下单和退款的回调接口都是同一个
     * @param request
     * @return 返回success，返回 failure会再次回调该接口
     */
    @Hidden
    @PostMapping("/order/notify")
    public String alipayCallbackNotify(HttpServletRequest request) throws AlipayApiException {
        return omsOrderService.alipayCallbackNotify(request);
    }
}
