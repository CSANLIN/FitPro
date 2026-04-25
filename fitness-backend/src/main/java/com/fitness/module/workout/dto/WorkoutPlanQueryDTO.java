package com.fitness.module.workout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "训练计划查询参数")
public class WorkoutPlanQueryDTO {

    @Schema(description = "关键词搜索", example = "减脂")
    private String keyword;

    @Schema(description = "状态 ACTIVE/COMPLETED/CANCELLED")
    private String status;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
