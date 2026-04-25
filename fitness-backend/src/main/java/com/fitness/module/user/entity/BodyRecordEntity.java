package com.fitness.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("body_record")
public class BodyRecordEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private BigDecimal weight;

    private BigDecimal height;

    private BigDecimal bodyFat;

    private BigDecimal bmi;

    private BigDecimal chest;

    private BigDecimal waist;

    private BigDecimal hip;

    private LocalDate recordDate;

    private String remark;

    private LocalDateTime createdAt;
}
