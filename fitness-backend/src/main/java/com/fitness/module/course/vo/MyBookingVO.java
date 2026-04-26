package com.fitness.module.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "我的预约响应(会员端)")
public class MyBookingVO {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "排课ID")
    private Long scheduleId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程封面")
    private String courseCover;

    @Schema(description = "课程类型")
    private String courseType;

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

    @Schema(description = "状态 BOOKED/CANCELLED/ATTENDED/ABSENT")
    private String status;

    @Schema(description = "预约时间")
    private LocalDateTime bookedAt;

    @Schema(description = "取消时间")
    private LocalDateTime cancelledAt;
}
