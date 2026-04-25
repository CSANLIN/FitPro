package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("workout_plan")
public class WorkoutPlanEntity extends BaseEntity {

    private Long userId;

    private Long coachId;

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;
}
