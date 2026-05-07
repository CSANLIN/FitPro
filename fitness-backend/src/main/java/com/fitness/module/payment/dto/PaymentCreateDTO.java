package com.fitness.module.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建支付订单请求")
public class PaymentCreateDTO {

    @NotNull(message = "排课ID不能为空")
    @Schema(description = "排课ID")
    private Long scheduleId;
}
