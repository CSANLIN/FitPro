package com.fitness.module.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "支付结果响应")
public class PaymentResultVO {

    @Schema(description = "支付状态 SUCCESS/FAILED")
    private String status;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "订单编号")
    private String orderNo;
}
