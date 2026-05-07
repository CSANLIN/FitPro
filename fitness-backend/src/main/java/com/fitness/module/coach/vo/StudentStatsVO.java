package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "学员统计响应")
public class StudentStatsVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "会员昵称")
    private String userName;

    @Schema(description = "会员头像")
    private String userAvatar;

    @Schema(description = "总预约次数")
    private long totalBookings;

    @Schema(description = "出勤次数")
    private long attendedCount;

    @Schema(description = "出勤率")
    private double attendanceRate;

    @Schema(description = "最近上课时间")
    private LocalDate lastBookingDate;
}
