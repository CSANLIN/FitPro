package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "出勤趋势数据点")
public class AttendanceTrendVO {

    @Schema(description = "周期标签(如 2026-01)")
    private String period;

    @Schema(description = "总预约数")
    private long total;

    @Schema(description = "出勤数")
    private long attended;

    @Schema(description = "出勤率")
    private double rate;
}
