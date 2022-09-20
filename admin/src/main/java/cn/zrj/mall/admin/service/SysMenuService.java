package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.entity.SysMenu;
import cn.zrj.mall.admin.pojo.dto.MenuDto;
import cn.zrj.mall.admin.pojo.vo.menu.MenuTreeVo;
import cn.zrj.mall.admin.pojo.vo.menu.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 列表查询
     * @param name
     * @param status
     * @return
     */
    List<MenuVo> getMenuList(String name, Integer status);

    /**
     * 菜单树查询
     * @param status
     * @return
     */
    List<MenuTreeVo> getMenuTree(Integer status);

    /**
     * 新增/修改
     * @param id
     * @param menuDto
     * @return
     */
    boolean save(Long id, MenuDto menuDto);

    /**
     * 删除菜单
     * @param id
     * @return
     */
    boolean deleteById(Long id);

    /**
     * 根据菜单id获取菜单树
     * @param menuIds
     * @return
     */
    List<MenuTreeVo> getMenuTreeByMenuIds(List<Long> menuIds);

    /**
     * 根据角色ids获取菜单树
     * @param roleIds
     * @return
     */
    List<MenuTreeVo> getMenuTreeByRoleIds(List<Long> roleIds);

}
