package com.fitness.module.workout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "创建训练计划请求参数")
public class WorkoutPlanCreateDTO {

    @Schema(description = "会员ID（教练/管理端创建时指定）")
    private Long userId;

    @NotBlank(message = "计划名称不能为空")
    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "描述")
    private String description;

    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @NotEmpty(message = "至少添加一个训练日")
    @Valid
    @Schema(description = "训练日列表")
    private List<PlanDayDTO> days;

    @Data
    @Schema(description = "训练日")
    public static class PlanDayDTO {

        @Schema(description = "星期几 1-7", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer dayOfWeek;

        @Schema(description = "训练日名称", example = "胸肌训练日")
        private String name;

        @Schema(description = "关联模板ID")
        private Long templateId;

        @Valid
        @Schema(description = "训练日动作列表")
        private List<PlanDayItemDTO> items;

        @Data
        @Schema(description = "训练日动作项")
        public static class PlanDayItemDTO {

            @Schema(description = "动作ID", requiredMode = Schema.RequiredMode.REQUIRED)
            private Long exerciseId;

            @Schema(description = "组数", example = "4")
            private Integer sets = 4;

            @Schema(description = "每组次数", example = "12")
            private Integer reps = 12;

            @Schema(description = "建议重量kg")
            private java.math.BigDecimal weight;

            @Schema(description = "组间休息秒数", example = "60")
            private Integer restSeconds = 60;

            @Schema(description = "排序", example = "0")
            private Integer sortOrder = 0;
        }
    }
}
