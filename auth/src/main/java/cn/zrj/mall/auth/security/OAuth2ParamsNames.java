package cn.zrj.mall.auth.security;

/**
 * @author zhaorujie
 * @date 2022/9/29
 */
public interface OAuth2ParamsNames {

    String CODE = "code";
    String MOBILE = "mobile";
    String MOBILE_AUTH_CODE = "mobileAuthCode";

    String PRINCIPAL = "java.security.Principal";

    // 验证码
    String VERIFY_CODE = "verifyCode";

    // 验证码key
    String VERIFY_CODE_KEY = "verifyCodeKey";
}
