package com.fitness.module.exercise.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "运动分类响应")
public class ExerciseCategoryVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "胸部")
    private String name;

    @Schema(description = "图标", example = "chest")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "动作数量", example = "10")
    private Integer exerciseCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
