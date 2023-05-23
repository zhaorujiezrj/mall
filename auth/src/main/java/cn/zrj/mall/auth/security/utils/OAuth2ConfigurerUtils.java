package cn.zrj.mall.auth.security.utils;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <p>Description: OAuth 2.0 Configurers 工具方法类</p>
 * <p>
 * 新版 spring-security-oauth2-authorization-server 很多代码都是“包”级可访问的，外部无法使用。为了方便扩展将其提取出来，便于使用。
 * <p>
 * 代码内容与原包代码基本一致。
 *
 * @author zhaorujie
 */
public class OAuth2ConfigurerUtils {

	private OAuth2ConfigurerUtils() {
	}

	public static <B extends HttpSecurityBuilder<B>> RegisteredClientRepository getRegisteredClientRepository(B builder) {
		RegisteredClientRepository registeredClientRepository = builder.getSharedObject(RegisteredClientRepository.class);
		if (registeredClientRepository == null) {
			registeredClientRepository = getBean(builder, RegisteredClientRepository.class);
			builder.setSharedObject(RegisteredClientRepository.class, registeredClientRepository);
		}
		return registeredClientRepository;
	}

	public static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizationService getAuthorizationService(B builder) {
		OAuth2AuthorizationService authorizationService = builder.getSharedObject(OAuth2AuthorizationService.class);
		if (authorizationService == null) {
			authorizationService = getOptionalBean(builder, OAuth2AuthorizationService.class);
			if (authorizationService == null) {
				authorizationService = new InMemoryOAuth2AuthorizationService();
			}
			builder.setSharedObject(OAuth2AuthorizationService.class, authorizationService);
		}
		return authorizationService;
	}

	public static <B extends HttpSecurityBuilder<B>> OAuth2AuthorizationConsentService getAuthorizationConsentService(B builder) {
		OAuth2AuthorizationConsentService authorizationConsentService = builder.getSharedObject(OAuth2AuthorizationConsentService.class);
		if (authorizationConsentService == null) {
			authorizationConsentService = getOptionalBean(builder, OAuth2AuthorizationConsentService.class);
			if (authorizationConsentService == null) {
				authorizationConsentService = new InMemoryOAuth2AuthorizationConsentService();
			}
			builder.setSharedObject(OAuth2AuthorizationConsentService.class, authorizationConsentService);
		}
		return authorizationConsentService;
	}

	@SuppressWarnings("unchecked")
	public static <B extends HttpSecurityBuilder<B>> OAuth2TokenGenerator<? extends OAuth2Token> getTokenGenerator(B builder) {
		OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = builder.getSharedObject(OAuth2TokenGenerator.class);
		if (tokenGenerator == null) {
			tokenGenerator = getOptionalBean(builder, OAuth2TokenGenerator.class);
			if (tokenGenerator == null) {
				JwtGenerator jwtGenerator = getJwtGenerator(builder);
				OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
				OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer = getAccessTokenCustomizer(builder);
				if (accessTokenCustomizer != null) {
					accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
				}
				OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
				if (jwtGenerator != null) {
					tokenGenerator = new DelegatingOAuth2TokenGenerator(
							jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
				} else {
					tokenGenerator = new DelegatingOAuth2TokenGenerator(
							accessTokenGenerator, refreshTokenGenerator);
				}
			}
			builder.setSharedObject(OAuth2TokenGenerator.class, tokenGenerator);
		}
		return tokenGenerator;
	}

	private static <B extends HttpSecurityBuilder<B>> JwtGenerator getJwtGenerator(B builder) {
		JwtGenerator jwtGenerator = builder.getSharedObject(JwtGenerator.class);
		if (jwtGenerator == null) {
			JwtEncoder jwtEncoder = getJwtEncoder(builder);
			if (jwtEncoder != null) {
				jwtGenerator = new JwtGenerator(jwtEncoder);
				OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = getJwtCustomizer(builder);
				if (jwtCustomizer != null) {
					jwtGenerator.setJwtCustomizer(jwtCustomizer);
				}
				builder.setSharedObject(JwtGenerator.class, jwtGenerator);
			}
		}
		return jwtGenerator;
	}

	private static <B extends HttpSecurityBuilder<B>> JwtEncoder getJwtEncoder(B builder) {
		JwtEncoder jwtEncoder = builder.getSharedObject(JwtEncoder.class);
		if (jwtEncoder == null) {
			jwtEncoder = getOptionalBean(builder, JwtEncoder.class);
			if (jwtEncoder == null) {
				JWKSource<SecurityContext> jwkSource = getJwkSource(builder);
				if (jwkSource != null) {
					jwtEncoder = new NimbusJwtEncoder(jwkSource);
				}
			}
			if (jwtEncoder != null) {
				builder.setSharedObject(JwtEncoder.class, jwtEncoder);
			}
		}
		return jwtEncoder;
	}

	@SuppressWarnings("unchecked")
	public static <B extends HttpSecurityBuilder<B>> JWKSource<SecurityContext> getJwkSource(B builder) {
		JWKSource<SecurityContext> jwkSource = builder.getSharedObject(JWKSource.class);
		if (jwkSource == null) {
			ResolvableType type = ResolvableType.forClassWithGenerics(JWKSource.class, SecurityContext.class);
			jwkSource = getOptionalBean(builder, type);
			if (jwkSource != null) {
				builder.setSharedObject(JWKSource.class, jwkSource);
			}
		}
		return jwkSource;
	}

	private static <B extends HttpSecurityBuilder<B>> OAuth2TokenCustomizer<JwtEncodingContext> getJwtCustomizer(B builder) {
		ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class, JwtEncodingContext.class);
		return getOptionalBean(builder, type);
	}

	private static <B extends HttpSecurityBuilder<B>> OAuth2TokenCustomizer<OAuth2TokenClaimsContext> getAccessTokenCustomizer(B builder) {
		ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2TokenCustomizer.class, OAuth2TokenClaimsContext.class);
		return getOptionalBean(builder, type);
	}


	public static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, Class<T> type) {
		return builder.getSharedObject(ApplicationContext.class).getBean(type);
	}

	@SuppressWarnings("unchecked")
	public static <B extends HttpSecurityBuilder<B>, T> T getBean(B builder, ResolvableType type) {
		ApplicationContext context = builder.getSharedObject(ApplicationContext.class);
		String[] names = context.getBeanNamesForType(type);
		if (names.length == 1) {
			return (T) context.getBean(names[0]);
		}
		if (names.length > 1) {
			throw new NoUniqueBeanDefinitionException(type, names);
		}
		throw new NoSuchBeanDefinitionException(type);
	}

	public static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
		Map<String, T> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				builder.getSharedObject(ApplicationContext.class), type);
		if (beansMap.size() > 1) {
			throw new NoUniqueBeanDefinitionException(type, beansMap.size(),
					"Expected single matching bean of type '" + type.getName() + "' but found " +
							beansMap.size() + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
		}
		return (!beansMap.isEmpty() ? beansMap.values().iterator().next() : null);
	}

	@SuppressWarnings("unchecked")
	public static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, ResolvableType type) {
		ApplicationContext context = builder.getSharedObject(ApplicationContext.class);
		String[] names = context.getBeanNamesForType(type);
		if (names.length > 1) {
			throw new NoUniqueBeanDefinitionException(type, names);
		}
		return names.length == 1 ? (T) context.getBean(names[0]) : null;
	}

}
