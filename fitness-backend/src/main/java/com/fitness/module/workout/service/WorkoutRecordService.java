package com.fitness.module.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.common.PageResult;
import com.fitness.module.workout.dto.WorkoutRecordCreateDTO;
import com.fitness.module.workout.dto.WorkoutRecordQueryDTO;
import com.fitness.module.workout.entity.WorkoutRecordEntity;
import com.fitness.module.workout.vo.WorkoutRecordDetailVO;
import com.fitness.module.workout.vo.WorkoutRecordVO;

import java.util.Map;

public interface WorkoutRecordService extends IService<WorkoutRecordEntity> {

    PageResult<WorkoutRecordVO> pageList(WorkoutRecordQueryDTO query, Long userId);

    WorkoutRecordDetailVO getDetail(Long id);

    WorkoutRecordDetailVO create(WorkoutRecordCreateDTO dto, Long userId);

    void delete(Long id);

    Map<String, Object> weeklyStats(Long userId);
}
