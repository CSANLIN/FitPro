package com.fitness.module.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class CourseEntity extends BaseEntity {

    private String name;

    private String description;

    private String coverImage;

    private String courseType;

    private Integer durationMinutes;

    private Integer maxCapacity;

    private BigDecimal price;

    private Integer status;
}
