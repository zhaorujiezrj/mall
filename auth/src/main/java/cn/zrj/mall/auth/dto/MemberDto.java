package cn.zrj.mall.auth.dto;

import lombok.Data;

import java.time.LocalDate;


/**
 * 会员传输层对象
 *
 * @author zhaorujie
 */
@Data
public class MemberDto {

    private Long id;

    private Integer gender;

    private String nickName;

    private String mobile;

    private LocalDate birthday;

    private String avatarUrl;

    private String openid;

    private String sessionKey;

    private String city;

    private String country;

    private String language;

    private String province;

}
