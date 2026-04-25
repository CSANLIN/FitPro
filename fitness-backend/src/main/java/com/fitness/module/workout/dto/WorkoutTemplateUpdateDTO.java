package com.fitness.module.workout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新训练模板请求参数")
public class WorkoutTemplateUpdateDTO {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

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
    private List<WorkoutTemplateCreateDTO.TemplateItemDTO> items;
}
