//package cn.zrj.mall.auth.security.point;
//
//import cn.zrj.mall.common.core.constant.SecurityConstants;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.DefaultRedirectStrategy;
//import org.springframework.security.web.RedirectStrategy;
//import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
//import org.springframework.security.web.util.UrlUtils;
//import org.springframework.util.ObjectUtils;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
///**
// * @description: 登录重定向处理
// * @author: zhaorujie
// * @date: 2023/7/12
// * @version: v1.0
// **/
//@Slf4j
//public class LoginTargetAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
//
//    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//
//    /**
//     * @param loginFormUrl URL where the login page can be found. Should either be
//     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
//     *                     URL.
//     */
//    public LoginTargetAuthenticationEntryPoint(String loginFormUrl) {
//        super(loginFormUrl);
//    }
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        // 获取登录表单的地址
//        String loginForm = determineUrlToUseForThisRequest(request, response, authException);
//        if (!UrlUtils.isAbsoluteUrl(loginForm)) {
//            // 不是绝对路径调用父类方法处理
//            super.commence(request, response, authException);
//            return;
//        }
//
//        StringBuffer requestUrl = request.getRequestURL();
//        if (!ObjectUtils.isEmpty(request.getQueryString())) {
//            requestUrl.append("?").append(request.getQueryString());
//        }
//
//        // 2023-07-11添加逻辑：重定向地址添加nonce参数，该参数的值为sessionId
//        // 绝对路径在重定向前添加target参数
//        String targetParameter = URLEncoder.encode(requestUrl.toString(), StandardCharsets.UTF_8);
//        String targetUrl = loginForm + "?target=" + targetParameter + "&" + SecurityConstants.NONCE_HEADER_NAME + "=" + request.getSession(Boolean.FALSE).getId();
//        log.debug("重定向至前后端分离的登录页面：{}", targetUrl);
//        this.redirectStrategy.sendRedirect(request, response, targetUrl);
//    }
//}
