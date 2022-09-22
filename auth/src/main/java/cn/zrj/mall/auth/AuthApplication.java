package cn.zrj.mall.auth;

import cn.zrj.mall.auth.feign.MemberFeignClient;
import cn.zrj.mall.auth.feign.SysUserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@SpringBootApplication
@EnableFeignClients(basePackageClasses = {SysUserFeignClient.class, MemberFeignClient.class})
@EnableDiscoveryClient
//开启Spring Cache缓存机制
@EnableCaching
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
