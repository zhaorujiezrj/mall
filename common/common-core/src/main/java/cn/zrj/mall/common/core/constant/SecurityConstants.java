package cn.zrj.mall.common.core.constant;

/**
 * @author zhaorujie
 * @date 2022/8/23
 */
public interface SecurityConstants {
    /**
     * 请求头
     */
     String AUTHENTICATION = "Authorization";

     String BEARER = "bearer ";

    /**
     * 随机字符串请求头名字
     */
    String NONCE_HEADER_NAME = "nonce";
}
