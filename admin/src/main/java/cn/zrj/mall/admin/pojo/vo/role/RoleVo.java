package cn.zrj.mall.admin.pojo.vo.role;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class RoleVo {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态(1-正常；0-停用)")
    private Integer status;
}
