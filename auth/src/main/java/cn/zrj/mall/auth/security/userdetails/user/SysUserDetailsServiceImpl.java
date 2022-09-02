package cn.zrj.mall.auth.security.userdetails.user;

import cn.zrj.mall.auth.dto.AuthUserDto;
import cn.zrj.mall.auth.feign.SysUserFeignClient;
import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.common.core.result.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Service("sysUserDetailsService")
public class SysUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserFeignClient sysUserFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Result<AuthUserDto> result = sysUserFeignClient.getAuthInfoByUsername(username);
        UserDetails userDetails = null;
        if (Result.isSuccess(result)) {
            AuthUserDto data = result.getData();
            if (Objects.nonNull(data)) {
                userDetails = new SysUserDetails(data);
            }
        }
        if (userDetails == null) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if(!userDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("账号已被锁定!");
        }
        return userDetails;
    }
}
