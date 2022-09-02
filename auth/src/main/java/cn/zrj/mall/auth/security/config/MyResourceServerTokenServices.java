package cn.zrj.mall.auth.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import java.util.Date;

@Slf4j
public class MyResourceServerTokenServices implements ResourceServerTokenServices, InitializingBean {

    private TokenStore tokenStore;

    /**
     * 根据访问令牌值加载用户认证信息
     */
    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException,
            InvalidTokenException {
        long start = System.currentTimeMillis();
        // 1. 根据令牌值，获取当前令牌对象
        DefaultOAuth2AccessToken oAuth2AccessToken = (DefaultOAuth2AccessToken) tokenStore.readAccessToken(accessToken);
        if (oAuth2AccessToken == null) {
            // 1.1 令牌对象不存在 抛出Invalid access token 异常
            throw new InvalidTokenException("Invalid access token: " + accessToken);
        } else if (oAuth2AccessToken.isExpired()) {
            // 1.2 存在令牌对象，但是已过期则删除，抛出 Access token expired 令牌过期异常
            tokenStore.removeAccessToken(oAuth2AccessToken);
            throw new InvalidTokenException("Access token expired: " + accessToken);
        }
        // 2. 根据令牌获取认证信息
        OAuth2Authentication result = tokenStore.readAuthentication(oAuth2AccessToken);
        if (result == null) {
            // in case of race condition
            // 2.1 没有认证信息 也会抛出异常Invalid access token
            throw new InvalidTokenException("Invalid access token: " + accessToken);
        }
        // 3. 访问令牌续签
        Date nowTokenExpiration = oAuth2AccessToken.getExpiration();
        Date now = new Date();
        // 3.1 如果过期时间少于5分钟，则重新设置令牌对象的过期时间，并存储
        if ((nowTokenExpiration.getTime() - now.getTime()) < 1000L * 300) {
            // 设置1小时过期，时间应该从配置中获取
            oAuth2AccessToken.setExpiration(new Date(System.currentTimeMillis() + (3600 * 1000L)));
            tokenStore.storeAccessToken(oAuth2AccessToken, result); // 设置新的令牌 会自动再根据令牌对象过期时间设置 redis过期
        }
        long end = System.currentTimeMillis();
        log.info("token续期耗时：{}毫秒", (end - start));
        return result;
    }

    /**
     * 读取令牌对象
     */
    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return tokenStore.readAccessToken(accessToken);
    }

    /**
     * 后置处理 检查TokenStore
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenStore, "tokenStore must be set");
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}

