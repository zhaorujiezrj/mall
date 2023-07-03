package cn.zrj.mall.auth.security.extension.mobile;

import cn.zrj.mall.auth.security.OAuth2GrantType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;

/**
 * 手机号验证码认证
 * @author zhaorujie
 * @date 2022/8/22
 */
public class SmsCodeAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private Set<String> scopes;

    private String code;

    private String mobile;

    public SmsCodeAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
    }

    public SmsCodeAuthenticationToken(Authentication clientPrincipal,
                                      Map<String, Object> additionalParameters,
                                      Set<String> scopes,
                                      String code,
                                      String mobile) {
        super(OAuth2GrantType.SMS_CODE, clientPrincipal, additionalParameters);
        Assert.notNull(code, "code is not null");
        Assert.notNull(mobile, "mobile is not null");
        this.scopes = scopes;
        this.code = code;
        this.mobile = mobile;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getCode() {
        return code;
    }

    public String getMobile() {
        return mobile;
    }
}
