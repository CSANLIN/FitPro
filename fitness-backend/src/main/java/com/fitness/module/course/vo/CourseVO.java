package com.fitness.module.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "课程响应")
public class CourseVO {

    @Schema(description = "课程ID")
    private Long id;

    @Schema(description = "课程名称")
    private String name;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "类型 YOGA/BOXING/SPINNING/HIIT/OTHER")
    private String courseType;

    @Schema(description = "课程时长(分钟)")
    private Integer durationMinutes;

    @Schema(description = "最大容量")
    private Integer maxCapacity;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "状态 0下架 1上架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
