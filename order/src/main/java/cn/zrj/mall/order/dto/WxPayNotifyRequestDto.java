package cn.zrj.mall.order.dto;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
@Data
@Accessors(chain = true)
public class WxPayNotifyRequestDto {

    private SignatureHeader signatureHeader;

    private String notifyData;
}
