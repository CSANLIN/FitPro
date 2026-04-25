package com.fitness.module.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运动动作查询参数")
public class ExerciseQueryDTO {

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "目标肌群", example = "胸大肌")
    private String muscleGroup;

    @Schema(description = "所需器械", example = "杠铃")
    private String equipment;

    @Schema(description = "难度: BEGINNER/INTERMEDIATE/ADVANCED", example = "INTERMEDIATE")
    private String difficulty;

    @Schema(description = "关键词搜索(动作名称)", example = "卧推")
    private String keyword;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
