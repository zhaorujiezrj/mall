package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单接口数据快照
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_order_snapshot")
public class TOrderSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单类型: 1-支付, 2-退款
     */
    private Integer orderType;

    /**
     * 下游请求数据
     */
    private String mchReqData;

    /**
     * 下游请求时间
     */
    private LocalDateTime mchReqTime;

    /**
     * 向下游响应数据
     */
    private String mchRespData;

    /**
     * 向下游响应时间
     */
    private LocalDateTime mchRespTime;

    /**
     * 向上游请求数据
     */
    private String channelReqData;

    /**
     * 向上游请求时间
     */
    private LocalDateTime channelReqTime;

    /**
     * 上游响应数据
     */
    private String channelRespData;

    /**
     * 上游响应时间
     */
    private LocalDateTime channelRespTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
