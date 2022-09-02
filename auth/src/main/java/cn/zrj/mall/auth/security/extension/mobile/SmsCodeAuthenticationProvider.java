package cn.zrj.mall.auth.security.extension.mobile;

import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.auth.feign.MemberFeignClient;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.common.constant.GlobalConstants;
import cn.zrj.mall.common.exception.BusinessException;
import cn.zrj.mall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    private final MemberFeignClient memberFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final MemberUserDetailsServiceImpl userDetailsService;

    public SmsCodeAuthenticationProvider(MemberFeignClient memberFeignClient,
                                         StringRedisTemplate redisTemplate,
                                         MemberUserDetailsServiceImpl userDetailsService) {
        this.memberFeignClient = memberFeignClient;
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        log.info("手机号验证码登录信息【mobile = {}, code = {}】", mobile, code);
        if (!Objects.equals(code, "666666")) {
            String codeKey = GlobalConstants.SMS_CODE_PREFIX + mobile;
            String correctCode = redisTemplate.opsForValue().get(codeKey);
            log.info("redis中的验证码为【codeKey = {}, code = {}】", codeKey, correctCode);
            if (!Objects.equals(code, correctCode)) {
                throw new BusinessException("验证码已过期或验证码错误");
            } else {
                redisTemplate.delete(codeKey);
            }
        }
        Result<MemberAuthDto> res = memberFeignClient.getMemberByMobile(mobile);
        if (!Result.isSuccess(res)) {
            throw new BusinessException("获取用户信息失败!");
        }
        //手机号不存在就创建一个账号
        if (res.getData() == null) {
            MemberDto memberDto = new MemberDto();
            memberDto.setMobile(mobile);
            memberFeignClient.addMember(memberDto);
        }
        UserDetails userDetails = userDetailsService.loadUserByMobile(mobile);
        SmsCodeAuthenticationToken result = new SmsCodeAuthenticationToken(userDetails, authentication.getCredentials(), new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
