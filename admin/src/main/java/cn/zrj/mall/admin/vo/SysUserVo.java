package cn.zrj.mall.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaorujie
 * @date 2022/8/19
 */
@Data
public class SysUserVo {

    private Long id;

    private String username;

    private String nickname;

    private String mobile;

    private Integer gender;

    private String avatar;

    private String password;

    private String email;

    private Integer status;

    private Long deptId;

    private Integer deleted;

    private String deptName;

    private List<Long> roleIds;

    private String roleNames;

    private List<String> roles;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
