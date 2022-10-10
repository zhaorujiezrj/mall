package cn.zrj.mall.order.pay.enums;

/**
 * 支付机构
 * @author zhaorujie
 * @date 2022/10/9
 */
public enum PayOrgType {
    /**
     * 微信
     */
    WX(1),
    /**
     * 支付宝
     */
    ALI(2)
    ;

    private final Integer type;

    PayOrgType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
