package com.fitness.module.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "排课查询参数")
public class ScheduleQueryDTO {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "教练ID")
    private Long coachId;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
