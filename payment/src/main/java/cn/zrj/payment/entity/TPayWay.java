package cn.zrj.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 支付方式表
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("t_pay_way")
public class TPayWay implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付方式代码  例如： wxpay_jsapi
     */
    private String wayCode;

    /**
     * 支付方式名称
     */
    private String wayName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
