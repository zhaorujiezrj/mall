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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.Disposable;
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
    private final WebClient.Builder webClient;

    public GatewayGlobalFilter(StringRedisTemplate redisTemplate,
                               RestTemplate restTemplate,
                               SecurityProperties properties,
                               WebClient.Builder webClient) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.webClient = webClient;
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
        //对校验通过的token进行续期
        //调用认证服务对token进行判断，当有效期小于指定的时间，就对token进行续期
        if (properties.isEnabled()) {
            //由于在gateway网关注入feign会卡死，可以通过applicationContext获取feign的bean对象，但调用feign的接口要对应改成异步，否则无法调用
            // 具体调用方式WebClient.Builder
            // 这里的“lb”也可以换成http
//            String finalToken = token;
//            Mono<Object> mono = webClient.baseUrl("lb://auth").build().get().uri(uriBuilder ->
//                    uriBuilder.path("/oauth/refreshToken")
//                            .queryParam("accessToken", finalToken)
//                            .build())
//                    .header(HttpHeaders.AUTHORIZATION, token)
//                    .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Object.class));
//            // 不调用subscribe或者block是不会调用服务的
//            mono.subscribe();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
