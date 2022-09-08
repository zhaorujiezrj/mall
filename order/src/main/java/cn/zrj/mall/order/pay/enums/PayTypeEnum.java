package cn.zrj.mall.order.pay.enums;

import cn.zrj.mall.order.pay.constant.BeanNameConstants;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/3
 */
public enum PayTypeEnum {

    WX_JSAPI(10, "微信JSAPI支付", BeanNameConstants.WX),
    WX_APP(11, "微信APP支付", BeanNameConstants.WX),
    WX_H5(12, "微信H5支付", BeanNameConstants.WX),
    WX_NATIVE(13, "微信native支付", BeanNameConstants.WX),
    ALI_APP(20, "支付宝APP支付", BeanNameConstants.ALI),
    ALI_WAP(21, "支付宝手机网站支付", BeanNameConstants.ALI),
    ALI_PC(22, "支付宝PC网站支付", BeanNameConstants.ALI),
    BALANCE(90, "会员余额支付", BeanNameConstants.OTHER),

    ;

    /**
     * 类型
     */
    private final Integer payType;
    /**
     * 名称
     */
    private final String name;
    /**
     * 支付机构beanName
     */
    private final String beanName;

    PayTypeEnum(Integer payType, String name, String beanName) {
        this.payType = payType;
        this.name = name;
        this.beanName = beanName;
    }

    public static PayTypeEnum getByPayType(Integer payType) {
        for (PayTypeEnum payTypeEnum : values()) {
            if (Objects.equals(payTypeEnum.payType, payType)) {
                return payTypeEnum;
            }
        }
        return null;
    }

    public Integer getPayType() {
        return payType;
    }

    public String getName() {
        return name;
    }

    public String getBeanName() {
        return beanName;
    }
}
