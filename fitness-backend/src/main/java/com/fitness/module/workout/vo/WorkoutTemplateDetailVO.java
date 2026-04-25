package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "训练模板详情响应")
public class WorkoutTemplateDetailVO {

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

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "模板动作列表")
    private List<TemplateItemVO> items;

    @Data
    @Schema(description = "模板动作项")
    public static class TemplateItemVO {

        @Schema(description = "ID")
        private Long id;

        @Schema(description = "动作ID")
        private Long exerciseId;

        @Schema(description = "动作名称")
        private String exerciseName;

        @Schema(description = "组数")
        private Integer sets;

        @Schema(description = "每组次数")
        private Integer reps;

        @Schema(description = "组间休息秒数")
        private Integer restSeconds;

        @Schema(description = "排序")
        private Integer sortOrder;
    }
}
