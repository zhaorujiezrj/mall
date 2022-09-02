package cn.zrj.mall.auth.feign;

import cn.zrj.mall.auth.dto.MemberAuthDto;
import cn.zrj.mall.auth.dto.MemberDto;
import cn.zrj.mall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@FeignClient(value = "member")
public interface MemberFeignClient {

    @GetMapping("/app-api/v1/member/openid/{openid}")
    Result<MemberAuthDto> getMemberByOpenid(@PathVariable String openid);

    @GetMapping("/app-api/v1/member/mobile/{mobile}")
    Result<MemberAuthDto> getMemberByMobile(@PathVariable String mobile);

    @PostMapping("/app-api/v1/member")
    Result<Void> addMember(@RequestBody MemberDto memberDto);

    @PutMapping("/app-api/v1/member")
    Result<Void> updateMember(@RequestBody MemberDto memberDto);
}
