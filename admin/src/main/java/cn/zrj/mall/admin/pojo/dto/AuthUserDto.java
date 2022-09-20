package cn.zrj.mall.admin.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OAuth2认证用户信息传输层对象
 *
 * @author zhaorujie
 */
@Data
public class AuthUserDto implements Serializable {

    private static final long serialVersionUID = 279397911246118643L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户状态(1:正常;0:禁用)
     */
    private Integer status;

    /**
     * 用户角色编码集合 ["ROOT","zhaorujie"]
     */
    private List<String> roles;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 手机号
     */
    private String mobile;

}
