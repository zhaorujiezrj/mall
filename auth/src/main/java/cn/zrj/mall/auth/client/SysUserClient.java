package cn.zrj.mall.auth.client;

import cn.zrj.mall.auth.dto.AuthUserDto;
import cn.zrj.mall.common.core.result.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@HttpExchange(accept = "application/json", contentType = "application/json")
public interface SysUserClient {

    @GetExchange("/api/v1/sys/user/username/{username}")
    Result<AuthUserDto> getAuthInfoByUsername(@PathVariable String username);
}
