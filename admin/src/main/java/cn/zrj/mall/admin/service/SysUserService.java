package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhaorujie
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名获取认证信息
     *
     * @param username
     * @return
     */
    AuthUserDto getAuthInfoByUsername(String username);
}
