package cn.zrj.mall.member.controller;

import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.member.dto.MemberAuthDto;
import cn.zrj.mall.member.dto.MemberDto;
import cn.zrj.mall.member.entity.UmsMember;
import cn.zrj.mall.member.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Tag(name = "APP-会员管理")
@RestController
@RequestMapping("/app-api/v1/member")
public class MemberController {

    @Autowired
    private UmsMemberService memberService;

    @Operation(summary = "根据 openid 获取会员认证信息")
    @GetMapping("/openid/{openid}")
    public Result<MemberAuthDto> getMemberByOpenid(@Parameter(ref = "微信身份标识") @PathVariable String openid) {
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
    public Result<MemberAuthDto> getMemberByMobile(@Parameter(ref = "手机号码") @PathVariable String mobile) {
        MemberAuthDto memberAuthInfo = memberService.getByMobile(mobile);
        return Result.success(memberAuthInfo);
    }

    @PostMapping
    public Result<Long> addMember(@RequestBody MemberDto memberDto) {
        return Result.success(memberService.addMember(memberDto));
    }

    @PutMapping
    public Result<Void> updateMember(@RequestBody MemberDto memberDto) {
        memberService.updateMember(memberDto);
        return Result.success();
    }

    @GetMapping("id/{id}")
    public Result<String> getOpenidById(@PathVariable Long id) {
        UmsMember member = Optional.ofNullable(memberService.getById(id)).orElseThrow(() -> new BusinessException("用户不存在！"));
        return Result.success(member.getOpenid());
    }
}
