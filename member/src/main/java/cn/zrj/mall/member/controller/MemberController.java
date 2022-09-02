package cn.zrj.mall.member.controller;

import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.member.dto.MemberAuthDto;
import cn.zrj.mall.member.dto.MemberDto;
import cn.zrj.mall.member.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Api(tags = "APP-会员管理")
@RestController
@RequestMapping("/app-api/v1/member")
public class MemberController {

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation(value = "根据 openid 获取会员认证信息")
    @GetMapping("/openid/{openid}")
    public Result<MemberAuthDto> getMemberByOpenid(@ApiParam("微信身份标识") @PathVariable String openid) {
        MemberAuthDto memberAuthInfo = memberService.getMemberByOpenid(openid);
        return Result.success(memberAuthInfo);
    }

    /**
     * 根据手机号获取会员认证信息
     *
     * @param mobile 手机号码
     * @return
     */
    @GetMapping("/mobile/{mobile}")
    public Result<MemberAuthDto> getMemberByMobile(@ApiParam("手机号码") @PathVariable String mobile) {
        MemberAuthDto memberAuthInfo = memberService.getByMobile(mobile);
        return Result.success(memberAuthInfo);
    }

    @PostMapping
    public Result<Void> addMember(@RequestBody MemberDto memberDto) {
        memberService.addMember(memberDto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateMember(@RequestBody MemberDto memberDto) {
        memberService.updateMember(memberDto);
        return Result.success();
    }
}
