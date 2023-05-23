package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TSysLog;
import cn.zrj.payment.mapper.TSysLogMapper;
import cn.zrj.payment.service.TSysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统操作日志表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TSysLogServiceImpl extends ServiceImpl<TSysLogMapper, TSysLog> implements TSysLogService {

}
