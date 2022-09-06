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
}
