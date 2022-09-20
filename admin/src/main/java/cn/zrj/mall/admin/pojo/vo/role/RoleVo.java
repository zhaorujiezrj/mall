package cn.zrj.mall.admin.pojo.vo.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class RoleVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态(1-正常；0-停用)")
    private Integer status;
}
