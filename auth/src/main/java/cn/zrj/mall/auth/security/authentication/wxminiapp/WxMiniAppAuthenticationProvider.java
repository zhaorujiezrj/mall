package cn.zrj.mall.auth.security.authentication.wxminiapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.zrj.mall.auth.security.OAuth2GrantType;
import cn.zrj.mall.auth.security.OAuth2ParamsNames;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.utils.OAuth2AuthenticationProviderUtils;
import cn.zrj.mall.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Slf4j
public class WxMiniAppAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);
    private final WxMaService wxMaService;

    public WxMiniAppAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                           OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                           MemberUserDetailsServiceImpl memberUserDetailsService,
                                           WxMaService wxMaService) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.memberUserDetailsService = memberUserDetailsService;
        this.wxMaService = wxMaService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxMiniAppAuthenticationToken wxMiniAppAuthenticationToken = (WxMiniAppAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(authentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "client is error", null));
        }
        if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2GrantType.WX_APP)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "unauthorized_client", null));
        }

        String code = wxMiniAppAuthenticationToken.getCode();
        String mobileAuthCode = wxMiniAppAuthenticationToken.getMobileAuthCode();
        String openid;
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String sessionKey = sessionInfo.getSessionKey();
            openid = sessionInfo.getOpenid();
            String unionId = sessionInfo.getUnionid();
            log.info("微信小程序用户认证信息【sessionKey = {}，openid = {}， unionId = {}】", sessionKey, openid, unionId);
        } catch (WxErrorException e) {
            log.error("微信小程序用户认证失败，原因：{}", e.getMessage());
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(),"登录失败，原因：" + e.getMessage(), null));
        }
        UserDetails userDetails = memberUserDetailsService.loadUserByOpenId(openid, mobileAuthCode);
        final Authentication principal = new UsernamePasswordAuthenticationToken(userDetails, null);

        // Default to configured scopes
        Set<String> authorizedScopes = registeredClient.getScopes();
        if (CollectionUtils.isNotEmpty(wxMiniAppAuthenticationToken.getScopes())) {
            for (String requestedScope : wxMiniAppAuthenticationToken.getScopes()) {
                if (!registeredClient.getScopes().contains(requestedScope)) {
                    throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.PARAM_ERROR.getCode(), OAuth2ErrorCodes.INVALID_SCOPE, null));
                }
            }
            authorizedScopes = new LinkedHashSet<>(wxMiniAppAuthenticationToken.getScopes());
        }

        // @formatter:off
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
//                .authorization(authorization)
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(OAuth2GrantType.WX_APP)
                .authorizationGrant(wxMiniAppAuthenticationToken);
        // @formatter:on

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(principal.getName())
                .authorizationGrantType(OAuth2GrantType.WX_APP)
                .authorizedScopes(authorizedScopes)
                .attribute(OAuth2ParamsNames.PRINCIPAL, principal);


        // ----- Access token -----
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", ERROR_URI);
            throw new OAuth2AuthenticationException(error);
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
        if (generatedAccessToken instanceof ClaimAccessor) {
            authorizationBuilder.token(accessToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                // Do not issue refresh token to public client
                !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the refresh token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;

            authorizationBuilder.refreshToken(refreshToken);
        }

        // ----- ID token -----
        OidcIdToken idToken;
        if (authorizedScopes.contains(OidcScopes.OPENID)) {
            // @formatter:off
            tokenContext = tokenContextBuilder
                    .tokenType(ID_TOKEN_TOKEN_TYPE)
                    .authorization(authorizationBuilder.build())	// ID token customizer may need access to the access token and/or refresh token
                    .build();
            // @formatter:on
            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the ID token.", ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }


            idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
                    generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            authorizationBuilder.token(idToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
        } else {
            idToken = null;
        }

        OAuth2Authorization authorization = authorizationBuilder.build();

        this.authorizationService.save(authorization);

        Map<String, Object> additionalParameters = Collections.emptyMap();
        if (idToken != null) {
            additionalParameters = new HashMap<>(16);
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMiniAppAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
