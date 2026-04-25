package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "训练记录响应")
public class WorkoutRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "训练名称")
    private String name;

    @Schema(description = "训练日ID")
    private Long planDayId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时长(分钟)")
    private Integer durationMinutes;

    @Schema(description = "总训练量kg")
    private Integer totalVolume;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
