package cn.zrj.mall.order.enums;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
public enum AlipayTradeStatusEnum {
    TRADE_CLOSED("交易关闭","TRADE_CLOSED"),
    TRADE_FINISHED("交易完结","TRADE_FINISHED"),
    TRADE_SUCCESS("支付成功","TRADE_SUCCESS"),
    WAIT_BUYER_PAY("交易创建","WAIT_BUYER_PAY"),
    ;

    private final String name;

    private final String value;

    AlipayTradeStatusEnum(String name, String value) {
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
