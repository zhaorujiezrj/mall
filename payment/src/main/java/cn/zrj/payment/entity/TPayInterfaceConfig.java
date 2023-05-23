package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 支付接口配置参数表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_pay_interface_config")
public class TPayInterfaceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号类型:1-服务商 2-商户 3-商户应用
     */
    private Integer infoType;

    /**
     * 服务商号/商户号/应用ID
     */
    private String infoId;

    /**
     * 支付接口代码
     */
    private String ifCode;

    /**
     * 接口配置参数,json字符串
     */
    private String ifParams;

    /**
     * 支付接口费率
     */
    private BigDecimal ifRate;

    /**
     * 状态: 0-停用, 1-启用
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者用户ID
     */
    private Long createdUid;

    /**
     * 创建者姓名
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新者用户ID
     */
    private Long updatedUid;

    /**
     * 更新者姓名
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
