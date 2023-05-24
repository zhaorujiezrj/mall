package cn.zrj.mall.admin.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author zhaorujie
 * @date 2022/9/8
 */
@Data
public class UserAddDto {

    @Schema(description = "账号")
    @NotBlank(message = "账号不能为空")
    private String username;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String nickname;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "状态(1:正常;0:禁用)")
    @NotBlank(message = "状态不能为空")
    private Integer status;
}
