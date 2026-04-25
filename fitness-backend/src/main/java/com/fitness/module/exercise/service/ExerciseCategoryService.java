package com.fitness.module.exercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.exercise.dto.ExerciseCategoryCreateDTO;
import com.fitness.module.exercise.dto.ExerciseCategoryUpdateDTO;
import com.fitness.module.exercise.entity.ExerciseCategoryEntity;
import com.fitness.module.exercise.vo.ExerciseCategoryVO;

import java.util.List;

public interface ExerciseCategoryService extends IService<ExerciseCategoryEntity> {

    /**
     * 查询全部分类（按排序字段升序）
     */
    List<ExerciseCategoryVO> listAll();

    /**
     * 创建分类
     */
    ExerciseCategoryVO create(ExerciseCategoryCreateDTO dto);

    /**
     * 更新分类
     */
    ExerciseCategoryVO update(ExerciseCategoryUpdateDTO dto);

    /**
     * 删除分类
     */
    void delete(Long id);
}
