package com.fitness.module.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "创建排课请求")
public class ScheduleCreateDTO {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @NotNull(message = "教练ID不能为空")
    @Schema(description = "教练ID")
    private Long coachId;

    @NotNull(message = "排课日期不能为空")
    @Future(message = "排课日期必须是将来的日期")
    @Schema(description = "排课日期")
    private LocalDate scheduleDate;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "上课地点")
    private String location;
}
