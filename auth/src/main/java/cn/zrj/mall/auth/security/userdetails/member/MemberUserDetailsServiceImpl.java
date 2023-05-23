package cn.zrj.mall.auth.security.userdetails.member;

import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.client.MemberClient;
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
@Service("memberUserDetailsService")
public class MemberUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberClient memberClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    /**
     * openId 认证方式
     * @param openId
     * @return
     */
    public UserDetails loadUserByOpenId(String openId) {
        Result<MemberAuthDto> result = memberClient.getMemberByOpenid(openId);
        return getUserDetails(result);
    }

    /**
     * 手机号认证方式
     * @param mobile
     * @return
     */
    public UserDetails loadUserByMobile(String mobile) {
        Result<MemberAuthDto> result = memberClient.getMemberByMobile(mobile);
        return getUserDetails(result);
    }

    public UserDetails getUserDetails(Result<MemberAuthDto> result) {
        UserDetails userDetails = null;
        if (Result.isSuccess(result)) {
            MemberAuthDto data = result.getData();
            if (Objects.nonNull(data)) {
                userDetails = new MemberUserDetails(data);
            }
        }
        if (Objects.isNull(userDetails)) {
            throw new UsernameNotFoundException(ResultCode.USER_NOT_EXIST.getMsg());
        } else if (!userDetails.isEnabled()) {
            throw new DisabledException("该账户已被禁用!");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("该账号已被锁定!");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("该账号已过期!");
        }

        return userDetails;
    }
}
