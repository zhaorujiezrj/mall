package cn.zrj.mall.admin.service;

import cn.zrj.mall.admin.entity.OauthClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-22
 */
public interface OauthClientDetailsService extends IService<OauthClientDetails> {
    /**
     * 客户端详情
     * @param clientId
     * @return
     */
    OauthClientDetails getDetailsByClientId(String clientId);
}
