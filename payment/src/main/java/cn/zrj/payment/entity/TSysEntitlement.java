package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 系统权限表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_sys_entitlement")
public class TSysEntitlement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID[ENT_功能模块_子模块_操作], eg: ENT_ROLE_LIST_ADD
     */
    private String entId;

    /**
     * 权限名称
     */
    private String entName;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 菜单uri/路由地址
     */
    private String menuUri;

    /**
     * 组件Name（前后端分离使用）
     */
    private String componentName;

    /**
     * 权限类型 ML-左侧显示菜单, MO-其他菜单, PB-页面/按钮
     */
    private String entType;

    /**
     * 快速开始菜单 0-否, 1-是
     */
    private Integer quickJump;

    /**
     * 状态 0-停用, 1-启用
     */
    private Integer state;

    /**
     * 父ID
     */
    private String pid;

    /**
     * 排序字段, 规则：正序
     */
    private Integer entSort;

    /**
     * 所属系统： MGR-运营平台, MCH-商户中心
     */
    private String sysType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
