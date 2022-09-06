package cn.zrj.mall.order.enums;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/3
 */
public enum PayTypeEnum {
    WX_JSAPI(1, "微信JSAPI支付"),
    WX_APP(2, "微信APP支付"),
    WX_H5(3, "微信H5支付"),
    WX_NATIVE(4, "微信native支付"),
    ALIPAY(5, "支付宝支付"),
    BALANCE(6, "会员余额支付"),

    ;

    private final Integer value;
    private final String name;

    PayTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static PayTypeEnum getValue(Integer value) {
        for (PayTypeEnum payTypeEnum : values()) {
            if (Objects.equals(payTypeEnum.value, value)) {
                return payTypeEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
