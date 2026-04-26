package com.fitness.module.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "预约课程请求")
public class BookingCreateDTO {

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID")
    private Long scheduleId;
}
