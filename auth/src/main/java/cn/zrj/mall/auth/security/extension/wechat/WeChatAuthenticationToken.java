package cn.zrj.mall.auth.security.extension.wechat;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
public class WeChatAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final String encryptedData;

    private final String iv;

    public WeChatAuthenticationToken(Object principal, String encryptedData, String iv) {
        super(new HashSet<>());
        this.principal = principal;
        this.encryptedData = encryptedData;
        this.iv = iv;
        this.setAuthenticated(false);
    }

    public WeChatAuthenticationToken(Object principal, String encryptedData, String iv,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.encryptedData = encryptedData;
        this.iv = iv;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getIv() {
        return iv;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        Assert.isTrue(!authenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
