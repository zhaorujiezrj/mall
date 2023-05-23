package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商户信息表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_mch_info")
public class TMchInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 商户名称
     */
    private String mchName;

    /**
     * 商户简称
     */
    private String mchShortName;

    /**
     * 类型: 1-普通商户, 2-特约商户(服务商模式)
     */
    private Integer type;

    /**
     * 服务商号
     */
    private String isvNo;

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
     * 商户状态: 0-停用, 1-正常
     */
    private Integer state;

    /**
     * 商户备注
     */
    private String remark;

    /**
     * 初始用户ID（创建商户时，允许商户登录的用户）
     */
    private Long initUserId;

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
