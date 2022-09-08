package cn.zrj.mall.order.pay.enums;

import cn.zrj.mall.order.pay.constant.BeanNameConstants;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/3
 */
public enum PayTypeEnum {

    WX_JSAPI(1, "微信JSAPI支付", BeanNameConstants.WX),
    WX_APP(2, "微信APP支付", BeanNameConstants.WX),
    WX_H5(3, "微信H5支付", BeanNameConstants.WX),
    WX_NATIVE(4, "微信native支付", BeanNameConstants.WX),
    ALI_APP(5, "支付宝APP支付", BeanNameConstants.ALI),
    ALI_WAP(6, "支付宝手机网站支付", BeanNameConstants.ALI),
    ALI_PC(7, "支付宝PC网站支付", BeanNameConstants.ALI),
    BALANCE(8, "会员余额支付", BeanNameConstants.OTHER),

    ;

    /**
     * 值
     */
    private final Integer value;
    /**
     * 名称
     */
    private final String name;
    /**
     * 支付机构beanName
     */
    private final String beanName;

    PayTypeEnum(Integer value, String name, String beanName) {
        this.value = value;
        this.name = name;
        this.beanName = beanName;
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

    public String getBeanName() {
        return beanName;
    }
}
