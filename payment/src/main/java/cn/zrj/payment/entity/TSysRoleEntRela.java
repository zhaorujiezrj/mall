package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统角色权限关联表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_role_ent_rela")
public class TSysRoleEntRela implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 权限ID
     */
    private String entId;


}
