package cn.zrj.mall.auth.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

/**
 * <p>Description: OAuth 2.0 Endpoint 工具方法类 </p>
 * <p>
 * 新版 spring-security-oauth2-authorization-server 很多代码都是“包”级可访问的，外部无法使用。为了方便扩展将其提取出来，便于使用。
 * <p>
 * 代码内容与原包代码基本一致。
 *
 * @author zhaorujie
 * @date 2022/9/27
 */

public class OAuth2AuthenticationProviderUtils {
    private OAuth2AuthenticationProviderUtils() {
    }

    public static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken)authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        } else {
            throw new OAuth2AuthenticationException("invalid_client");
        }
    }

    public static <T extends AbstractOAuth2Token> OAuth2Authorization invalidate(OAuth2Authorization authorization, T token) {
        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization).token(token, (metadata) -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true);
        });
        if (OAuth2RefreshToken.class.isAssignableFrom(token.getClass())) {
            authorizationBuilder.token(authorization.getAccessToken().getToken(), (metadata) -> {
                metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true);
            });
            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
            if (authorizationCode != null && !authorizationCode.isInvalidated()) {
                authorizationBuilder.token(authorizationCode.getToken(), (metadata) -> {
                    metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true);
                });
            }
        }

        return authorizationBuilder.build();
    }
}
