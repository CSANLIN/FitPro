package com.fitness.module.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.checkin.entity.CheckInEntity;
import com.fitness.module.checkin.vo.CheckInVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckInEntity> {

    List<CheckInVO> selectCheckInVOByUserId(@Param("userId") Long userId);

    List<CheckInVO> selectAllCheckInVO();

    List<String> selectCheckInDatesByMonth(@Param("userId") Long userId,
                                           @Param("yearMonth") String yearMonth);
}
