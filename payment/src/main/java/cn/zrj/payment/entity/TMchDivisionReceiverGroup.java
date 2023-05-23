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
 * 分账账号组
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_mch_division_receiver_group")
public class TMchDivisionReceiverGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组ID
     */
    @TableId(value = "receiver_group_id", type = IdType.AUTO)
    private Long receiverGroupId;

    /**
     * 组名称
     */
    private String receiverGroupName;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 自动分账组（当订单分账模式为自动分账，改组将完成分账逻辑） 0-否 1-是
     */
    private Integer autoDivisionFlag;

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
