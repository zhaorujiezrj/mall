package cn.zrj.mall.admin.pojo.query;

import cn.zrj.mall.common.core.base.BasePageQuery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/8
 */
@Data
public class UserPageQuery extends BasePageQuery {

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "用户名称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态（1:正常;0:禁用）")
    private Integer status;

}
