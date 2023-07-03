package cn.zrj.mall.auth.security;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

/**
 * @author zhaorujie
 * @date 2022/9/29
 */
public class OAuth2GrantType {

    public static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);

    public static final String SMS_CODE_GRANT_TYPE = "sms_code";
    public static final AuthorizationGrantType SMS_CODE = new AuthorizationGrantType(SMS_CODE_GRANT_TYPE);

    public static final String WX_APP_GRANT_TYPE = "wx_app";
    public static final AuthorizationGrantType WX_APP = new AuthorizationGrantType(WX_APP_GRANT_TYPE);


    public static final String CAPTCHA = "captcha";

}
