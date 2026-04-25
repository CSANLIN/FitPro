package com.fitness.module.workout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("workout_record_item")
public class WorkoutRecordItemEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long recordId;

    private Long exerciseId;

    private Integer setNumber;

    private Integer reps;

    private BigDecimal weight;

    private Integer durationSeconds;

    private Integer completed;
}
