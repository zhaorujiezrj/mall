package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TTransferOrder;
import cn.zrj.payment.mapper.TTransferOrderMapper;
import cn.zrj.payment.service.TTransferOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 转账订单表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TTransferOrderServiceImpl extends ServiceImpl<TTransferOrderMapper, TTransferOrder> implements TTransferOrderService {

}
