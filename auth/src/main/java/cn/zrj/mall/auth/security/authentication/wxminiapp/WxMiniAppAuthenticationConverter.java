package cn.zrj.mall.auth.security.authentication.wxminiapp;


import cn.zrj.mall.auth.security.OAuth2GrantType;
import cn.zrj.mall.auth.security.OAuth2ParamsNames;
import cn.zrj.mall.auth.security.utils.OAuth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zhaorujie
 * @date 2022/9/27
 */
public class WxMiniAppAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        // 授权类型 (必需)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!Objects.equals(OAuth2GrantType.WX_APP_GRANT_TYPE, grantType)) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        String code = parameters.getFirst(OAuth2ParamsNames.CODE);
        OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParamsNames.CODE);

        String mobileAuthCode = parameters.getFirst(OAuth2ParamsNames.MOBILE_AUTH_CODE);
        //OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParamsNames.MOBILE_AUTH_CODE);

        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParameterNames.SCOPE);
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(
                    Arrays.asList(StringUtils.delimitedListToStringArray(scope, ",")));
        }

        Map<String, Object> additionalParameters = new HashMap<>(16);
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
                    !key.equals(OAuth2ParameterNames.SCOPE) &&
                    !key.equals(OAuth2ParamsNames.CODE) &&
                    !key.equals(OAuth2ParamsNames.MOBILE_AUTH_CODE)) {
                additionalParameters.put(key, value.get(0));
            }
        });
        return new WxMiniAppAuthenticationToken(authentication, requestedScopes, additionalParameters, code, mobileAuthCode);
    }
}
