package com.fitness.module.workout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.module.workout.entity.WorkoutRecordEntity;
import com.fitness.module.workout.vo.WorkoutRecordDetailVO;
import com.fitness.module.workout.vo.WorkoutRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkoutRecordMapper extends BaseMapper<WorkoutRecordEntity> {

    IPage<WorkoutRecordVO> selectRecordVOPage(IPage<WorkoutRecordVO> page, @Param("userId") Long userId,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate);

    WorkoutRecordDetailVO selectRecordDetail(@Param("recordId") Long recordId);

    List<WorkoutRecordDetailVO.RecordItemVO> selectRecordItems(@Param("recordId") Long recordId);

    /**
     * 查询用户本周训练次数
     */
    Long selectWeeklyCount(@Param("userId") Long userId, @Param("weekStart") String weekStart);

    /**
     * 查询用户本周总训练量
     */
    Long selectWeeklyVolume(@Param("userId") Long userId, @Param("weekStart") String weekStart);
}
