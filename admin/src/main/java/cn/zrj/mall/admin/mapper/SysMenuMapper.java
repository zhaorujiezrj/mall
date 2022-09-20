package cn.zrj.mall.admin.mapper;

import cn.zrj.mall.admin.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单管理 Mapper 接口
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getMenuListByRoleIds(List<Long> roleIds);
}
