package com.fitness.module.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建运动动作请求参数")
public class ExerciseCreateDTO {

    @NotNull(message = "所属分类不能为空")
    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @NotBlank(message = "动作名称不能为空")
    @Schema(description = "动作名称", example = "杠铃卧推", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "动作描述", example = "躺在卧推凳上，双手握杠铃杆...")
    private String description;

    @Schema(description = "目标肌群", example = "胸大肌,三角肌前束,肱三头肌")
    private String muscleGroup;

    @Schema(description = "所需器械", example = "杠铃,卧推凳")
    private String equipment;

    @Schema(description = "难度: BEGINNER/INTERMEDIATE/ADVANCED", example = "INTERMEDIATE")
    private String difficulty;

    @Schema(description = "教学视频URL", example = "https://example.com/video/bench-press.mp4")
    private String videoUrl;

    @Schema(description = "示意图URL", example = "https://example.com/img/bench-press.png")
    private String imageUrl;
}
