package cn.zrj.mall.auth.security.extension.wechat;

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

public class OAuth2WeChatAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private final Set<String> scopes;
    private final String code;
    private final String encryptedData;
    private final String iv;


    public OAuth2WeChatAuthenticationToken(Authentication clientPrincipal,
                                           @Nullable Set<String> scopes,
                                           @Nullable Map<String, Object> additionalParameters,
                                           @Nullable String code,
                                           @Nullable String encryptedData,
                                           @Nullable String iv) {
        super(OAuth2GrantType.SMS_CODE, clientPrincipal, additionalParameters);
        Assert.notNull(clientPrincipal, "clientPrincipal cannot be null");
        this.scopes = Collections.unmodifiableSet(CollectionUtils.isNotEmpty(scopes) ? new HashSet<>(scopes) : Collections.emptySet());
        this.code = code;
        this.encryptedData = encryptedData;
        this.iv = iv;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getCode() {
        return code;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getIv() {
        return iv;
    }
}