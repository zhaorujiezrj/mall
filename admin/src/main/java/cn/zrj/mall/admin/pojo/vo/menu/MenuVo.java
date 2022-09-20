package cn.zrj.mall.admin.pojo.vo.menu;

import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/9/9
 */
@Data
public class MenuVo {

    private Long id;

    private Long parentId;

    private String name;

    private String path;

    private String component;

    private String icon;

    private Integer sort;

    private Integer status;

    private String redirect;
}
