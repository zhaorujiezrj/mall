package cn.zrj.mall.admin.pojo.vo.user;

import lombok.Data;

import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Data
public class UserVo {

    private Long id;

    private String username;

    private String nickname;

    private String mobile;

    private Integer status;

    private String roleId;

    private List<String> roleIds;

    private String roleNames;

}
