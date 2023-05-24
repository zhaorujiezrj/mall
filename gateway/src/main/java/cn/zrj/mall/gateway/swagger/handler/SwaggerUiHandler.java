//package cn.zrj.mall.gateway.swagger.handler;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.server.HandlerFunction;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Mono;
//
//import java.util.Optional;
//
///**
// * UI处理器
// * @author zhaorujie
// * @date 2022/9/21
// */
//@Component
//public class SwaggerUiHandler implements HandlerFunction<ServerResponse> {
//
//    @Autowired(required = false)
//    private  UiConfiguration  uiConfiguration;
//
//    @Override
//    public Mono<ServerResponse> handle(ServerRequest request) {
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters
//                        .fromValue(Optional.ofNullable(uiConfiguration)
//                                .orElse(UiConfigurationBuilder.builder().build())));
//    }
//}
