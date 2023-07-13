package cn.zrj.mall.auth.security.authentication.captcha;

import cn.zrj.mall.auth.security.OAuth2ParamsNames;
import cn.zrj.mall.auth.security.utils.OAuth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: CaptchaAuthenticationConverter
 * @author: zhaorujie
 * @date: 2023/7/11
 * @version: v1.0
 **/
public class CaptchaAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 授权类型 (必需)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!CaptchaAuthenticationToken.CAPTCHA.getValue().equals(grantType)) {
            return null;
        }

        // 客户端信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 参数提取验证
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        // 令牌申请访问范围验证 (可选)
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParameterNames.SCOPE);
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(
                    Arrays.asList(StringUtils.delimitedListToStringArray(scope, ",")));
        }

        // 用户名验证(必需)
        OAuth2EndpointUtils.checkRequiredParameter(parameters,OAuth2ParameterNames.USERNAME);

        // 密码验证(必需)
        OAuth2EndpointUtils.checkRequiredParameter(parameters,OAuth2ParameterNames.PASSWORD);

        // 验证码(必需)
        OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParamsNames.VERIFY_CODE);

        // 验证码缓存Key(必需)
        OAuth2EndpointUtils.checkRequiredParameter(parameters, OAuth2ParamsNames.VERIFY_CODE_KEY);

        // 附加参数(保存用户名/密码传递给 CaptchaAuthenticationProvider 用于身份认证)
        Map<String, Object> additionalParameters = parameters
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) &&
                        !e.getKey().equals(OAuth2ParameterNames.SCOPE)
                ).collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
        return new CaptchaAuthenticationToken(authentication, requestedScopes, additionalParameters);
    }
}
