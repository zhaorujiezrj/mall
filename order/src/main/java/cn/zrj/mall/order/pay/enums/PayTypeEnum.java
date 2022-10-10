package cn.zrj.mall.order.pay.enums;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/3
 */
public enum PayTypeEnum {

    WX_JSAPI(10, "微信JSAPI支付", PayOrgType.WX),
    WX_APP(11, "微信APP支付", PayOrgType.WX),
    WX_H5(12, "微信H5支付", PayOrgType.WX),
    WX_NATIVE(13, "微信native支付", PayOrgType.WX),
    ALI_APP(20, "支付宝APP支付", PayOrgType.ALI),
    ALI_WAP(21, "支付宝手机网站支付", PayOrgType.ALI),
    ALI_PC(22, "支付宝PC网站支付", PayOrgType.ALI),
    BALANCE(90, "会员余额支付", PayOrgType.ALI),

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
     * 支付机构
     */
    private final PayOrgType payOrgType;

    PayTypeEnum(Integer payType, String name, PayOrgType payOrgType) {
        this.payType = payType;
        this.name = name;
        this.payOrgType = payOrgType;
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

    public PayOrgType getPayOrgType() {
        return payOrgType;
    }
}
