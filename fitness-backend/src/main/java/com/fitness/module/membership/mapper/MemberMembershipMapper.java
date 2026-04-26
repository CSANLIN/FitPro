package com.fitness.module.membership.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.vo.MembershipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMembershipMapper extends BaseMapper<MemberMembershipEntity> {

    List<MembershipVO> selectMembershipVOByUserId(@Param("userId") Long userId);

    List<MembershipVO> selectAllMembershipVO();
}
