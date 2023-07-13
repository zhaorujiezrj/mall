package cn.zrj.mall.common.core.aspect;

import cn.zrj.mall.common.core.annotation.DuplicateSubmit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaorujie
 * @version v1.0
 * @description 防重复提交AOP切面
 * @date 2023/7/13
 **/
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class DuplicateSubmitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(cn.zrj.mall.common.core.annotation.DuplicateSubmit)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 上面拿到的可能是接口的方法，所以需要以下的操作来确保获取到的是实现类的方法
        if (method.getDeclaringClass().isInterface()) {
            method = joinPoint.getTarget().getClass()
                    .getDeclaredMethod(joinPoint.getSignature().getName(), method.getParameterTypes());
        }

        DuplicateSubmit annotation = method.getAnnotation(DuplicateSubmit.class);
        String duplicateKey = null;
        if (annotation != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(joinPoint.getTarget().getClass().getName())
                    .append("#")
                    .append(joinPoint.getSignature().getName())
                    .append("#")
                    .append(Arrays.toString(joinPoint.getArgs()));
            duplicateKey = "dup:" + DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));

            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(duplicateKey, "DUP", annotation.interval(), TimeUnit.SECONDS);
            if(lock == null || !lock){
                // 未获取锁，重复请求
                log.info("检测到重复请求：{}, duplicateKey:{}", sb, duplicateKey);
                throw new RuntimeException("Please do not resubmit");
            }
        }
        Object object;
        try {
            object = joinPoint.proceed();
        } finally {
            if(duplicateKey != null){
                Boolean delete = stringRedisTemplate.delete(duplicateKey);
                System.out.println(delete);
            }
        }

        return object;
    }

}
