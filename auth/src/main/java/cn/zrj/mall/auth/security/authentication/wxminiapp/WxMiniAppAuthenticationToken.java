package cn.zrj.mall.auth.security.authentication.wxminiapp;

import cn.zrj.mall.auth.security.OAuth2GrantType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author rujie_z
 */
public class WxMiniAppAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final Set<String> scopes;
    private final String code;
    private final String mobileAuthCode;


    public WxMiniAppAuthenticationToken(Authentication clientPrincipal,
                                        @Nullable Set<String> scopes,
                                        @Nullable Map<String, Object> additionalParameters,
                                        @Nullable String code,
                                        String mobileAuthCode) {
        super(OAuth2GrantType.WX_APP, clientPrincipal, additionalParameters);
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        this.scopes = Collections.unmodifiableSet(CollectionUtils.isNotEmpty(scopes) ? new HashSet<>(scopes) : Collections.emptySet());
        this.code = code;
        this.mobileAuthCode = mobileAuthCode;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getCode() {
        return code;
    }

    public String getMobileAuthCode() {
        return mobileAuthCode;
    }
}