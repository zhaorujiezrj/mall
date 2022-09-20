package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户和角色关联表 服务类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
public interface SysUserRoleService extends IService<SysUserRole> {
    /**
     * 保存用户角色
     * @param userId
     * @param roleIds
     * @return
     */
    boolean saveUserRoles(Long userId, List<Long> roleIds);
}
