package cn.zrj.mall.admin.controller;

import cn.zrj.mall.admin.pojo.dto.AuthUserDto;
import cn.zrj.mall.admin.pojo.dto.UserAddDto;
import cn.zrj.mall.admin.pojo.dto.UserUpdateDto;
import cn.zrj.mall.admin.pojo.query.UserPageQuery;
import cn.zrj.mall.admin.service.SysUserService;
import cn.zrj.mall.admin.pojo.vo.user.UserVo;
import cn.zrj.mall.common.core.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/v1/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @ApiOperation(value = "根据用户名获取认证信息", notes = "提供用于用户登录认证信息", hidden = true)
    @GetMapping("/username/{username}")
    public Result<AuthUserDto> getAuthInfoByUsername(@PathVariable String username) {
        return Result.success(sysUserService.getAuthInfoByUsername(username));
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/pages")
    public Result<IPage<UserVo>> getUserListPage(UserPageQuery query) {
        IPage<UserVo> data = sysUserService.getUserListPage(query);
        return Result.success(data);
    }

    @ApiOperation(value = "添加用户")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody UserAddDto userAddDto) {
        return Result.success(sysUserService.add(userAddDto));
    }

    @ApiOperation(value = "编辑用户")
    @PutMapping
    public Result<Boolean> update(@RequestBody UserUpdateDto userUpdateDto) {
        return Result.success(sysUserService.update(userUpdateDto));
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysUserService.delete(id));
    }

    @ApiOperation(value = "修改状态")
    @PostMapping("/status/{id}/{status}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        return Result.success(sysUserService.updateStatus(id, status));
    }
}
