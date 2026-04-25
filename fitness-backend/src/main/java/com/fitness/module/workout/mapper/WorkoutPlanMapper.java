package com.fitness.module.workout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.module.workout.entity.WorkoutPlanEntity;
import com.fitness.module.workout.vo.WorkoutPlanDetailVO;
import com.fitness.module.workout.vo.WorkoutPlanVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkoutPlanMapper extends BaseMapper<WorkoutPlanEntity> {

    IPage<WorkoutPlanVO> selectPlanVOPage(IPage<WorkoutPlanVO> page, @Param("keyword") String keyword,
                                          @Param("status") String status, @Param("userId") Long userId);

    WorkoutPlanDetailVO selectPlanDetail(@Param("planId") Long planId);

    List<WorkoutPlanDetailVO.PlanDayVO> selectPlanDays(@Param("planId") Long planId);

    List<WorkoutPlanDetailVO.PlanDayVO.DayItemVO> selectPlanDayItems(@Param("planDayId") Long planDayId);
}
