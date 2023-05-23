package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 服务商信息表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_isv_info")
public class TIsvInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务商号
     */
    private String isvNo;

    /**
     * 服务商名称
     */
    private String isvName;

    /**
     * 服务商简称
     */
    private String isvShortName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactTel;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 状态: 0-停用, 1-正常
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
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
