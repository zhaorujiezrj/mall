package cn.zrj.mall.admin.pojo.query;

import cn.zrj.mall.common.core.base.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class RolePageQuery extends BasePageQuery {

    @ApiModelProperty(value = "编码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态(1-正常；0-停用)")
    private Integer status;
}
