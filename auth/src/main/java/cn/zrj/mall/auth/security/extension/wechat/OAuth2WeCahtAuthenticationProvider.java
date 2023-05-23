package cn.zrj.mall.auth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.auth.client.MemberClient;
import cn.zrj.mall.auth.security.OAuth2GrantType;
import cn.zrj.mall.auth.security.OAuth2ParamsNames;
import cn.zrj.mall.auth.security.extension.mobile.OAuth2SmsCodeAuthenticationToken;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.utils.OAuth2AuthenticationProviderUtils;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.AuthenticationProvider;
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
public class OAuth2WeCahtAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);
    private final WxMaService wxMaService;
    private final MemberClient memberClient;

    public OAuth2WeCahtAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                              OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                              MemberUserDetailsServiceImpl memberUserDetailsService,
                                              WxMaService wxMaService,
                                              MemberClient memberClient) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.memberUserDetailsService = memberUserDetailsService;
        this.wxMaService = wxMaService;
        this.memberClient = memberClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2WeChatAuthenticationToken weChatAuthenticationToken = (OAuth2WeChatAuthenticationToken) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal = OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(authentication);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException("client is error");
        }
        if (!registeredClient.getAuthorizationGrantTypes().contains(OAuth2GrantType.SMS_CODE)) {
            throw new OAuth2AuthenticationException("unauthorized_client");
        }

        String code = weChatAuthenticationToken.getCode();
        String encryptedData = weChatAuthenticationToken.getEncryptedData();
        String iv = weChatAuthenticationToken.getIv();
        String openid;
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String sessionKey = sessionInfo.getSessionKey();
            openid = sessionInfo.getOpenid();
            String unionId = sessionInfo.getUnionid();
            log.info("微信小程序用户认证信息【sessionKey = {}，openid = {}， unionId = {}】", sessionKey, openid, unionId);

            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
            String phoneNumber = phoneNoInfo.getPhoneNumber();

            log.info("微信小程序用户手机号解密【phoneNumber = {}，countryCode = {}】", phoneNumber, phoneNoInfo.getCountryCode());
            Result<MemberAuthDto> memberResult = memberClient.getMemberByMobile(phoneNumber);
            if (!Result.isSuccess(memberResult)) {
                throw new BusinessException("获取用户信息失败!");
            }
            MemberAuthDto data = memberResult.getData();
            //不存在则注册会员
            if (Objects.isNull(data)) {
                MemberDto memberDto = new MemberDto();
                memberDto.setOpenid(openid);
                memberDto.setMobile(phoneNumber);
                memberDto.setSessionKey(sessionKey);
                memberClient.addMember(memberDto);
            } else {
                MemberDto memberDto = new MemberDto();
                memberDto.setId(data.getMemberId());
                memberDto.setOpenid(openid);
                memberDto.setMobile(phoneNumber);
                memberDto.setSessionKey(sessionKey);
                memberClient.updateMember(memberDto);
            }
        } catch (WxErrorException e) {
            log.error("微信小程序用户认证失败，原因：{}", e);
            throw new BusinessException("登录失败，原因：" + e.getMessage());
        }
        UserDetails userDetails = memberUserDetailsService.loadUserByOpenId(openid);
        weChatAuthenticationToken.setDetails(userDetails);
        final Authentication principal = weChatAuthenticationToken;

        // Default to configured scopes
        Set<String> authorizedScopes = registeredClient.getScopes();
        if (CollectionUtils.isNotEmpty(weChatAuthenticationToken.getScopes())) {
            for (String requestedScope : weChatAuthenticationToken.getScopes()) {
                if (!registeredClient.getScopes().contains(requestedScope)) {
                    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
                }
            }
            authorizedScopes = new LinkedHashSet<>(weChatAuthenticationToken.getScopes());
        }

        // @formatter:off
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(principal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
//                .authorization(authorization)
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(OAuth2GrantType.SMS_CODE)
                .authorizationGrant(weChatAuthenticationToken);
        // @formatter:on

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(principal.getName())
                .authorizationGrantType(OAuth2GrantType.SMS_CODE)
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
        return OAuth2SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
