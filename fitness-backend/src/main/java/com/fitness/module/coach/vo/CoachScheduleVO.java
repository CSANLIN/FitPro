package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "教练端排课响应")
public class CoachScheduleVO {

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

    @Schema(description = "课程时长(分钟)")
    private Integer durationMinutes;

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

    @Schema(description = "排课状态 UPCOMING/ONGOING/FINISHED/CANCELLED")
    private String status;
}
