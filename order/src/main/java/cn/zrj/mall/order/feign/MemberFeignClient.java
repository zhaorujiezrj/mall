package cn.zrj.mall.order.feign;

import cn.zrj.mall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@FeignClient("member")
public interface MemberFeignClient {

    @GetMapping("/app-api/v1/member/id/{id}")
    Result<String> getOpenidById(@PathVariable Long id);
}
