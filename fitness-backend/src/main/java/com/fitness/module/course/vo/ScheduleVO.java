package com.fitness.module.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "排课响应")
public class ScheduleVO {

    @Schema(description = "排课ID")
    private Long id;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程封面")
    private String courseCover;

    @Schema(description = "课程类型")
    private String courseType;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "教练名称")
    private String coachName;

    @Schema(description = "排课日期")
    private LocalDate scheduleDate;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "上课地点")
    private String location;

    @Schema(description = "当前预约人数")
    private Integer currentCount;

    @Schema(description = "最大容量")
    private Integer maxCapacity;

    @Schema(description = "状态 UPCOMING/ONGOING/FINISHED/CANCELLED")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "是否已预约")
    private Boolean booked;
}
