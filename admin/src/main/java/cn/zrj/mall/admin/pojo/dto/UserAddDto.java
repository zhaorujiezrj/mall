package cn.zrj.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhaorujie
 * @date 2022/9/8
 */
@Data
public class UserAddDto {

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    private String username;

    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "状态(1:正常;0:禁用)")
    @NotBlank(message = "状态不能为空")
    private Integer status;
}
