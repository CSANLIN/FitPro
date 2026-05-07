package com.fitness.module.payment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_order")
public class PaymentOrderEntity extends BaseEntity {

    private String orderNo;

    private Long userId;

    private Long scheduleId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidAt;
}
