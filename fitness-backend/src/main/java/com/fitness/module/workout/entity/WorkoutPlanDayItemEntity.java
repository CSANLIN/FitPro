package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("workout_plan_day_item")
public class WorkoutPlanDayItemEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long planDayId;

    private Long exerciseId;

    private Integer sets;

    private Integer reps;

    private BigDecimal weight;

    private Integer restSeconds;

    private Integer sortOrder;
}
