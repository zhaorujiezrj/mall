package cn.zrj.mall.admin.controller;

import cn.zrj.mall.admin.pojo.dto.MenuDto;
import cn.zrj.mall.admin.pojo.vo.menu.MenuTreeVo;
import cn.zrj.mall.admin.pojo.vo.menu.MenuVo;
import cn.zrj.mall.admin.service.SysMenuService;
import cn.zrj.mall.common.core.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/api/v1/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @ApiOperation(value = "列表查询")
    @GetMapping("/list")
    public Result<List<MenuVo>> getMenuList(String name, Integer status) {
        return Result.success(sysMenuService.getMenuList(name, status));
    }

    @ApiOperation(value = "菜单树查询")
    @GetMapping("/tree")
    public Result<List<MenuTreeVo>> getMenuTree(Integer status) {
        return Result.success(sysMenuService.getMenuTree(status));
    }

    @ApiOperation(value = "添加菜单")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody MenuDto menuDto) {
        return Result.success(sysMenuService.save(null, menuDto));
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @Valid @RequestBody MenuDto menuDto) {
        return Result.success(sysMenuService.save(id, menuDto));
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysMenuService.deleteById(id));
    }

}
