package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "授课概览响应")
public class CoachSummaryVO {

    @Schema(description = "总排课数")
    private long totalSchedules;

    @Schema(description = "总预约数")
    private long totalBookings;

    @Schema(description = "出勤率(百分比)")
    private double attendanceRate;

    @Schema(description = "总学员数")
    private long totalStudents;
}
