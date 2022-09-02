package cn.zrj.mall.auth.security.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.zrj.mall.auth.feign.MemberFeignClient;
import cn.zrj.mall.auth.security.NoOpPasswordEncoder;
import cn.zrj.mall.auth.security.extension.mobile.SmsCodeAuthenticationProvider;
import cn.zrj.mall.auth.security.extension.wechat.WeChatAuthenticationProvider;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SysUserDetailsServiceImpl sysUserDetailsService;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private final MemberFeignClient memberFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final WxMaService wxMaService;

    public WebSecurityConfig(SysUserDetailsServiceImpl sysUserDetailsService,
                             MemberUserDetailsServiceImpl memberUserDetailsService,
                             MemberFeignClient memberFeignClient,
                             StringRedisTemplate redisTemplate,
                             WxMaService wxMaService) {
        this.sysUserDetailsService = sysUserDetailsService;
        this.memberUserDetailsService = memberUserDetailsService;

        this.memberFeignClient = memberFeignClient;
        this.redisTemplate = redisTemplate;
        this.wxMaService = wxMaService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/oauth/**", "/sms/**").permitAll()
                .antMatchers("/webjars/**", "/doc.html", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider())
                .authenticationProvider(smsCodeAuthenticationProvider())
                .authenticationProvider(weChatAuthenticationProvider())
        ;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(sysUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public SmsCodeAuthenticationProvider smsCodeAuthenticationProvider() {
        return new SmsCodeAuthenticationProvider(memberFeignClient, redisTemplate, memberUserDetailsService);
    }

    @Bean
    public WeChatAuthenticationProvider weChatAuthenticationProvider() {
        return new WeChatAuthenticationProvider(wxMaService, redisTemplate, memberFeignClient, memberUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new NoOpPasswordEncoder();
    }
}
