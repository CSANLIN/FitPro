package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("workout_template")
public class WorkoutTemplateEntity extends BaseEntity {

    private String name;

    private String description;

    private Long coachId;

    private String targetType;

    private String difficulty;
}
