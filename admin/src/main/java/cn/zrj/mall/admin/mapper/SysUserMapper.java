package cn.zrj.mall.admin.mapper;


import cn.zrj.mall.admin.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

/**
 * @author zhaorujie
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 通过账号获取用户信息
     * @param username
     * @return
     */
    AuthUserDto getAuthInfoByUsername(String username);
}
