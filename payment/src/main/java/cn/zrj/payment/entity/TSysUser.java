package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_user")
public class TSysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统用户ID
     */
    @TableId(value = "sys_user_id", type = IdType.AUTO)
    private Long sysUserId;

    /**
     * 登录用户名
     */
    private String loginUsername;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 手机号
     */
    private String telphone;

    /**
     * 性别 0-未知, 1-男, 2-女
     */
    private Integer sex;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 员工编号
     */
    private String userNo;

    /**
     * 是否超管（超管拥有全部权限） 0-否 1-是
     */
    private Integer isAdmin;

    /**
     * 状态 0-停用 1-启用
     */
    private Integer state;

    /**
     * 所属系统： MGR-运营平台, MCH-商户中心
     */
    private String sysType;

    /**
     * 所属商户ID / 0(平台)
     */
    private String belongInfoId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
