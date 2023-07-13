package cn.zrj.mall.auth.security.handler;

import cn.zrj.mall.common.core.result.Result;
import cn.zrj.mall.common.core.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @description: MyAuthenticationFailureHandler
 * @author: zhaorujie
 * @date: 2023/7/11
 * @version: v1.0
 **/
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final HttpMessageConverter<Object> accessTokenHttpResponseConverter = new MappingJackson2HttpMessageConverter();


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn(" authentication failure: ", exception);

        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.OK);
        String errorCode = error.getErrorCode();
        String msg = error.getDescription();
        if (errorCode.startsWith("invalid_")) {
            msg = errorCode;
            errorCode = ResultCode.PARAM_ERROR.getCode();
        }
        if (StringUtils.isBlank(errorCode)) {

            errorCode = ResultCode.SYSTEM_EXECUTION_ERROR.getCode();
        }
        if (StringUtils.isBlank(msg)) {
            msg = ResultCode.SYSTEM_EXECUTION_ERROR.getMsg();
        }
        Result<Object> result = Result.error(errorCode, msg);
        accessTokenHttpResponseConverter.write(result, null, httpResponse);
    }
}
