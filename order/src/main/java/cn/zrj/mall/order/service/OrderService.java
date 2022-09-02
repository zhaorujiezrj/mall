package cn.zrj.mall.order.service;

import cn.zrj.mall.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhaorujie
 * @date 2022/8/25
 */
public interface OrderService extends IService<Order> {
    /**
     * 创建订单
     */
    void createOrder();
}
