package com.fitness.module.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新运动分类请求参数")
public class ExerciseCategoryUpdateDTO {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "胸部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "图标", example = "chest")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}
