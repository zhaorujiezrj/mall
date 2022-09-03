package cn.zrj.mall.order.enums;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
public enum OrderStatusEnum {
    PENDING_PAYMENT(101, "待支付"),
    USER_CANCEL(102, "用户取消"),
    AUTO_CANCEL(103, "系统自动取消"),

    PAYED(201, "已支付"),
    APPLY_REFUND(202, "申请退款"),
    REFUNDED(203, "已退款"),

    DELIVERED(301, "已发货"),

    USER_RECEIVE(401, "用户收货"),
    AUTO_RECEIVE(402, "系统自动收货"),

    FINISHED(901, "已完成")
    ;

    private final Integer code;

    private final String name;

    OrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OrderStatusEnum getValue(Integer code) {
        for (OrderStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
