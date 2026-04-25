package com.fitness.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "身体数据录入请求参数")
public class BodyRecordCreateDTO {

    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "20", message = "体重不能低于20kg")
    @DecimalMax(value = "300", message = "体重不能超过300kg")
    @Schema(description = "体重(kg)", example = "70.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal weight;

    @DecimalMin(value = "100", message = "身高不能低于100cm")
    @DecimalMax(value = "250", message = "身高不能超过250cm")
    @Schema(description = "身高(cm)", example = "175.0")
    private BigDecimal height;

    @DecimalMin(value = "3", message = "体脂率不能低于3%")
    @DecimalMax(value = "60", message = "体脂率不能超过60%")
    @Schema(description = "体脂率(%)", example = "18.5")
    private BigDecimal bodyFat;

    @DecimalMin(value = "50", message = "胸围不能低于50cm")
    @DecimalMax(value = "200", message = "胸围不能超过200cm")
    @Schema(description = "胸围(cm)", example = "100.0")
    private BigDecimal chest;

    @DecimalMin(value = "40", message = "腰围不能低于40cm")
    @DecimalMax(value = "200", message = "腰围不能超过200cm")
    @Schema(description = "腰围(cm)", example = "80.0")
    private BigDecimal waist;

    @DecimalMin(value = "50", message = "臀围不能低于50cm")
    @DecimalMax(value = "200", message = "臀围不能超过200cm")
    @Schema(description = "臀围(cm)", example = "95.0")
    private BigDecimal hip;

    @NotNull(message = "记录日期不能为空")
    @Schema(description = "记录日期", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate recordDate;

    @Schema(description = "备注", example = "早晨空腹测量")
    private String remark;
}
