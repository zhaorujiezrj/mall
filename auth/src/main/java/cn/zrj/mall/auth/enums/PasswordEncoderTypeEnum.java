package cn.zrj.mall.auth.enums;

/**
 * @author zhaorujie
 * @date 2022/9/21
 */
public enum PasswordEncoderTypeEnum {

    BCRYPT("{bcrypt}","BCRYPT加密"),
    NOOP("{noop}","无加密明文");

    private final String prefix;
    private final String desc;

    PasswordEncoderTypeEnum(String prefix, String desc){
        this.prefix = prefix;
        this.desc = desc;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDesc() {
        return desc;
    }
}
