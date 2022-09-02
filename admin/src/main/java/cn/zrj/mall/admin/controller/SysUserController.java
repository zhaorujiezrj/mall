package cn.zrj.mall.admin.controller;

import cn.hutool.json.JSONObject;
import cn.zrj.mall.admin.dto.AuthUserDto;
import cn.zrj.mall.admin.service.SysUserService;
import cn.zrj.mall.common.core.exception.BusinessException;
import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.common.core.security.UserContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/v1/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "根据用户名获取认证信息", notes = "提供用于用户登录认证信息", hidden = true)
    @GetMapping("/username/{username}")
    public Result<AuthUserDto> getAuthInfoByUsername(@PathVariable String username) {
        return Result.success(sysUserService.getAuthInfoByUsername(username));
    }

    @GetMapping("/test")
    public Result<JSONObject> test() {
        JSONObject userInfo = UserContextHolder.getUserInfo();
        return Result.success(userInfo);
    }

    @GetMapping("exception")
    public Result<Void> exception() {
        throw new BusinessException("发生异常");
    }
}
