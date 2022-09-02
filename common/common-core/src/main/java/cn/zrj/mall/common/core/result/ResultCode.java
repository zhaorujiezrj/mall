package cn.zrj.mall.common.core.result;

import java.io.Serializable;

/**
 * @author zhaorujie
 * @date 2022/8/16
 */
public enum ResultCode implements Serializable {
    SUCCESS("200", "成功"),
    UNAUTHORIZED("401", "访问未授权"),
    TOKEN_INVALID_OR_EXPIRED("401", "token无效或已过期"),
    CLIENT_AUTHENTICATION_FAILED("401", "客户端认证失败"),
    TOKEN_ACCESS_FORBIDDEN("402", "token已被禁止访问"),
    SYSTEM_EXECUTION_ERROR("500", "系统执行出错"),
    UNKNOWN("-1", "未知的错误"),
    USER_NOT_EXIST("400", "用户不存在"),
    PARAM_ERROR("400", "用户请求参数错误"),
    RESOURCE_NOT_FOUND("404", "请求资源不存在"),
    ;

    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return SYSTEM_EXECUTION_ERROR;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
