package cn.zrj.mall.admin.service.impl;

import cn.zrj.mall.admin.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import cn.zrj.mall.admin.mapper.SysUserMapper;
import cn.zrj.mall.admin.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zhaorujie
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public AuthUserDto getAuthInfoByUsername(String username) {
        return this.baseMapper.getAuthInfoByUsername(username);
    }
}
