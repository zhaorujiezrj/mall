package cn.zrj.mall.common.core.enums;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
public enum OutTradeNoTypeEnum {
    WX_PAY("微信支付订单前缀", "wxo"),
    ALIPAY("支付宝支付订单前缀", "alio")
    ;

    private final String name;
    private final String value;

    OutTradeNoTypeEnum(String name, String value) {
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
