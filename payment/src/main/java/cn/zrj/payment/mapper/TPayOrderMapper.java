package cn.zrj.payment.mapper;

import cn.zrj.payment.entity.TPayOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 支付订单表 Mapper 接口
 * </p>
 *
 * @author zhaorujie
 * @since 2023-04-26
 */
@Mapper
public interface TPayOrderMapper extends BaseMapper<TPayOrder> {

}
