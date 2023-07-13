//package cn.zrj.mall.auth.security.point;
//
//import cn.zrj.mall.common.core.constant.RedisConstants;
//import cn.zrj.mall.common.core.constant.SecurityConstants;
//import jakarta.annotation.Resource;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.ObjectUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.context.DeferredSecurityContext;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolderStrategy;
//import org.springframework.security.web.context.HttpRequestResponseHolder;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.function.Supplier;
//
///**
// * @description: 基于redis存储认证信息
// * @author: zhaorujie
// * @date: 2023/7/12
// * @version: v1.0
// **/
//@Slf4j
//@Component
//public class RedisSecurityContextRepository implements SecurityContextRepository {
//
//    @Resource
//    private RedisTemplate<String, SecurityContext> redisTemplate;
//
//    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
//            .getContextHolderStrategy();
//
//    @Override
//    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
//        throw new UnsupportedOperationException("Method deprecated.");
//    }
//
//    @Override
//    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
//        Supplier<SecurityContext> supplier = () -> readSecurityContextFromRedis(request);
//        return new SupplierDeferredSecurityContext(supplier, this.securityContextHolderStrategy);
//    }
//
//    @Override
//    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
//        String nonce = getNonce(request);
//        if (ObjectUtils.isEmpty(nonce)) {
//            return;
//        }
//
//        // 如果当前的context是空的，则移除
//        SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
//        if (emptyContext.equals(context)) {
//            redisTemplate.delete((RedisConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce));
//        } else {
//            // 保存认证信息
//            redisTemplate.opsForValue().set(RedisConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce, context, RedisConstants.DEFAULT_TIMEOUT_SECONDS);
//        }
//    }
//
//    @Override
//    public boolean containsContext(HttpServletRequest request) {
//        String nonce = getNonce(request);
//        if (ObjectUtils.isEmpty(nonce)) {
//            return false;
//        }
//        // 检验当前请求是否有认证信息
//        return redisTemplate.opsForValue().get(RedisConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce) != null;
//    }
//
//    /**
//     * 从redis中获取认证信息
//     *
//     * @param request 当前请求
//     * @return 认证信息
//     */
//    private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        String nonce = getNonce(request);
//        if (ObjectUtils.isEmpty(nonce)) {
//            return null;
//        }
//
//        // 根据缓存id获取认证信息
//        return redisTemplate.opsForValue().get(RedisConstants.SECURITY_CONTEXT_PREFIX_KEY + nonce);
//    }
//
//    /**
//     * 先从请求头中找，找不到去请求参数中找，找不到获取当前session的id
//     * 获取当前session的sessionId
//     *
//     * @param request 当前请求
//     * @return 随机字符串(sessionId)，这个字符串本来是前端生成，现在改为后端获取的sessionId
//     */
//    private String getNonce(HttpServletRequest request) {
//        String nonce = request.getHeader(SecurityConstants.NONCE_HEADER_NAME);
//        if (ObjectUtils.isEmpty(nonce)) {
//            nonce = request.getParameter(SecurityConstants.NONCE_HEADER_NAME);
//            HttpSession session = request.getSession(Boolean.FALSE);
//            if (ObjectUtils.isEmpty(nonce) && session != null) {
//                nonce = session.getId();
//            }
//        }
//        return nonce;
//    }
//}
