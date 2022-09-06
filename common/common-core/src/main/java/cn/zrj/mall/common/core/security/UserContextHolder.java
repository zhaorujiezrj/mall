package cn.zrj.mall.common.core.security;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zrj.mall.common.core.constant.SecurityConstants;
import cn.zrj.mall.common.core.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JWSObject;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author zhaorujie
 * @date 2022/8/23
 */
@Component
public class UserContextHolder {


    private static StringRedisTemplate redisTemplate;

    public UserContextHolder(StringRedisTemplate redisTemplate) {
        UserContextHolder.redisTemplate = redisTemplate;
    }

    public static JSONObject getUserInfo() {
        String payload = getPayload(getToken());
        return JSONUtil.parseObj(payload);
    }

    public static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            throw new BusinessException("请登录!");
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader(SecurityConstants.AUTHENTICATION);
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("请登录!");
        }
        token = token.replaceAll("^[Bb]earer\\s+", "");
        return token;
    }

    @SneakyThrows
    public static String getPayload(String token) {
        return JWSObject.parse(token).getPayload().toString();
    }

    public static PayloadToken getPayloadToken(String token) {
        return JSONUtil.toBean(getPayload(token), PayloadToken.class);
    }

    public static PayloadToken getPayloadToken() {
        return JSONUtil.toBean(getPayload(getToken()), PayloadToken.class);
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayloadToken {
        private String jti;

        private String username;

        private List<String> scope;

        private Long exp;

        private Long userId;

        private Long memberId;

        private List<String> authorities;

        @JsonProperty("client_id")
        private String clientId;

        private String mobile;
    }
}
