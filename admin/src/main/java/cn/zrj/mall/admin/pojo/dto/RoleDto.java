package cn.zrj.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class RoleDto {

    @ApiModelProperty(value = "编码")
    @NotBlank(message = "角色编码不能为空")
    private String code;

    @ApiModelProperty(value = "名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @ApiModelProperty(value = "状态(1-正常；0-停用)")
    @NotBlank(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty(value = "菜单数组id")
    private List<Long> menuIds;
}
