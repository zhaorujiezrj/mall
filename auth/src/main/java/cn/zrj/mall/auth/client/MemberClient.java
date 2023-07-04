package cn.zrj.mall.auth.client;

import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.common.core.result.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@HttpExchange(accept = "application/json", contentType = "application/json")
public interface MemberClient {

    @GetExchange("/app-api/v1/member/openid/{openid}")
    Result<MemberAuthDto> getMemberByOpenid(@PathVariable String openid);

    @GetExchange("/app-api/v1/member/mobile/{mobile}")
    Result<MemberAuthDto> getMemberByMobile(@PathVariable String mobile);

    @PostExchange("/app-api/v1/member")
    Result<Void> addMember(@RequestBody MemberDto memberDto);

    @PutExchange("/app-api/v1/member")
    Result<Void> updateMember(@RequestBody MemberDto memberDto);
}
