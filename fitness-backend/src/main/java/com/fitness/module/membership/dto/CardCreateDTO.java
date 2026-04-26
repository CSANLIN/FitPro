package com.fitness.module.membership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建会员卡种请求")
public class CardCreateDTO {

    @NotBlank(message = "卡种名称不能为空")
    @Schema(description = "卡种名称")
    private String cardName;

    @NotBlank(message = "卡种类型不能为空")
    @Schema(description = "类型 MONTH/QUARTER/YEAR/TIMES")
    private String cardType;

    @Schema(description = "有效天数（期限卡）")
    private Integer durationDays;

    @Schema(description = "总次数（次卡）")
    private Integer totalTimes;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负")
    @Schema(description = "价格")
    private BigDecimal price;
}
