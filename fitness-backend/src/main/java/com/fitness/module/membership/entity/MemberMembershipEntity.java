package com.fitness.module.membership.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_membership")
public class MemberMembershipEntity extends BaseEntity {

    private Long userId;

    private Long cardId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer remainingTimes;

    private String status;

    private LocalDateTime frozenAt;
}
