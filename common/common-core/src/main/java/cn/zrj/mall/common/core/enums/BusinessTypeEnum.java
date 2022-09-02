package cn.zrj.mall.common.core.enums;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/9/2
 */
public enum BusinessTypeEnum {
    ORDER("订单", "100"),
    ;

    private final String name;

    private final String value;

    BusinessTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getNameByValue(String value) {
        for (BusinessTypeEnum typeEnum : values()) {
            if (Objects.equals(typeEnum.value, value)) {
                return typeEnum.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
