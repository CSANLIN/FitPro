package com.fitness.module.exercise.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "运动动作响应")
public class ExerciseVO {

    @Schema(description = "动作ID", example = "1")
    private Long id;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "胸部")
    private String categoryName;

    @Schema(description = "动作名称", example = "杠铃卧推")
    private String name;

    @Schema(description = "动作描述", example = "躺在卧推凳上，双手握杠铃杆...")
    private String description;

    @Schema(description = "目标肌群", example = "胸大肌,三角肌前束,肱三头肌")
    private String muscleGroup;

    @Schema(description = "所需器械", example = "杠铃,卧推凳")
    private String equipment;

    @Schema(description = "难度: BEGINNER/INTERMEDIATE/ADVANCED", example = "INTERMEDIATE")
    private String difficulty;

    @Schema(description = "教学视频URL")
    private String videoUrl;

    @Schema(description = "示意图URL")
    private String imageUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
