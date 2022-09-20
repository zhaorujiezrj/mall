package cn.zrj.mall.auth.feign;

import cn.zrj.mall.auth.dto.AuthUserDto;
import cn.zrj.mall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@FeignClient(value = "admin")
public interface SysUserFeignClient {

    @GetMapping("/api/v1/sys/user/username/{username}")
    Result<AuthUserDto> getAuthInfoByUsername(@PathVariable String username);
}
