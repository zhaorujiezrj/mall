package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 操作员<->角色 关联表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_user_role_rela")
public class TSysUserRoleRela implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private String roleId;


}
