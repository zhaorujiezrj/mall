package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.entity.SysRole;
import cn.zrj.mall.admin.pojo.dto.RoleDto;
import cn.zrj.mall.admin.pojo.query.RolePageQuery;
import cn.zrj.mall.admin.pojo.vo.role.RoleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 分页查询
     * @param query
     * @return
     */
    IPage<RoleVo> getRolePages(RolePageQuery query);

    /**
     * 新增或修改
     * @param id
     * @param roleDto
     * @return
     */
    boolean save(Long id, RoleDto roleDto);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean deleteById(Long id);
}
