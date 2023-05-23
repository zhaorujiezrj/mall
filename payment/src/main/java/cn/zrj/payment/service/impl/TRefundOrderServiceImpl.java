package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TRefundOrder;
import cn.zrj.payment.mapper.TRefundOrderMapper;
import cn.zrj.payment.service.TRefundOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款订单表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TRefundOrderServiceImpl extends ServiceImpl<TRefundOrderMapper, TRefundOrder> implements TRefundOrderService {

}
