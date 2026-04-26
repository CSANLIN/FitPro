package com.fitness.module.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("course_schedule")
public class CourseScheduleEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long courseId;

    private Long coachId;

    private LocalDate scheduleDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private Integer currentCount;

    private Integer maxCapacity;

    private String status;

    private LocalDateTime createdAt;
}
