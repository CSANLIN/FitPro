package com.fitness.module.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.common.PageResult;
import com.fitness.module.exercise.dto.ExerciseCreateDTO;
import com.fitness.module.exercise.dto.ExerciseQueryDTO;
import com.fitness.module.exercise.dto.ExerciseUpdateDTO;
import com.fitness.module.exercise.entity.ExerciseEntity;
import com.fitness.module.exercise.vo.ExerciseVO;

public interface ExerciseService extends IService<ExerciseEntity> {

    /**
     * 分页查询动作（带分类名称，支持多条件筛选）
     */
    PageResult<ExerciseVO> pageList(ExerciseQueryDTO query);

    /**
     * 获取动作详情
     */
    ExerciseVO getDetail(Long id);

    /**
     * 创建动作
     */
    ExerciseVO create(ExerciseCreateDTO dto);

    /**
     * 更新动作
     */
    ExerciseVO update(ExerciseUpdateDTO dto);

    /**
     * 删除动作
     */
    void delete(Long id);
}
