package com.fitness.module.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建课程请求")
public class CourseCreateDTO {

    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "封面图URL")
    private String coverImage;

    @NotBlank(message = "课程类型不能为空")
    @Schema(description = "类型 YOGA/BOXING/SPINNING/HIIT/OTHER")
    private String courseType;

    @NotNull(message = "课程时长不能为空")
    @Min(value = 15, message = "课程时长最少15分钟")
    @Schema(description = "课程时长(分钟)")
    private Integer durationMinutes;

    @NotNull(message = "最大容量不能为空")
    @Min(value = 1, message = "最大容量至少1人")
    @Schema(description = "最大容量")
    private Integer maxCapacity;

    @Schema(description = "课程价格(默认0免费)")
    private BigDecimal price;
}
