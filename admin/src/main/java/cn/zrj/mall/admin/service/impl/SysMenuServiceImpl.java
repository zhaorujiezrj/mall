package cn.zrj.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zrj.mall.admin.entity.SysMenu;
import cn.zrj.mall.admin.mapper.SysMenuMapper;
import cn.zrj.mall.admin.pojo.dto.MenuDto;
import cn.zrj.mall.admin.pojo.vo.menu.MenuTreeVo;
import cn.zrj.mall.admin.pojo.vo.menu.MenuVo;
import cn.zrj.mall.admin.service.SysMenuService;
import cn.zrj.mall.common.core.constant.RedisConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<MenuVo> getMenuList(String name, Integer status) {
        return BeanUtil.copyToList(getMenuLists(name, status), MenuVo.class);
    }

    @Override
    public List<MenuTreeVo> getMenuTree(Integer status) {
        List<SysMenu> menuLists = getMenuLists(null, status);
        return recurTableMenus(0L, menuLists);
    }

    @Override
    public boolean save(Long id, MenuDto menuDto) {
        SysMenu menu;
        boolean isUpdate = false;
        if (id == null) {
            menu = new SysMenu();
        } else {
            menu = getById(id);
            Assert.notNull(menu, "该菜单不存在！");
            if (!Objects.equals(menu.getParentId(), menuDto.getParentId())
                    || !Objects.equals(menu.getName(), menuDto.getName())
                    || !Objects.equals(menu.getPath(), menuDto.getPath())
                    || !Objects.equals(menu.getStatus(), menuDto.getStatus())) {
                isUpdate = true;
            }
        }
        BeanUtil.copyProperties(menuDto, menu);
        saveOrUpdate(menu);
        if (isUpdate) {
            Boolean hasKey = redisTemplate.hasKey(RedisConstants.USER_MENU_RELATION);
            if (Boolean.TRUE.equals(hasKey)) redisTemplate.delete(RedisConstants.USER_MENU_RELATION);
        }
        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        return removeById(id);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByMenuIds(List<Long> menuIds) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1)
                .in(SysMenu::getId, menuIds);
        List<SysMenu> list = this.list(wrapper);
        return getMenuTree(list);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByRoleIds(List<Long> roleIds) {
        List<SysMenu> list = this.baseMapper.getMenuListByRoleIds(roleIds);
        return getMenuTree(list);
    }

    private List<SysMenu> getMenuLists(String name, Integer status) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), SysMenu::getName, name)
                .eq(status != null, SysMenu::getStatus, status);
        return this.list(wrapper);
    }

    private List<MenuTreeVo> getMenuTree(List<SysMenu> list) {
        return recurTableMenus(0L, list);
    }

    /**
     * 递归构建菜单树
     * @param parentId
     * @param menuVoList
     * @return
     */
    private List<MenuTreeVo> recurTableMenus(Long parentId, List<SysMenu> menuVoList) {
        return Optional.ofNullable(menuVoList).orElse(new ArrayList<>())
                .stream().filter(item -> Objects.equals(item.getParentId(), parentId))
                .map(entity -> {
                    MenuTreeVo menuTreeVo = BeanUtil.copyProperties(entity, MenuTreeVo.class);
                    menuTreeVo.setChildren(recurTableMenus(entity.getId(), menuVoList));
                    return menuTreeVo;
                }).collect(Collectors.toList());
    }

}
