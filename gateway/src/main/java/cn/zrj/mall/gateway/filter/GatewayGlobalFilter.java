package cn.zrj.mall.gateway.filter;

import cn.zrj.mall.common.core.constant.RedisConstants;
import cn.zrj.mall.common.core.constant.SecurityConstants;
import cn.zrj.mall.common.core.result.ResultCode;
import cn.zrj.mall.gateway.autoconfigure.SecurityProperties;
import cn.zrj.mall.gateway.util.WebFluxUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/16
 */
@Configuration
@Slf4j
public class GatewayGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate redisTemplate;

    private final RestTemplate restTemplate;

    private final SecurityProperties properties;

    public GatewayGlobalFilter(StringRedisTemplate redisTemplate,
                               RestTemplate restTemplate,
                               SecurityProperties properties) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    //    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS)) {
            log.error("OPTIONS 请求不做处理，直接放行！");
            return chain.filter(exchange);
        }

        // 非JWT放行不做后续解析处理
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            return chain.filter(exchange);
        }
        token = token.replace("Bearer ", "bearer ");
        if (!token.startsWith(SecurityConstants.BEARER)) {
            return chain.filter(exchange);
        }
        token = token.replaceAll("^[Bb]earer\\s+", "");
        Boolean hasKey = redisTemplate.hasKey(RedisConstants.AUTH + token);
        if (Boolean.FALSE.equals(hasKey)) {
            return WebFluxUtils.writeResponse(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
        }
        //对校验通过的token进行续期
        //调用认证服务对token进行判断，当有效期小于指定的时间，就对token进行续期
        if (properties.isEnabled()) {
            restTemplate.getForEntity(properties.getRefreshTokenUrl() + token, Void.class);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
