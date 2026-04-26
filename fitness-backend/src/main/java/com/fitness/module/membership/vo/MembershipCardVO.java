package com.fitness.module.membership.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "会员卡种响应")
public class MembershipCardVO {

    @Schema(description = "卡种ID")
    private Long id;

    @Schema(description = "卡种名称")
    private String cardName;

    @Schema(description = "类型 MONTH/QUARTER/YEAR/TIMES")
    private String cardType;

    @Schema(description = "有效天数")
    private Integer durationDays;

    @Schema(description = "总次数")
    private Integer totalTimes;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "状态 0下架 1上架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
