package cn.zrj.mall.order.pay.enums;

/**
 * @author zhaorujie
 * @date 2022/9/7
 */
public enum AliTradeTypeEnum {
    APP("APP"),
    WAP("WAP"),
    PC("PC"),
    ;
    private final String name;

    AliTradeTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
