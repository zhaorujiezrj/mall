package cn.zrj.mall.admin.service.impl;

import cn.zrj.mall.admin.entity.OauthClientDetails;
import cn.zrj.mall.admin.mapper.OauthClientDetailsMapper;
import cn.zrj.mall.admin.service.OauthClientDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhaorujie
 * @since 2022-09-22
 */
@Service
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsMapper, OauthClientDetails> implements OauthClientDetailsService {

    @Override
    public OauthClientDetails getDetailsByClientId(String clientId) {
        return getOne(new LambdaQueryWrapper<OauthClientDetails>().eq(OauthClientDetails::getClientId, clientId));
    }
}
