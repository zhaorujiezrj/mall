package cn.zrj.mall.auth.dto;

import lombok.Data;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
@Data
public class Oauth2AuthDto {

    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String grantType;
    private String username;
    private String password;
    private String mobile;
    private String code;
    private String encryptedData;
    private String iv;
}
