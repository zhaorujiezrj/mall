package cn.zrj.mall.admin.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class UserUpdateDto {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "状态(1:正常;0:禁用)")
    private Integer status;

    @ApiModelProperty(value = "角色id数组")
    private List<Long> roleIds;
}
