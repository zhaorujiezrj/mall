package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商户应用表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_mch_app")
public class TMchApp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 应用状态: 0-停用, 1-正常
     */
    private Integer state;

    /**
     * 应用私钥
     */
    private String appSecret;

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
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
