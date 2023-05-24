package cn.zrj.mall.common.core.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/8
 */
@Data
@Schema
public class BasePageQuery {

    @Schema(description  = "页码", example = "1")
    private int pageNum = 1;

    @Schema(description = "每页记录数", example = "10")
    private int pageSize = 10;
}
