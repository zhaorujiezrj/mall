package cn.zrj.mall.member.mapper;

import cn.zrj.mall.member.entity.UmsMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UmsMemberMapper extends BaseMapper<UmsMember> {
}
