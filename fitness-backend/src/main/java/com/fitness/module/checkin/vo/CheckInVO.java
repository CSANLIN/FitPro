package com.fitness.module.checkin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "签到记录响应")
public class CheckInVO {

    @Schema(description = "签到ID")
    private Long id;

    @Schema(description = "会员ID")
    private Long userId;

    @Schema(description = "会员名称")
    private String userName;

    @Schema(description = "会籍ID")
    private Long membershipId;

    @Schema(description = "签到时间")
    private LocalDateTime checkInTime;

    @Schema(description = "签到方式 MANUAL/QR_CODE")
    private String checkInType;

    @Schema(description = "签到日期")
    private String checkInDate;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
