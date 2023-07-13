package cn.zrj.mall.auth.security.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import cn.zrj.mall.auth.security.authentication.captcha.CaptchaAuthenticationConverter;
import cn.zrj.mall.auth.security.authentication.captcha.CaptchaAuthenticationProvider;
import cn.zrj.mall.auth.security.handler.MyAuthenticationFailureHandler;
import cn.zrj.mall.auth.security.handler.MyAuthenticationSuccessHandler;
import cn.zrj.mall.auth.security.authentication.mobile.SmsCodeAuthenticationConverter;
import cn.zrj.mall.auth.security.authentication.mobile.SmsCodeAuthenticationProvider;
import cn.zrj.mall.auth.security.authentication.password.PasswordAuthenticationConverter;
import cn.zrj.mall.auth.security.authentication.password.PasswordAuthenticationProvider;
import cn.zrj.mall.auth.security.authentication.wxminiapp.WxMiniAppAuthenticationProvider;
import cn.zrj.mall.auth.security.authentication.wxminiapp.WxMiniAppAuthenticationConverter;
//import cn.zrj.mall.auth.security.point.LoginTargetAuthenticationEntryPoint;
//import cn.zrj.mall.auth.security.point.RedisSecurityContextRepository;
import cn.zrj.mall.auth.security.userdetails.member.deserializer.MemberUserMixin;
import cn.zrj.mall.auth.security.userdetails.user.deserializer.SysUserMixin;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetails;
import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetailsServiceImpl;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetails;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetailsServiceImpl;
import cn.zrj.mall.common.core.constant.RedisConstants;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceConfig {

    private final SysUserDetailsServiceImpl sysUserDetailsService;
    private final MemberUserDetailsServiceImpl memberUserDetailsService;
    private final WxMaService wxMaService;
    private final StringRedisTemplate redisTemplate;
//    private final RedisSecurityContextRepository redisSecurityContextRepository;

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    // http://localhost:9000/auth/oauth2/authorize?client_id=client&response_type=code&scope=message.read&redirect_uri=https://baidu.com

    //http://127.0.0.1:9000/auth/oauth2/authorize?client_id=client&response_type=code&scope=message.read&redirect_uri=https://www.baidu.com
    /**
     * 授权配置
     * 这是个Spring security 的过滤器链，默认会配置
     * <p>
     * OAuth2 Authorization endpoint
     * <p>
     * OAuth2 Token endpoint
     * <p>
     * OAuth2 Token Introspection endpoint
     * <p>
     * OAuth2 Token Revocation endpoint
     * <p>
     * OAuth2 Authorization Server Metadata endpoint
     * <p>
     * JWK Set endpoint
     * <p>
     * OpenID Connect 1.0 Provider Configuration endpoint
     * <p>
     * OpenID Connect 1.0 UserInfo endpoint
     * 这些协议端点，只有配置了他才能够访问的到接口地址（类似mvc的controller）。
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AuthenticationManager authenticationManager,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2TokenGenerator<?> tokenGenerator) throws Exception {
        // 使用redis存储、读取登录的认证信息
//        http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer.tokenEndpoint(endpoint -> {

            endpoint.accessTokenRequestConverters(authenticationConverters ->
                    authenticationConverters.addAll(
                            List.of(
//                                    new OAuth2AuthorizationCodeRequestAuthenticationConverter(),
//                                    new OAuth2AuthorizationCodeAuthenticationConverter(),
//                                    new OAuth2RefreshTokenAuthenticationConverter(),
//                                    new OAuth2ClientCredentialsAuthenticationConverter(),
                                    new PasswordAuthenticationConverter(),
                                    new SmsCodeAuthenticationConverter(),
                                    new WxMiniAppAuthenticationConverter(),
                                    new CaptchaAuthenticationConverter()
                            )
                    ));

            endpoint.authenticationProviders(authenticationProviders ->
                    authenticationProviders.addAll(
                            List.of(
                                    new PasswordAuthenticationProvider(authorizationService, tokenGenerator, authenticationManager),
                                    new SmsCodeAuthenticationProvider(authorizationService, tokenGenerator, memberUserDetailsService, redisTemplate),
                                    new WxMiniAppAuthenticationProvider(authorizationService, tokenGenerator, memberUserDetailsService, wxMaService),
                                    new CaptchaAuthenticationProvider(authenticationManager, authorizationService, tokenGenerator, redisTemplate))
                    ));

            endpoint.accessTokenResponseHandler(new MyAuthenticationSuccessHandler()) // 自定义成功响应
                    .errorResponseHandler(new MyAuthenticationFailureHandler()); // 自定义异常响应
        });

        http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher));

        http.apply(authorizationServerConfigurer);

//        http
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

        authorizationServerConfigurer
                .oidc(oidc -> {
                            // 用户信息
                            oidc.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(oidcUserInfoAuthenticationContext -> {
                                OAuth2AccessToken accessToken = oidcUserInfoAuthenticationContext.getAccessToken();
                                Map<String, Object> claims = new HashMap<>();
                                claims.put("url", "https://github.com/ITLab1024");
                                claims.put("accessToken", accessToken);
                                claims.put("sub", oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName());
                                return new OidcUserInfo(claims);

                            }));
                            // 客户端注册
                            oidc.clientRegistrationEndpoint(Customizer.withDefaults());
                        }
                );

//        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
//                // 开启OpenID Connect 1.0协议相关端点
//                .oidc(Customizer.withDefaults())
//                // 设置自定义用户确认授权页
//                .authorizationEndpoint(Customizer.withDefaults())
//                // 设置设备码用户验证url(自定义用户验证页)
//                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
//                        deviceAuthorizationEndpoint.verificationUri("/activate")
//                )
//                // 设置验证设备码用户确认页面
//                .deviceVerificationEndpoint(deviceVerificationEndpoint ->
//                        deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
//                );

        return http.build();
    }


    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(sysUserDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false) ; // 抛出用户不存在异常
        return daoAuthenticationProvider ;
    }

    /**
     * 密码编码器
     * <p>
     * 委托方式，根据密码的前缀选择对应的encoder，例如：{bcrypt}前缀->标识BCYPT算法加密；{noop}->标识不使用任何加密即明文的方式
     * 密码判读 DaoAuthenticationProvider#additionalAuthenticationChecks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate) {
        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate,
                registeredClientRepository(jdbcTemplate));

        //配置Jackson 反序列化白名单
        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper authorizationRowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
                registeredClientRepository(jdbcTemplate));
        authorizationRowMapper.setLobHandler(new DefaultLobHandler());

        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.addMixIn(SysUserDetails.class, SysUserMixin.class);
        objectMapper.addMixIn(MemberUserDetails.class, MemberUserMixin.class);
        objectMapper.addMixIn(Long.class, Object.class);
        authorizationRowMapper.setObjectMapper(objectMapper);

        service.setAuthorizationRowMapper(authorizationRowMapper);
        return service;
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * 用于给access_token签名使用。
     *
     * @return
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
//        KeyPair keyPair = generateRsaKey();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
//        JWKSet jwkSet = new JWKSet(rsaKey);
        KeyPair keyPair = keyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey()).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @SneakyThrows
    @Bean
    public RSAPublicKey rsaPublicKey() {
        Resource resource = new ClassPathResource("public.key");
        InputStream is = resource.getInputStream();
        String publicKeyData = IoUtil.read(is).toString();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((Base64.decode(publicKeyData)));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return factory.getKeyPair("jwt", "123456".toCharArray());
    }

    /**
     * 生成秘钥对，为jwkSource提供服务。
     *
     * @return
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(jwkSource());
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator() {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder());
        jwtGenerator.setJwtCustomizer(jwtCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwtClaimsSet.Builder claims = context.getClaims();
            Object principal = context.getPrincipal().getPrincipal();
            if (principal instanceof SysUserDetails sysUserDetails)  {
                claims.claim("userId", sysUserDetails.getUserId());
                claims.claim("username", sysUserDetails.getUsername());
                // 这里存入角色至JWT，解析JWT的角色用于鉴权的位置: ResourceServerConfig#jwtAuthenticationConverter
                var authorities = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                        .stream()
                        .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                claims.claim(RedisConstants.AUTHORITIES_CLAIM_NAME_KEY, authorities);

                // 权限数据比较多，缓存至redis
                Set<String> perms = sysUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                redisTemplate.opsForValue().set(RedisConstants.AUTHORITIES_CLAIM_NAME_KEY + sysUserDetails.getUserId(), JSONUtil.toJsonStr(perms));
            } else if (principal instanceof MemberUserDetails memberUserDetails) {
                claims.claim("memberId", memberUserDetails.getMemberId());
                claims.claim("username", memberUserDetails.getUsername());
                claims.claim("mobile", memberUserDetails.getMobile());
            }
        };
    }

    /**
     * 自定义jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
     *
     * @return jwt解析器 JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // 设置权限信息在jwt claims中的key
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
