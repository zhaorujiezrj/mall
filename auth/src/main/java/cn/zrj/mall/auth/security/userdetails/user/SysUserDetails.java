package cn.zrj.mall.auth.security.userdetails.user;

import cn.zrj.mall.auth.dto.AuthUserDto;
import cn.zrj.mall.auth.enums.PasswordEncoderTypeEnum;
import cn.zrj.mall.common.core.constant.GlobalConstants;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Data
public class SysUserDetails implements UserDetails, CredentialsContainer, Serializable {
    private static final long serialVersionUID = -7442638371214528947L;
    private Long userId;
    private String username;
    private String password;
    private Boolean enabled;
    private String mobile;
    private Collection<SimpleGrantedAuthority> authorities;

    public SysUserDetails(AuthUserDto user) {
        this.setUserId(user.getUserId());
        this.setUsername(user.getUsername());
        this.setPassword(PasswordEncoderTypeEnum.BCRYPT.getPrefix() + user.getPassword());
        this.setEnabled(GlobalConstants.STATUS_YES.equals(user.getStatus()));
        if (CollectionUtils.isNotEmpty(user.getRoles())) {
            authorities = user.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
    }

    public SysUserDetails(Long userId, String username, String password, Boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public SysUserDetails(Long userId, String username, String password, boolean enabled, String mobile, Set<? extends GrantedAuthority> authorities) {
        Assert.isTrue(username != null && !"".equals(username) && password != null,
                "Cannot pass null or empty values to constructor");
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.mobile = mobile;
        this.authorities = (Collection<SimpleGrantedAuthority>) Collections.unmodifiableSet(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

}
