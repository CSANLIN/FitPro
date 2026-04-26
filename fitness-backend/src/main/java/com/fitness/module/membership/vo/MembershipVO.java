package com.fitness.module.membership.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "会籍信息响应")
public class MembershipVO {

    @Schema(description = "会籍ID")
    private Long id;

    @Schema(description = "会员ID")
    private Long userId;

    @Schema(description = "会员名称")
    private String userName;

    @Schema(description = "卡种ID")
    private Long cardId;

    @Schema(description = "卡种名称")
    private String cardName;

    @Schema(description = "卡种类型")
    private String cardType;

    @Schema(description = "卡种价格")
    private BigDecimal cardPrice;

    @Schema(description = "开始日期")
    private LocalDateTime startDate;

    @Schema(description = "到期日期")
    private LocalDateTime endDate;

    @Schema(description = "剩余天数")
    private Long remainingDays;

    @Schema(description = "剩余次数")
    private Integer remainingTimes;

    @Schema(description = "状态 ACTIVE/FROZEN/EXPIRED/CANCELLED")
    private String status;

    @Schema(description = "冻结时间")
    private LocalDateTime frozenAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
