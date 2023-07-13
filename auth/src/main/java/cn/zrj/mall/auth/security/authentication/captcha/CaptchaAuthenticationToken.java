package cn.zrj.mall.auth.security.authentication.captcha;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * @description: CaptchaAuthenticationToken
 * @author: zhaorujie
 * @date: 2023/7/11
 * @version: v1.0
 **/
public class CaptchaAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * 令牌申请访问范围
     */
    private final Set<String> scopes;

    /**
     * 授权类型(验证码: captcha)
     */
    public static final AuthorizationGrantType CAPTCHA = new AuthorizationGrantType("captcha");

    /**
     * 验证码模式身份验证令牌
     * @param clientPrincipal 客户端信息
     * @param scopes 令牌申请范围
     * @param additionalParameters 自定义额外参数(用户名、密码、验证码)
     */
    public CaptchaAuthenticationToken(Authentication clientPrincipal,
                                      Set<String> scopes,
                                      @NotNull Map<String, Object> additionalParameters) {
        super(CAPTCHA, clientPrincipal, additionalParameters);
        this.scopes = scopes;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    @Override
    public Object getCredentials() {
        return this.getAdditionalParameters().get(OAuth2ParameterNames.PASSWORD);
    }
}
