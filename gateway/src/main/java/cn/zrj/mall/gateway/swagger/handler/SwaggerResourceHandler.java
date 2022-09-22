package cn.zrj.mall.gateway.swagger.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * 聚合各个服务的swagger接口
 * @author zhaorujie
 * @date 2022/9/21
 */
@Component
@RequiredArgsConstructor
public class SwaggerResourceHandler implements HandlerFunction<ServerResponse> {

    private final SwaggerResourcesProvider swaggerResources;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters
                        .fromValue(swaggerResources.get()));
    }

}
