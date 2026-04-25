package com.fitness.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "身体数据响应")
public class BodyRecordVO {

    @Schema(description = "记录ID", example = "123456789")
    private Long id;

    @Schema(description = "体重(kg)", example = "70.5")
    private BigDecimal weight;

    @Schema(description = "身高(cm)", example = "175.0")
    private BigDecimal height;

    @Schema(description = "体脂率(%)", example = "18.5")
    private BigDecimal bodyFat;

    @Schema(description = "BMI", example = "23.0")
    private BigDecimal bmi;

    @Schema(description = "胸围(cm)", example = "100.0")
    private BigDecimal chest;

    @Schema(description = "腰围(cm)", example = "80.0")
    private BigDecimal waist;

    @Schema(description = "臀围(cm)", example = "95.0")
    private BigDecimal hip;

    @Schema(description = "记录日期", example = "2024-01-15")
    private LocalDate recordDate;

    @Schema(description = "备注", example = "早晨空腹测量")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-15T08:00:00")
    private LocalDateTime createdAt;
}
