package cn.zrj.mall.admin.service.impl;

import cn.zrj.mall.admin.entity.SysRoleMenu;
import cn.zrj.mall.admin.mapper.SysRoleMenuMapper;
import cn.zrj.mall.admin.service.SysRoleMenuService;
import cn.zrj.mall.common.core.constant.RedisConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean saveRoleMenu(Long roleId, List<Long> menuIds) {
        if (null == roleId || CollectionUtils.isEmpty(menuIds)) {
            return false;
        }
        List<Long> roleMenuIds = this.list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        List<Long> saveMenuIds;
        List<Long> removeMenuIds = null;

        if (CollectionUtils.isEmpty(roleMenuIds)) {
            saveMenuIds = menuIds;
        } else {
            //找到需要新增的角色菜单关系
            saveMenuIds = menuIds.stream().filter(menuId -> !roleMenuIds.contains(menuId)).collect(Collectors.toList());
            //找到需要删除的角色菜单关系
            removeMenuIds = roleMenuIds.stream().filter(menuId -> !menuIds.contains(menuId)).collect(Collectors.toList());

            roleMenuIds.addAll(saveMenuIds);
            roleMenuIds.retainAll(removeMenuIds);
        }

        //保存新的角色菜单权限
        List<SysRoleMenu> list = new ArrayList<>();
        SysRoleMenu roleMenu;
        for (Long menuId : saveMenuIds) {
            roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            list.add(roleMenu);
        }
        this.saveBatch(list);

        if (CollectionUtils.isNotEmpty(removeMenuIds)) {
            this.remove(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getRoleId, roleId)
                    .in(SysRoleMenu::getMenuId, removeMenuIds));
        }

        //删除所有的用户菜单数据
        Boolean hasKey = redisTemplate.hasKey(RedisConstants.USER_MENU_RELATION);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(RedisConstants.USER_MENU_RELATION);
        }
/*        //对缓存中的角色绑定的权限进行更新处理
        List<MenuTreeVo> menuTreeList = sysMenuService.getMenuTreeByMenuIds(totalMenuIds);
        String key = RedisConstants.ROLE_MENU_RELATION + roleId;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(menuTreeList));*/
        return true;
    }
}
