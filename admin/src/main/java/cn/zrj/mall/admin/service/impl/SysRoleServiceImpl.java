package cn.zrj.mall.admin.service.impl;

import cn.zrj.mall.admin.entity.SysRole;
import cn.zrj.mall.admin.entity.SysUserRole;
import cn.zrj.mall.admin.mapper.SysRoleMapper;
import cn.zrj.mall.admin.pojo.dto.RoleDto;
import cn.zrj.mall.admin.pojo.query.RolePageQuery;
import cn.zrj.mall.admin.pojo.vo.role.RoleVo;
import cn.zrj.mall.admin.service.SysRoleMenuService;
import cn.zrj.mall.admin.service.SysRoleService;
import cn.zrj.mall.admin.service.SysUserRoleService;
import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.mybatis.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleService sysUserRoleService;

    private final SysRoleMenuService sysRoleMenuService;

    private final StringRedisTemplate redisTemplate;

    @Override
    public IPage<RoleVo> getRolePages(RolePageQuery query) {
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getCode()), SysRole::getCode, query.getCode())
                .like(StringUtils.isNotBlank(query.getName()), SysRole::getCode, query.getName())
        ;
        Page<SysRole> data = this.page(page, wrapper);

        return PageUtils.convertPageToVo(data, RoleVo.class);
    }

    @Override
    public boolean save(Long id, RoleDto roleDto) {
        chekRoleCode(roleDto.getCode(), id);
        SysRole sysRole;
        if (id == null) {
            sysRole = new SysRole();
        } else {
            sysRole = this.getById(id);
            Assert.notNull(sysRole, "该角色不存在");
        }
        sysRole.setId(id);
        sysRole.setCode(roleDto.getCode());
        sysRole.setName(roleDto.getName());
        sysRole.setStatus(roleDto.getStatus());
        this.saveOrUpdate(sysRole);

        sysRoleMenuService.saveRoleMenu(sysRole.getId(), roleDto.getMenuIds());
        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        SysRole role = this.getById(id);
        Assert.notNull(role, "该角色不存在！");

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getRoleId, id);
        List<SysUserRole> list = sysUserRoleService.list(wrapper);
        Assert.isTrue(CollectionUtils.isEmpty(list), "改角色已被绑定，不能删除！");
        this.removeById(id);

        String key = RedisConstants.ROLE_MENU_RELATION + id;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(key);
        }
        return true;
    }

    private void chekRoleCode(String code, Long id) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getCode, code)
                .ne(id != null, SysRole::getId, id);
        SysRole sysRole = this.getOne(wrapper);
        Assert.isTrue(Objects.nonNull(sysRole), "该角色编码已存在，编码：" + code);
    }
}
