package cn.zrj.mall.auth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.auth.feign.MemberFeignClient;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Slf4j
public class WeChatAuthenticationProvider implements AuthenticationProvider {

    private final WxMaService wxMaService;

    private final StringRedisTemplate redisTemplate;

    private final MemberFeignClient memberFeignClient;

    private final UserDetailsService userDetailsService;

    public WeChatAuthenticationProvider(WxMaService wxMaService,
                                        StringRedisTemplate redisTemplate,
                                        MemberFeignClient memberFeignClient,
                                        UserDetailsService userDetailsService) {
        this.wxMaService = wxMaService;
        this.redisTemplate = redisTemplate;
        this.memberFeignClient = memberFeignClient;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WeChatAuthenticationToken authenticationToken = (WeChatAuthenticationToken) authentication;
        String code = (String)authenticationToken.getPrincipal();
        String encryptedData = authenticationToken.getEncryptedData();
        String iv = authenticationToken.getIv();
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            String sessionKey = sessionInfo.getSessionKey();
            String openid = sessionInfo.getOpenid();
            String unionId = sessionInfo.getUnionid();
            log.info("微信小程序用户认证信息【sessionKey = {}，openid = {}， unionId = {}】", sessionKey, openid, unionId);

            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
            String phoneNumber = phoneNoInfo.getPhoneNumber();
            log.info("微信小程序用户手机号解密【phoneNumber = {}，countryCode = {}】", phoneNumber, phoneNoInfo.getCountryCode());
            Result<MemberAuthDto> memberResult = memberFeignClient.getMemberByMobile(phoneNumber);
            if (!Result.isSuccess(memberResult)) {
                throw new BusinessException("获取用户信息失败!");
            }
            MemberAuthDto data = memberResult.getData();
            //不存在则注册会员
            if (Objects.isNull(data)) {
                MemberDto memberDto = new MemberDto();
                memberDto.setOpenid(openid);
                memberDto.setMobile(phoneNumber);
                memberDto.setSessionKey(sessionKey);
                memberFeignClient.addMember(memberDto);
            } else {
                MemberDto memberDto = new MemberDto();
                memberDto.setId(data.getMemberId());
                memberDto.setOpenid(openid);
                memberDto.setMobile(phoneNumber);
                memberDto.setSessionKey(sessionKey);
                memberFeignClient.updateMember(memberDto);
            }

            UserDetails userDetails = ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
            WeChatAuthenticationToken result = new WeChatAuthenticationToken(userDetails, encryptedData, iv, new HashSet<>());
            result.setDetails(authentication.getDetails());
            return result;

        } catch (WxErrorException e) {
            log.error("微信小程序用户认证失败，原因：{}", e);
            throw new BusinessException("登录失败，原因：" + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WeChatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
