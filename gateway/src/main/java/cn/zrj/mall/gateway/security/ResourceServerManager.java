package cn.zrj.mall.gateway.security;

import cn.zrj.mall.common.core.constant.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 授权管理
 * @author zhaorujie
 * @date 2022/8/16
 */
@Configuration
@Slf4j
public class ResourceServerManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final StringRedisTemplate redisTemplate;

    private static final String IGNORE_URL = "/app-api";

    public ResourceServerManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 鉴权处理
     * @param authentication
     * @param authorizationContext
     * @return
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        //如果是options请求直接放行
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        PathMatcher antPathMatcher = new AntPathMatcher();
        String method = request.getMethod().name();
        String path = request.getURI().getPath();

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        //如果请求头没有携带token的话，直接拦截
        if (StringUtils.isBlank(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }
        // 商城移动端请求需认证不需鉴权放行（根据实际场景需求）
        if (path.contains(IGNORE_URL)) {
            return Mono.just(new AuthorizationDecision(true));
        }

        List<String> authorizedRoles = new ArrayList<>();

        // 是否需要鉴权，默认未设置拦截规则不需鉴权
        boolean requireCheck = false;
        if (!requireCheck) {
            //todo 现在默认全部放行
            return Mono.just(new AuthorizationDecision(true));
        }
        // 判断JWT中携带的用户角色是否有权限访问
        return authentication.filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(item -> {
                    // ROLE_ADMIN移除前缀ROLE_得到用户的角色编码ADMIN
                    String roleCode = StringUtils.replace(item, "ROLE_", "");
                    if (Objects.equals(roleCode, GlobalConstants.ROOT_ROLE_CODE)) {
                        return true;
                    }
                    return CollectionUtils.isNotEmpty(authorizedRoles) && authorizedRoles.contains(roleCode);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
