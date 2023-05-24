package cn.zrj.mall.admin.controller;

import cn.zrj.mall.admin.pojo.dto.RoleDto;
import cn.zrj.mall.admin.pojo.query.RolePageQuery;
import cn.zrj.mall.admin.pojo.vo.role.RoleVo;
import cn.zrj.mall.admin.service.SysRoleService;
import cn.zrj.mall.common.core.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/api/v1/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询")
    @GetMapping("/pages")
    public Result<IPage<RoleVo>> getRolePages(RolePageQuery query) {
        return Result.success(sysRoleService.getRolePages(query));
    }

    @Operation(summary = "添加角色")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody RoleDto roleDto) {
        return Result.success(sysRoleService.save(null, roleDto));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        return Result.success(sysRoleService.save(id, roleDto));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysRoleService.deleteById(id));
    }

}
