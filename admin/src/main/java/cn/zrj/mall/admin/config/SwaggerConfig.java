package cn.zrj.mall.admin.config;

import com.google.common.collect.Lists;

import com.nimbusds.jose.proc.SecurityContext;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/21
 */
@Configuration
@Slf4j
public class SwaggerConfig {

//    @Bean
//    public GroupedOpenApi restApi() {
//        //schema
//        List<GrantType> grantTypes = new ArrayList<>();
//        //密码模式
//        ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant("http://localhost:9000/auth/oauth/token");
//        grantTypes.add(resourceOwnerPasswordCredentialsGrant);
//        OAuth oAuth = new OAuthBuilder().name("oauth2")
//                .grantTypes(grantTypes).build();
//        //context
//        //scope方位
//        List<AuthorizationScope> scopes = new ArrayList<>();
//        scopes.add(new AuthorizationScope("read", "read  resources"));
//        scopes.add(new AuthorizationScope("write", "write resources"));
//        scopes.add(new AuthorizationScope("reads", "read all resources"));
//        scopes.add(new AuthorizationScope("writes", "write all resources"));
//
//        SecurityReference securityReference = new SecurityReference("oauth2", scopes.toArray(new AuthorizationScope[]{}));
//        SecurityContext securityContext = new SecurityContext(Lists.newArrayList(securityReference), PathSelectors.ant("/**"), null, null);
//        //schemas
//        List<SecurityScheme> securitySchemes = Lists.newArrayList(oAuth);
//        //securyContext
//        List<SecurityContext> securityContexts = Lists.newArrayList(securityContext);
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                // .apis(RequestHandlerSelectors.basePackage("com.youlai.admin.controller"))
//                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.any())
//                .build()
//                .securityContexts(securityContexts)
//                .securitySchemes(securitySchemes)
//                .apiInfo(apiInfo());
//    }

    @Bean
    public GroupedOpenApi userApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"cn.zrj.mall.admin"};
        return GroupedOpenApi.builder().group("mall")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }


    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("zhaorujie zhaorujiezrj@63.com");

        return new OpenAPI().info(new Info()
                .title("mall")
                .description("mall")
                .contact(contact)
                .version("3.0")
                .termsOfService("https://www.baidu.com")
                .license(new License().name("MIT")
                        .url("https://www.baidu.com")));
    }
}
