package cn.zrj.mall.auth.security.userdetails.user;

import cn.zrj.mall.auth.dto.AuthUserDto;
import cn.zrj.mall.common.core.constant.GlobalConstants;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Data
public class SysUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = -7442638371214528947L;
    private Long userId;
    private String username;
    private String password;
    private Boolean enabled;
    private String mobile;
    private Collection<SimpleGrantedAuthority> authorities;

    public SysUserDetails(AuthUserDto authUserDto) {
        this.setUserId(authUserDto.getUserId());
        this.setUsername(authUserDto.getUsername());
        this.setPassword(authUserDto.getPassword());
        this.setEnabled(Objects.equals(GlobalConstants.STATUS_YES, authUserDto.getStatus()));
        this.setMobile(authUserDto.getMobile());
        if (CollectionUtils.isNotEmpty(authUserDto.getRoles())) {
            this.setAuthorities(authUserDto.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
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
}
