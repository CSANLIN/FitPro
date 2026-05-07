package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "教练端预约学员响应")
public class CoachBookingVO {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "会员昵称")
    private String userName;

    @Schema(description = "会员头像")
    private String userAvatar;

    @Schema(description = "排课日期")
    private LocalDate scheduleDate;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "预约状态 BOOKED/CANCELLED/ATTENDED/ABSENT")
    private String status;

    @Schema(description = "预约时间")
    private LocalDateTime bookedAt;
}
