package cn.zrj.mall.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zhaorujie
 * @version v1.0
 * @date 2023/7/3
 **/
public class InvalidCaptchaException extends AuthenticationException {
    public InvalidCaptchaException(String msg) {
        super(msg);
    }
}
