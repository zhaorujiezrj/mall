package cn.zrj.mall.auth.security.userdetails.member;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.client.MemberClient;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Slf4j
@Service("memberUserDetailsService")
public class MemberUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private WxMaService wxMaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    /**
     * openId 认证方式
     * @param openId
     * @return
     */
    public UserDetails loadUserByOpenId(String openId, String mobileAuthCode) {
        Result<MemberAuthDto> result = memberClient.getMemberByOpenid(openId);
        if (!Result.isSuccess(result)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "获取用户信息失败!", null));
        }
        MemberAuthDto authDto = result.getData();
        //首次登录需要授权手机号登录，后面的登录则不需要
        if (authDto == null && StringUtils.isBlank(mobileAuthCode)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.PLEASE_MOBILE_AUTH.getCode(), ResultCode.PLEASE_MOBILE_AUTH.getMsg(), null));
        }
        if (StringUtils.isNotBlank(mobileAuthCode)) {
            if (authDto == null || StringUtils.isBlank(authDto.getMobile())) {
                WxMaPhoneNumberInfo phoneNoInfo;
                try {
                    phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(mobileAuthCode);
                } catch (Exception e) {
                    throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "手机号授权失败", null));
                }
                String phoneNumber = phoneNoInfo.getPhoneNumber();
                log.info("微信小程序用户手机号解密【phoneNumber = {}，countryCode = {}】", phoneNumber, phoneNoInfo.getCountryCode());
                Result<MemberAuthDto> memberResult = memberClient.getMemberByMobile(phoneNumber);
                if (!Result.isSuccess(memberResult)) {
                    throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "获取用户信息失败!", null));
                }
                MemberAuthDto data = memberResult.getData();
                //不存在则注册会员
                if (Objects.isNull(data)) {
                    MemberDto memberDto = new MemberDto();
                    memberDto.setOpenid(openId);
                    memberDto.setMobile(phoneNumber);
                    Result<Long> addMember = memberClient.addMember(memberDto);
                    data = new MemberAuthDto();
                    data.setMemberId(addMember.getData());
                    data.setStatus(1);
                    data.setUsername(phoneNumber);
                    data.setMobile(phoneNumber);
                } else {
                    MemberDto memberDto = new MemberDto();
                    memberDto.setId(data.getMemberId());
                    memberDto.setOpenid(openId);
                    memberDto.setMobile(phoneNumber);
                    memberClient.updateMember(memberDto);
                    data.setMobile(phoneNumber);
                }
                result = Result.success(data);
            }
        }
        return getUserDetails(result);
    }

    /**
     * 手机号认证方式
     * @param mobile
     * @return
     */
    public UserDetails loadUserByMobile(String mobile) {
        Result<MemberAuthDto> result = memberClient.getMemberByMobile(mobile);
        if (!Result.isSuccess(result)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ResultCode.SYSTEM_EXECUTION_ERROR.getCode(), "获取用户信息失败!", null));
        }
        MemberAuthDto data = result.getData();
        if (data == null) {
            MemberDto memberDto = new MemberDto();
            memberDto.setMobile(mobile);
            Result<Long> addMember = memberClient.addMember(memberDto);
            data = new MemberAuthDto();
            data.setMobile(mobile);
            data.setMemberId(addMember.getData());
            data.setStatus(1);
            data.setUsername(mobile);
        }
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
