package cn.zrj.mall.auth.feign;

import cn.zrj.mall.auth.dto.OAuth2ClientDTO;
import cn.zrj.mall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhaorujie
 * @date 2022/9/22
 */
@FeignClient(value = "admin")
public interface Oauth2FeignClient {

    @GetMapping("/api/v1/oauth-client/getOAuth2ClientById")
    Result<OAuth2ClientDTO> getDetailsByClientId(@RequestParam String clientId);
}
