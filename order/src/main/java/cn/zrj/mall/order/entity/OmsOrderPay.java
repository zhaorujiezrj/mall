package cn.zrj.mall.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 支付信息表
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-21
 */
@Getter
@Setter
@TableName("oms_order_pay")
public class OmsOrderPay implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 支付流水号
     */
    private String paySn;

    /**
     * 应付总额(分)
     */
    private Long payAmount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】
     */
    private Integer payType;

    /**
     * 支付状态
     */
    private Integer payStatus;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 回调内容
     */
    private String callbackContent;

    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;

    /**
     * 交易内容
     */
    private String paySubject;

    /**
     * 逻辑删除【0->正常；1->已删除】
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
