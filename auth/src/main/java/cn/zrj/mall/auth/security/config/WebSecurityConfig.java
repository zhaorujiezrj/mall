package cn.zrj.mall.auth.security.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.zrj.mall.auth.security.handler.MyAuthenticationFailureHandler;
import cn.zrj.mall.auth.security.handler.MyAuthenticationSuccessHandler;
//import cn.zrj.mall.auth.security.point.LoginTargetAuthenticationEntryPoint;
//import cn.zrj.mall.auth.security.point.RedisSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {


    @Setter
    private List<String> ignoreUrls;

//    private final RedisSecurityContextRepository redisSecurityContextRepository;

    /**
     * Spring Security 安全过滤器链配置
     *
     * @param http 安全配置
     * @return
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize  ->
                        {
                            if (CollectionUtil.isNotEmpty(ignoreUrls)) {
                                // 白名单①：走 springSecurityFilterChain 过滤器链(场景：验证码登录走验证码过滤器)
                                authorize .requestMatchers(Convert.toStrArray(ignoreUrls)).permitAll();
                            }
                            authorize.requestMatchers("/webjars/**",
                                    "/doc.html",
                                    "/swagger-resources/**",
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/login").permitAll();
                            authorize.anyRequest().authenticated();
                        }
                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
//                .formLogin(Customizer.withDefaults())
                // 指定登录页面
//                .formLogin(formLogin ->
//                        formLogin.loginPage("/login")
//                                // 登录成功和失败改为写回json，不重定向了
////                                .successHandler(new MyAuthenticationSuccessHandler())
////                                .failureHandler(new MyAuthenticationFailureHandler())
//                )
        ;

//        http
//                // 当未登录时访问认证端点时重定向至login页面
//                .exceptionHandling((exceptions) -> exceptions
//                        .defaultAuthenticationEntryPointFor(
//                                new LoginUrlAuthenticationEntryPoint("/login"),
//                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                        )
//                );

        // 使用redis存储、读取登录的认证信息
//        http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

        return http.build();
    }


    /**
     * Spring Security 自定义安全配置
     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) ->
//                // 白名单②: 不走过滤器链(场景：静态资源js、css、html)
//                web.ignoring().requestMatchers(
//                        "/webjars/**",
//                        "/doc.html",
//                        "/swagger-resources/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui/**",
//                        "/login"
//                );
//    }



}
