package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 支付接口定义表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_pay_interface_define")
public class TPayInterfaceDefine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口代码 全小写  wxpay alipay 
     */
    private String ifCode;

    /**
     * 接口名称
     */
    private String ifName;

    /**
     * 是否支持普通商户模式: 0-不支持, 1-支持
     */
    private Integer isMchMode;

    /**
     * 是否支持服务商子商户模式: 0-不支持, 1-支持
     */
    private Integer isIsvMode;

    /**
     * 支付参数配置页面类型:1-JSON渲染,2-自定义
     */
    private Integer configPageType;

    /**
     * ISV接口配置定义描述,json字符串
     */
    private String isvParams;

    /**
     * 特约商户接口配置定义描述,json字符串
     */
    private String isvsubMchParams;

    /**
     * 普通商户接口配置定义描述,json字符串
     */
    private String normalMchParams;

    /**
     * 支持的支付方式 ["wxpay_jsapi", "wxpay_bar"]
     */
    private String wayCodes;

    /**
     * 页面展示：卡片-图标
     */
    private String icon;

    /**
     * 页面展示：卡片-背景色
     */
    private String bgColor;

    /**
     * 状态: 0-停用, 1-启用
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
