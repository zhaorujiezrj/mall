package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TOrderSnapshot;
import cn.zrj.payment.mapper.TOrderSnapshotMapper;
import cn.zrj.payment.service.TOrderSnapshotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单接口数据快照 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TOrderSnapshotServiceImpl extends ServiceImpl<TOrderSnapshotMapper, TOrderSnapshot> implements TOrderSnapshotService {

}
