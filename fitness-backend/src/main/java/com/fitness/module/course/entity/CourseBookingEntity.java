package com.fitness.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_booking")
public class CourseBookingEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long scheduleId;

    private String status;

    private LocalDateTime bookedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
}
