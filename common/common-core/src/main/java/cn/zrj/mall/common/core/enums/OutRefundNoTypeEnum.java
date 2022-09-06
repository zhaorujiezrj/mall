package cn.zrj.mall.common.core.enums;

/**
 * @author zhaorujie
 * @date 2022/9/6
 */
public enum OutRefundNoTypeEnum {
    WX_REFUND("微信退款单前缀", "wxof"),
    ALI_REFUND("支付宝退款单前缀", "aliof"),
    ;

    private final String name;
    private final String value;

    OutRefundNoTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
