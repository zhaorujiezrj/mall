package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色和菜单关联表 服务类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {
    /**
     * 保存角色菜单关系
     * @param roleId
     * @param menuIds
     * @return
     */
    boolean saveRoleMenu(Long roleId, List<Long> menuIds);
}
