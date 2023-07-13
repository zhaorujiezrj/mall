package cn.zrj.mall.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zrj.mall.member.dto.MemberAuthDto;
import cn.zrj.mall.member.dto.MemberDto;
import cn.zrj.mall.member.entity.UmsMember;
import cn.zrj.mall.member.mapper.UmsMemberMapper;
import cn.zrj.mall.member.service.UmsMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zhaorujie
 */
@Service
public class UmsMemberServiceImpl extends ServiceImpl<UmsMemberMapper, UmsMember> implements UmsMemberService {
    @Override
    public MemberAuthDto getMemberByOpenid(String openid) {
        LambdaQueryWrapper<UmsMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmsMember::getOpenid, openid);
        UmsMember member = this.getOne(wrapper);
        MemberAuthDto memberAuth = null;
        if (member != null) {
            memberAuth = new MemberAuthDto()
                    .setMemberId(member.getId())
                    .setUsername(member.getOpenid())
                    .setStatus(member.getStatus())
                    .setMobile(member.getMobile());
        }
        return memberAuth;
    }

    @Override
    public MemberAuthDto getByMobile(String mobile) {
        UmsMember member = this.getOne(new LambdaQueryWrapper<UmsMember>().eq(UmsMember::getMobile, mobile));
        MemberAuthDto memberAuth = null;
        if (member != null) {
            memberAuth = new MemberAuthDto()
                    .setMemberId(member.getId())
                    .setUsername(member.getMobile())
                    .setStatus(member.getStatus())
                    .setMobile(member.getMobile());
        }
        return memberAuth;
    }

    @Override
    public Long addMember(MemberDto memberDto) {
        UmsMember umsMember = BeanUtil.copyProperties(memberDto, UmsMember.class);
        umsMember.setStatus(1);
        this.save(umsMember);

        return umsMember.getId();
    }

    @Override
    public void updateMember(MemberDto memberDto) {
        UmsMember umsMember = BeanUtil.copyProperties(memberDto, UmsMember.class);
        this.updateById(umsMember);
    }
}
