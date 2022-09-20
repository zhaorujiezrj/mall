package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.pojo.dto.AuthUserDto;
import cn.zrj.mall.admin.entity.SysUser;
import cn.zrj.mall.admin.pojo.dto.UserAddDto;
import cn.zrj.mall.admin.pojo.dto.UserUpdateDto;
import cn.zrj.mall.admin.pojo.query.UserPageQuery;
import cn.zrj.mall.admin.pojo.vo.user.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhaorujie
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名获取认证信息
     *
     * @param username
     * @return
     */
    AuthUserDto getAuthInfoByUsername(String username);

    /**
     * 分页查询
     * @param query
     * @return
     */
    IPage<UserVo> getUserListPage(UserPageQuery query);

    /**
     * 添加
     * @param userAddDto
     * @return
     */
    boolean add(UserAddDto userAddDto);

    /**
     * 编辑
     * @param userUpdateDto
     * @return
     */
    boolean update(UserUpdateDto userUpdateDto);

    /**
     * 状态修改
     * @param id
     * @param status
     * @return
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Long id);
}
