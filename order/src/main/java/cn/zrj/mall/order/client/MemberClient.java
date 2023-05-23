package cn.zrj.mall.order.client;

import cn.zrj.mall.common.core.result.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author zhaorujie
 * @date 2022/9/5
 */
@HttpExchange
public interface MemberClient {

    @GetExchange("/app-api/v1/member/id/{id}")
    Result<String> getOpenidById(@PathVariable Long id);
}
