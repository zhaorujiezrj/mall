package cn.zrj.mall.auth.security.userdetails.member;

import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.common.constant.GlobalConstants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Data
public class MemberUserDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 4768037462094113570L;
    private Long memberId;
    private String username;
    private Boolean enabled;
    private String mobile;

    public MemberUserDetails(MemberAuthDto memberAuthInfoDto) {
        this.setMemberId(memberAuthInfoDto.getMemberId());
        this.setUsername(memberAuthInfoDto.getUsername());
        this.setEnabled(Objects.equals(GlobalConstants.STATUS_YES, memberAuthInfoDto.getStatus()));
        this.setMobile(memberAuthInfoDto.getMobile());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getPassword() {
        return null;
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
