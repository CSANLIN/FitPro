package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "训练计划响应")
public class WorkoutPlanVO {

    @Schema(description = "计划ID")
    private Long id;

    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "会员名称")
    private String userName;

    @Schema(description = "教练名称")
    private String coachName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "状态 ACTIVE/COMPLETED/CANCELLED")
    private String status;

    @Schema(description = "训练日数量")
    private Integer dayCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
