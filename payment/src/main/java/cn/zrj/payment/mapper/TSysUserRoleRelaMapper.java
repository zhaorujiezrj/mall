package cn.zrj.payment.mapper;

import cn.zrj.payment.entity.TSysUserRoleRela;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 操作员<->角色 关联表 Mapper 接口
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Mapper
public interface TSysUserRoleRelaMapper extends BaseMapper<TSysUserRoleRela> {

}
