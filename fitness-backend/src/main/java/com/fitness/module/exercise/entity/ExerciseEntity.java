package com.fitness.module.exercise.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exercise")
public class ExerciseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long categoryId;

    private String name;

    private String description;

    private String muscleGroup;

    private String equipment;

    private String difficulty;

    private String videoUrl;

    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
