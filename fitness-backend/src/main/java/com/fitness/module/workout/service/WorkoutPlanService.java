package com.fitness.module.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.common.PageResult;
import com.fitness.module.workout.dto.WorkoutPlanCreateDTO;
import com.fitness.module.workout.dto.WorkoutPlanQueryDTO;
import com.fitness.module.workout.entity.WorkoutPlanEntity;
import com.fitness.module.workout.vo.WorkoutPlanDetailVO;
import com.fitness.module.workout.vo.WorkoutPlanVO;

public interface WorkoutPlanService extends IService<WorkoutPlanEntity> {

    PageResult<WorkoutPlanVO> pageList(WorkoutPlanQueryDTO query, Long userId);

    WorkoutPlanDetailVO getDetail(Long id);

    WorkoutPlanDetailVO create(WorkoutPlanCreateDTO dto, Long userId, Long coachId);

    void updateStatus(Long id, String status);

    void delete(Long id);
}
