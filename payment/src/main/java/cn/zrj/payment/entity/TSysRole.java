package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统角色表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_role")
public class TSysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID, ROLE_开头
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 所属系统： MGR-运营平台, MCH-商户中心
     */
    private String sysType;

    /**
     * 所属商户ID / 0(平台)
     */
    private String belongInfoId;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
