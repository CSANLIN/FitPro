package com.fitness.module.workout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建训练模板请求参数")
public class WorkoutTemplateCreateDTO {

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "目标: FAT_LOSS/MUSCLE_GAIN/SHAPE")
    private String targetType;

    @Schema(description = "难度: BEGINNER/INTERMEDIATE/ADVANCED")
    private String difficulty;

    @NotEmpty(message = "至少添加一个动作")
    @Valid
    @Schema(description = "模板动作列表")
    private List<TemplateItemDTO> items;

    @Data
    @Schema(description = "模板动作项")
    public static class TemplateItemDTO {

        @Schema(description = "动作ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long exerciseId;

        @Schema(description = "组数", example = "3")
        private Integer sets = 3;

        @Schema(description = "每组次数", example = "10")
        private Integer reps = 10;

        @Schema(description = "组间休息秒数", example = "60")
        private Integer restSeconds = 60;

        @Schema(description = "排序", example = "0")
        private Integer sortOrder = 0;
    }
}
