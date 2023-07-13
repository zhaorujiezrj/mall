package cn.zrj.mall.common.core.constant;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
public interface RedisConstants {

    String OAUTH_AUTH = "OAUTH:AUTH:";

    String AUTH = OAUTH_AUTH + "auth:";

    String BUSINESS = "business:";

    String BUSINESS_NO = BUSINESS + "no:";

    String ORDER_SN_PREFIX = "order:sn:";

    String ROLE_MENU_RELATION = "admin:roleMenuRelation:";

    String USER_MENU_RELATION = "admin:userMenuRelation:";

    String CAPTCHA_CODE = "captchaCode:";
    String MOBILE_CODE = "mobileCode:";

    /**
     * 授权信息中的权限或角色的key
     */
    String AUTHORITIES_CLAIM_NAME_KEY= "authorities:";

    /**
     * 认证信息存储前缀
     */
    String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";

    long DEFAULT_TIMEOUT_SECONDS = 7200;
}
