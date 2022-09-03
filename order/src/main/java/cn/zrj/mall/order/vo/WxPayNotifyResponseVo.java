package cn.zrj.mall.order.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Data
@Accessors(chain = true)
public class WxPayNotifyResponseVo {

    private String code;

    private String message;
}
