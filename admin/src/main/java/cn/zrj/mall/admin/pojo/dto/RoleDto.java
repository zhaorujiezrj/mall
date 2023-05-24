package cn.zrj.mall.admin.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class RoleDto {

    @Schema(description = "编码")
    @NotBlank(message = "角色编码不能为空")
    private String code;

    @Schema(description = "名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @Schema(description = "状态(1-正常；0-停用)")
    @NotBlank(message = "状态不能为空")
    private Integer status;

    @Schema(description = "菜单数组id")
    private List<Long> menuIds;
}
