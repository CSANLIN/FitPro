package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "课程排行项")
public class CourseRankVO {

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "预约次数")
    private long bookingCount;
}
