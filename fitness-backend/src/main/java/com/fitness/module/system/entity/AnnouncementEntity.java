package com.fitness.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fitness.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_announcement")
public class AnnouncementEntity extends BaseEntity {

    private String title;

    private String content;

    private String type;

    private Integer isTop;

    private Integer status;

    private Long createdBy;

    private LocalDateTime publishAt;
}
