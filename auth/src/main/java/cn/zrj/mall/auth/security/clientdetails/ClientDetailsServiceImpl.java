package cn.zrj.mall.auth.security.clientdetails;

import cn.zrj.mall.auth.dto.OAuth2ClientDTO;
import cn.zrj.mall.auth.enums.PasswordEncoderTypeEnum;
import cn.zrj.mall.auth.feign.Oauth2FeignClient;
import cn.zrj.mall.common.core.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

/**
 * @author zhaorujie
 * @date 2022/9/22
 */
@Component
@RequiredArgsConstructor
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private final Oauth2FeignClient oauth2FeignClient;

    @Override
    @Cacheable(cacheNames = "auth", key = "'oauth-client:'+#clientId")
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Result<OAuth2ClientDTO> result = oauth2FeignClient.getDetailsByClientId(clientId);
        if (Result.isSuccess(result)) {
            OAuth2ClientDTO client = result.getData();
            BaseClientDetails clientDetails = new BaseClientDetails(
                    client.getClientId(),
                    client.getResourceIds(),
                    client.getScope(),
                    client.getAuthorizedGrantTypes(),
                    client.getAuthorities(),
                    client.getWebServerRedirectUri()
            );
            clientDetails.setClientSecret(PasswordEncoderTypeEnum.NOOP.getPrefix() + client.getClientSecret());
            clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
            clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
            return clientDetails;
        } else {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }
}
