package cn.zrj.mall.order.autoconfigure;

import cn.zrj.mall.order.client.MemberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Spring Boot 3 移除openfeign，使用内置的http客户端
 * @author zhaorujie
 * @version v1.0
 * @date 2023/5/23
 **/
@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    public MemberClient memberClient() {
        WebClient client = WebClient.builder().filter(reactorLoadBalancerExchangeFilterFunction).baseUrl("lb://member").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(MemberClient.class);
    }



}
