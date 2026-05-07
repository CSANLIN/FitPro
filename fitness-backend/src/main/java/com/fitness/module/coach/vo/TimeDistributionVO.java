package com.fitness.module.coach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "时段分布项")
public class TimeDistributionVO {

    @Schema(description = "时段标签(上午/下午/晚上)")
    private String period;

    @Schema(description = "排课数")
    private long scheduleCount;

    @Schema(description = "预约数")
    private long bookingCount;
}
