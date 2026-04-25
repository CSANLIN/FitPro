package com.fitness.module.exercise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.module.exercise.entity.ExerciseEntity;
import com.fitness.module.exercise.vo.ExerciseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExerciseMapper extends BaseMapper<ExerciseEntity> {

    /**
     * 分页查询运动动作（带分类名称）
     */
    IPage<ExerciseVO> selectExerciseVOPage(IPage<ExerciseVO> page, @Param("categoryId") Long categoryId,
                                            @Param("muscleGroup") String muscleGroup,
                                            @Param("equipment") String equipment,
                                            @Param("difficulty") String difficulty,
                                            @Param("keyword") String keyword);
}
