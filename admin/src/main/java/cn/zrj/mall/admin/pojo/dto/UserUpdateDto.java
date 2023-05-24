package cn.zrj.mall.admin.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class UserUpdateDto {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "姓名")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态(1:正常;0:禁用)")
    private Integer status;

    @Schema(description = "角色id数组")
    private List<Long> roleIds;
}
