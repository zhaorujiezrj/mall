package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 退款订单表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_refund_order")
public class TRefundOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款订单号（支付系统生成订单号）
     */
    private String refundOrderId;

    /**
     * 支付订单号（与t_pay_order对应）
     */
    private String payOrderId;

    /**
     * 渠道支付单号（与t_pay_order channel_order_no对应）
     */
    private String channelPayOrderNo;

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
     * 商户名称
     */
    private String mchName;

    /**
     * 类型: 1-普通商户, 2-特约商户(服务商模式)
     */
    private Integer mchType;

    /**
     * 商户退款单号（商户系统的订单号）
     */
    private String mchRefundNo;

    /**
     * 支付方式代码
     */
    private String wayCode;

    /**
     * 支付接口代码
     */
    private String ifCode;

    /**
     * 支付金额,单位分
     */
    private Long payAmount;

    /**
     * 退款金额,单位分
     */
    private Long refundAmount;

    /**
     * 三位货币代码,人民币:cny
     */
    private String currency;

    /**
     * 退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-退款任务关闭
     */
    private Integer state;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 渠道错误码
     */
    private String errCode;

    /**
     * 渠道错误描述
     */
    private String errMsg;

    /**
     * 特定渠道发起时额外参数
     */
    private String channelExtra;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 扩展参数
     */
    private String extParam;

    /**
     * 订单退款成功时间
     */
    private LocalDateTime successTime;

    /**
     * 退款失效时间（失效后系统更改为退款任务关闭状态）
     */
    private LocalDateTime expiredTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
