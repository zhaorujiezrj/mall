package cn.zrj.mall.admin.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.zrj.mall.admin.entity.OauthClientDetails;
import cn.zrj.mall.admin.pojo.dto.OAuth2ClientDTO;
import cn.zrj.mall.admin.service.OauthClientDetailsService;
import cn.zrj.mall.common.core.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-22
 */
@Api(tags = "oauth2客户端管理")
@RestController
@RequestMapping("/api/v1/oauth-client")
@RequiredArgsConstructor
public class OauthClientDetailsController {

    private final OauthClientDetailsService oauthClientDetailsService;


    @ApiOperation(value = "获取 OAuth2 客户端认证信息", notes = "Feign 调用", hidden = true)
    @GetMapping("/getOAuth2ClientById")
    public Result<OAuth2ClientDTO> getDetailsByClientId(String clientId) {
        OauthClientDetails details = oauthClientDetailsService.getDetailsByClientId(clientId);
        return Result.success(BeanUtil.copyProperties(details, OAuth2ClientDTO.class));
    }

}
