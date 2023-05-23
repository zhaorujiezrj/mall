package cn.zrj.mall.admin.mapper;


import cn.zrj.mall.admin.pojo.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import cn.zrj.mall.admin.pojo.query.UserPageQuery;
import cn.zrj.mall.admin.pojo.vo.user.UserVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    IPage<UserVo> getUserListPage(Page<UserVo> page, @Param("query") UserPageQuery query);
}
