package cn.zrj.mall.member.service;

import cn.zrj.mall.member.dto.MemberAuthDto;
import cn.zrj.mall.member.dto.MemberDto;
import cn.zrj.mall.member.entity.UmsMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhaorujie
 */
public interface UmsMemberService extends IService<UmsMember> {

    /**
     * 根据 openid 获取会员认证信息
     *
     * @param openid
     * @return
     */
    MemberAuthDto getMemberByOpenid(String openid);

    /**
     * 根据手机号获取会员认证信息
     *
     * @param mobile
     * @return
     */
    MemberAuthDto getByMobile(String mobile);

    /**
     * 添加用户
     * @param memberDTO
     */
    void addMember(MemberDto memberDTO);

    /**
     * 修改用户
     * @param memberDto
     */
    void updateMember(MemberDto memberDto);
}
