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
 * 订单商品信息表
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-21
 */
@Getter
@Setter
@TableName("oms_order_item")
public class OmsOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * 商品ID
     */
    private Long skuId;

    /**
     * 商品编码
     */
    private String skuSn;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 商品图片
     */
    private String picUrl;

    /**
     * 商品单价(单位：分)
     */
    private Long price;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 商品总价(单位：分)
     */
    private Long totalAmount;

    /**
     * 逻辑删除标识(1:已删除；0:正常)
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
