package cn.zrj.mall.admin.service.impl;

import cn.zrj.mall.admin.pojo.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import cn.zrj.mall.admin.mapper.SysUserMapper;
import cn.zrj.mall.admin.pojo.dto.UserAddDto;
import cn.zrj.mall.admin.pojo.dto.UserUpdateDto;
import cn.zrj.mall.admin.pojo.query.UserPageQuery;
import cn.zrj.mall.admin.pojo.vo.user.UserVo;
import cn.zrj.mall.admin.service.SysUserRoleService;
import cn.zrj.mall.admin.service.SysUserService;
import cn.zrj.mall.common.core.constant.RedisConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @author zhaorujie
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleService sysUserRoleService;

    private final StringRedisTemplate redisTemplate;

    @Override
    public AuthUserDto getAuthInfoByUsername(String username) {
        return this.baseMapper.getAuthInfoByUsername(username);
    }

    @Override
    public IPage<UserVo> getUserListPage(UserPageQuery query) {
        Page<UserVo> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<UserVo> data = this.baseMapper.getUserListPage(page, query);
        for (UserVo vo : data.getRecords()) {
            if (StringUtils.isNotBlank(vo.getRoleId())) {
                vo.setRoleIds(Arrays.asList(vo.getRoleId().split(",")));
            }
        }
        return data;
    }

    @Override
    public boolean add(UserAddDto userAddDto) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userAddDto.getUsername()));
        Assert.notNull(user, "该账号已存在！");
        user = new SysUser();
        user.setUsername(userAddDto.getUsername());
        user.setNickname(userAddDto.getNickname());
        user.setMobile(userAddDto.getMobile());
        user.setStatus(userAddDto.getStatus());
        return this.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(UserUpdateDto userUpdateDto) {
        SysUser user = this.getById(userUpdateDto.getId());
        Assert.notNull(user, "该用户不存在！");
        user.setNickname(userUpdateDto.getNickname());
        user.setMobile(userUpdateDto.getMobile());
        user.setStatus(userUpdateDto.getStatus());
        this.updateById(user);

        //保存用户角色关系
        sysUserRoleService.saveUserRoles(userUpdateDto.getId(), userUpdateDto.getRoleIds());
        return true;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        SysUser user = this.getById(id);
        Assert.notNull(user, "该用户不存在！");
        user.setStatus(status);
        return this.updateById(user);
    }

    @Override
    public boolean delete(Long id) {
        this.removeById(id);
        String key = RedisConstants.USER_MENU_RELATION + id;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            redisTemplate.delete(key);
        }
        return true;
    }
}
