package com.fitness.module.workout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.workout.dto.WorkoutTemplateCreateDTO;
import com.fitness.module.workout.dto.WorkoutTemplateUpdateDTO;
import com.fitness.module.workout.entity.WorkoutTemplateEntity;
import com.fitness.module.workout.vo.WorkoutTemplateDetailVO;
import com.fitness.module.workout.vo.WorkoutTemplateVO;

import java.util.List;

public interface WorkoutTemplateService extends IService<WorkoutTemplateEntity> {

    List<WorkoutTemplateVO> listAll();

    WorkoutTemplateDetailVO getDetail(Long id);

    WorkoutTemplateDetailVO create(WorkoutTemplateCreateDTO dto, Long coachId);

    WorkoutTemplateDetailVO update(WorkoutTemplateUpdateDTO dto);

    void delete(Long id);
}
