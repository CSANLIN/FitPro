package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("workout_plan_day")
public class WorkoutPlanDayEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long planId;

    private Integer dayOfWeek;

    private String name;

    private Long templateId;
}
