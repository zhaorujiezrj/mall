package cn.zrj.mall.admin.pojo.query;

import cn.zrj.mall.common.core.base.BasePageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/8
 */
@Data
public class UserPageQuery extends BasePageQuery {

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "用户名称")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "状态（1:正常;0:禁用）")
    private Integer status;

}
