package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("workout_template_item")
public class WorkoutTemplateItemEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long templateId;

    private Long exerciseId;

    private Integer sets;

    private Integer reps;

    private Integer restSeconds;

    private Integer sortOrder;
}
