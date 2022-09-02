package cn.zrj.mall.auth.security.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.zrj.mall.auth.security.extension.mobile.SmsCodeTokenGranter;
import cn.zrj.mall.auth.security.extension.wechat.WeChatTokenGranter;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetails;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetails;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetailsServiceImpl;
import cn.zrj.mall.common.constant.RedisConstants;
import cn.zrj.mall.common.result.Result;
import cn.zrj.mall.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.*;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServiceConfig extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;

    private final RedisConnectionFactory connectionFactory;

    private final AuthenticationManager authenticationManager;

    private final SysUserDetailsServiceImpl sysUserDetailsService;

    private final MemberUserDetailsServiceImpl memberUserDetailsService;

    public AuthorizationServiceConfig(DataSource dataSource,
                                      RedisConnectionFactory connectionFactory,
                                      AuthenticationManager authenticationManager,
                                      SysUserDetailsServiceImpl sysUserDetailsService,
                                      MemberUserDetailsServiceImpl memberUserDetailsService) {
        this.dataSource = dataSource;
        this.connectionFactory = connectionFactory;
        this.authenticationManager = authenticationManager;
        this.sysUserDetailsService = sysUserDetailsService;
        this.memberUserDetailsService = memberUserDetailsService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService());
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancerList = new ArrayList<>();
        tokenEnhancerList.add(tokenEnhancer());
        tokenEnhancerList.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancerList);

        List<TokenGranter> tokenGranterList = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));

        //添加手机号验证码授权模式
        tokenGranterList.add(new SmsCodeTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), authenticationManager));

        //添加微信小程序授权模式
        tokenGranterList.add(new WeChatTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), authenticationManager));

        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranterList);

        endpoints.tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
                .reuseRefreshTokens(true)
                .tokenGranter(compositeTokenGranter)
                .tokenStore(redisTokenStore())
                .tokenServices(tokenServices(endpoints))
                ;
    }

    private DefaultTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancerList = new ArrayList<>();
        tokenEnhancerList.add(tokenEnhancer());
        tokenEnhancerList.add(jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancerList);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setClientDetailsService(clientDetailsService());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);


        // 刷新token模式下，重写预认证提供者替换其AuthenticationManager，可自定义根据客户端ID和认证方式区分用户体系获取认证用户信息
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        PreAuthenticatedUserDetailsService<PreAuthenticatedAuthenticationToken> detailsService = new PreAuthenticatedUserDetailsService<>();
        detailsService.setSysUserDetailsService(sysUserDetailsService);
        detailsService.setMemberUserDetailsService(memberUserDetailsService);
        provider.setPreAuthenticatedUserDetailsService(detailsService);
        tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));

        /** refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
         *  1 重复使用：access_token过期刷新时， refresh_token过期时间未改变，仍以初次生成的时间为准
         *  2 非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效达到无需再次登录的目的
         */
        tokenServices.setReuseRefreshToken(true);
        return tokenServices;
    }

    /**
     * jwt token存储模式
     */
//    @Bean
//    public JwtTokenStore jwtTokenStore(){
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }

    /**
     * redis token存储模式
     * 使用redis存储token可以保证多次登录获取的token都是一样的，只要token没有过期
     * @return
     */
    @Bean
    public TokenStore redisTokenStore() {
        RedisTokenStore redisTokenStore = new RedisTokenStore(connectionFactory);
        redisTokenStore.setPrefix(RedisConstants.OAUTH_AUTH);
        return redisTokenStore;
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = MapUtil.newHashMap();
            Object principal = authentication.getUserAuthentication() != null ? authentication.getUserAuthentication().getPrincipal() : null;
            if (principal instanceof SysUserDetails) {
                SysUserDetails sysUserDetails = (SysUserDetails) principal;
                additionalInfo.put("userId", sysUserDetails.getUserId());
                additionalInfo.put("username", sysUserDetails.getUsername());
                additionalInfo.put("mobile", sysUserDetails.getMobile());
                // 认证身份标识(username:用户名；)
            } else if (principal instanceof MemberUserDetails) {
                MemberUserDetails memberUserDetails = (MemberUserDetails) principal;
                additionalInfo.put("memberId", memberUserDetails.getMemberId());
                additionalInfo.put("username", memberUserDetails.getUsername());
                additionalInfo.put("mobile", memberUserDetails.getMobile());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return factory.getKeyPair("jwt", "123456".toCharArray());
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            Result<Object> error = Result.error(ResultCode.CLIENT_AUTHENTICATION_FAILED);
            response.getWriter().print(JSONUtil.toJsonStr(error));
            response.getWriter().flush();
        };
    }

    /**
     * 对token进行续期
     * @return
     */
    @Bean
    @Primary
    public MyResourceServerTokenServices myResourceServerTokenServices() {
        MyResourceServerTokenServices resourceServerTokenServices = new MyResourceServerTokenServices();
        resourceServerTokenServices.setTokenStore(redisTokenStore());
        return resourceServerTokenServices;
    }
}
