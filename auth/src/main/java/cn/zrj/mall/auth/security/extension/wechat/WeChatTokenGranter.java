package cn.zrj.mall.auth.security.extension.wechat;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
public class WeChatTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "wx_app";

    private final AuthenticationManager authenticationManager;

    public WeChatTokenGranter(AuthorizationServerTokenServices tokenServices,
                              ClientDetailsService clientDetailsService,
                              OAuth2RequestFactory requestFactory,
                              AuthenticationManager authenticationManager) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String code = parameters.get("code");
        String encryptedData = parameters.get("encryptedData");
        String iv = parameters.get("iv");

        parameters.remove(encryptedData);
        parameters.remove(iv);

        // 未认证状态
        AbstractAuthenticationToken authentication = new WeChatAuthenticationToken(code, encryptedData, iv);
        authentication.setDetails(parameters);

        // 认证中
        Authentication authenticate = this.authenticationManager.authenticate(authentication);

        // 认证成功
        if (Objects.nonNull(authenticate) && authenticate.isAuthenticated()) {
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(client);
            return new OAuth2Authentication(oAuth2Request, authenticate);
        }
        // 认证失败
        throw new InvalidGrantException("Could not authenticate code: " + code);
    }
}
