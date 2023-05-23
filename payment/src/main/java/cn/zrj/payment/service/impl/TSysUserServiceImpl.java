package cn.zrj.payment.service.impl;

import cn.zrj.payment.entity.TSysUser;
import cn.zrj.payment.mapper.TSysUserMapper;
import cn.zrj.payment.service.TSysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Service
public class TSysUserServiceImpl extends ServiceImpl<TSysUserMapper, TSysUser> implements TSysUserService {

}
