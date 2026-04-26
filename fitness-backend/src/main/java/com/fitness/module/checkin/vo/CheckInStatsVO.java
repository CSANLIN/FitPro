package com.fitness.module.checkin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "签到统计响应")
public class CheckInStatsVO {

    @Schema(description = "本月签到天数")
    private long monthCount;

    @Schema(description = "连续签到天数")
    private long streakDays;

    @Schema(description = "本月已签到日期列表 (yyyy-MM-dd)")
    private List<String> checkInDates;
}
