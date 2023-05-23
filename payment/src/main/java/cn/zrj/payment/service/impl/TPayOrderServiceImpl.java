package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TPayOrder;
import cn.zrj.payment.mapper.TPayOrderMapper;
import cn.zrj.payment.service.TPayOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付订单表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TPayOrderServiceImpl extends ServiceImpl<TPayOrderMapper, TPayOrder> implements TPayOrderService {

}
