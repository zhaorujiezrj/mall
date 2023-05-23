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
 * 商户通知记录表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_mch_notify_record")
public class TMchNotifyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户通知记录ID
     */
    @TableId(value = "notify_id", type = IdType.AUTO)
    private Long notifyId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单类型:1-支付,2-退款
     */
    private Integer orderType;

    /**
     * 商户订单号
     */
    private String mchOrderNo;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 服务商号
     */
    private String isvNo;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 通知响应结果
     */
    private String resResult;

    /**
     * 通知次数
     */
    private Integer notifyCount;

    /**
     * 最大通知次数, 默认6次
     */
    private Integer notifyCountLimit;

    /**
     * 通知状态,1-通知中,2-通知成功,3-通知失败
     */
    private Integer state;

    /**
     * 最后一次通知时间
     */
    private LocalDateTime lastNotifyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
