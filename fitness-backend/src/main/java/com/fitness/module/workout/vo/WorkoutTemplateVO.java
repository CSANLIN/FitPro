package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "训练模板响应")
public class WorkoutTemplateVO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "教练名称")
    private String coachName;

    @Schema(description = "目标: FAT_LOSS/MUSCLE_GAIN/SHAPE")
    private String targetType;

    @Schema(description = "难度: BEGINNER/INTERMEDIATE/ADVANCED")
    private String difficulty;

    @Schema(description = "动作数量")
    private Integer itemCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
