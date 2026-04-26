package com.fitness.module.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("membership_card")
public class MembershipCardEntity extends BaseEntity {

    private String cardName;

    private String cardType;

    private Integer durationDays;

    private Integer totalTimes;

    private BigDecimal price;

    private Integer status;
}
