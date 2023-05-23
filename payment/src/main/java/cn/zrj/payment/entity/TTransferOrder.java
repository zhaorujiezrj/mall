package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 转账订单表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_transfer_order")
public class TTransferOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 转账订单号
     */
    private String transferId;

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
     * 商户订单号
     */
    private String mchOrderNo;

    /**
     * 支付接口代码
     */
    private String ifCode;

    /**
     * 入账方式： WX_CASH-微信零钱; ALIPAY_CASH-支付宝转账; BANK_CARD-银行卡
     */
    private String entryType;

    /**
     * 转账金额,单位分
     */
    private Long amount;

    /**
     * 三位货币代码,人民币:cny
     */
    private String currency;

    /**
     * 收款账号
     */
    private String accountNo;

    /**
     * 收款人姓名
     */
    private String accountName;

    /**
     * 收款人开户行名称
     */
    private String bankName;

    /**
     * 转账备注信息
     */
    private String transferDesc;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 支付状态: 0-订单生成, 1-转账中, 2-转账成功, 3-转账失败, 4-订单关闭
     */
    private Integer state;

    /**
     * 特定渠道发起额外参数
     */
    private String channelExtra;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 渠道支付错误码
     */
    private String errCode;

    /**
     * 渠道支付错误描述
     */
    private String errMsg;

    /**
     * 商户扩展参数
     */
    private String extParam;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 转账成功时间
     */
    private LocalDateTime successTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
