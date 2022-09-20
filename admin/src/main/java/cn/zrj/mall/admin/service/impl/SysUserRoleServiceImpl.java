package cn.zrj.mall.admin.service.impl;

import cn.hutool.json.JSONUtil;
import cn.zrj.mall.admin.entity.SysUserRole;
import cn.zrj.mall.admin.mapper.SysUserRoleMapper;
import cn.zrj.mall.admin.pojo.vo.menu.MenuTreeVo;
import cn.zrj.mall.admin.service.SysMenuService;
import cn.zrj.mall.admin.service.SysUserRoleService;
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
 * 用户和角色关联表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    private final StringRedisTemplate redisTemplate;

    private final SysMenuService sysMenuService;

    @Override
    public boolean saveUserRoles(Long userId, List<Long> roleIds) {
        if (null == userId || CollectionUtils.isEmpty(roleIds)) {
            return false;
        }

        List<SysUserRole> list = this.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        List<Long> userRoleIds = list.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<Long> totalRoleIds;
        List<Long> saveRoleIds;
        List<Long> removeRoleIds = null;
        if (CollectionUtils.isEmpty(userRoleIds)) {
            saveRoleIds = roleIds;
            totalRoleIds = roleIds;
        } else {
            //要新增用户角色关系
            saveRoleIds = roleIds.stream().filter(roleId -> !userRoleIds.contains(roleId)).collect(Collectors.toList());
            //要删除的用户角色关系
            removeRoleIds = userRoleIds.stream().filter(roleId -> !roleIds.contains(roleId)).collect(Collectors.toList());

            userRoleIds.addAll(saveRoleIds);
            userRoleIds.removeAll(removeRoleIds);
            totalRoleIds = userRoleIds;
        }
        //添加新的用户角色关系
        List<SysUserRole> userRoleList = new ArrayList<>();
        SysUserRole sysUserRole;
        if (CollectionUtils.isNotEmpty(saveRoleIds)) {
            for (Long roleId : saveRoleIds) {
                sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                userRoleList.add(sysUserRole);
            }
            this.saveBatch(userRoleList);
        }

        //删除已经移除的角色
        if (CollectionUtils.isNotEmpty(removeRoleIds)) {
            this.remove(new LambdaQueryWrapper<SysUserRole>()
                    .in(SysUserRole::getRoleId, removeRoleIds)
                    .eq(SysUserRole::getUserId, userId));
        }

        //删除缓存中的用户菜单数据
        String key = RedisConstants.USER_MENU_RELATION + userId;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(key);
        }
        //添加新的用户菜单数据
        if (CollectionUtils.isNotEmpty(saveRoleIds) || CollectionUtils.isNotEmpty(removeRoleIds)) {
            List<MenuTreeVo> menuTreeVoList = sysMenuService.getMenuTreeByRoleIds(totalRoleIds);
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(menuTreeVoList));
        }
        return true;
    }
}
