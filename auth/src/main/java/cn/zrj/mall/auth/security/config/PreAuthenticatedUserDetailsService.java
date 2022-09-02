package cn.zrj.mall.auth.security.config;

import cn.zrj.mall.auth.security.extension.mobile.SmsCodeAuthenticationToken;
import cn.zrj.mall.auth.security.extension.wechat.WeChatAuthenticationToken;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetailsServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * @author zhaorujie
 * @date 2022/9/1
 */
@Data
@Slf4j
public class PreAuthenticatedUserDetailsService<T extends Authentication> implements AuthenticationUserDetailsService<T> {

    private SysUserDetailsServiceImpl sysUserDetailsService;

    private MemberUserDetailsServiceImpl memberUserDetailsService;

    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        Object principal = authentication.getPrincipal();
        String name = authentication.getName();
        if (principal instanceof SmsCodeAuthenticationToken) {
            return memberUserDetailsService.loadUserByMobile(name);
        } else if (principal instanceof WeChatAuthenticationToken) {
            return memberUserDetailsService.loadUserByOpenId(name);
        } else {
            return sysUserDetailsService.loadUserByUsername(name);
        }
    }
}
