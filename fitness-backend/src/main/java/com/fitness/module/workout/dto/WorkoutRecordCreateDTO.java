package com.fitness.module.workout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "创建训练记录请求参数")
public class WorkoutRecordCreateDTO {

    @Schema(description = "训练日ID（可选，从计划进入时关联）")
    private Long planDayId;

    @NotBlank(message = "训练名称不能为空")
    @Schema(description = "训练名称", example = "今日训练", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    private String note;

    @NotEmpty(message = "至少添加一组记录")
    @Valid
    @Schema(description = "训练组列表")
    private List<RecordItemDTO> items;

    @Data
    @Schema(description = "训练记录组项")
    public static class RecordItemDTO {

        @Schema(description = "动作ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long exerciseId;

        @Schema(description = "第几组", example = "1")
        private Integer setNumber;

        @Schema(description = "次数", example = "12")
        private Integer reps;

        @Schema(description = "重量kg", example = "20.0")
        private java.math.BigDecimal weight;

        @Schema(description = "时长秒数（有氧用）")
        private Integer durationSeconds;

        @Schema(description = "是否完成 1是0否", example = "1")
        private Integer completed = 1;
    }
}
