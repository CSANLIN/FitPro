package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "训练计划详情响应")
public class WorkoutPlanDetailVO {

    @Schema(description = "计划ID")
    private Long id;

    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "会员名称")
    private String userName;

    @Schema(description = "教练名称")
    private String coachName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "训练日列表")
    private List<PlanDayVO> days;

    @Data
    @Schema(description = "训练日")
    public static class PlanDayVO {

        @Schema(description = "训练日ID")
        private Long id;

        @Schema(description = "计划ID")
        private Long planId;

        @Schema(description = "星期几 1-7")
        private Integer dayOfWeek;

        @Schema(description = "训练日名称")
        private String name;

        @Schema(description = "关联模板ID")
        private Long templateId;

        @Schema(description = "动作列表")
        private List<DayItemVO> items;

        @Data
        @Schema(description = "训练日动作")
        public static class DayItemVO {

            @Schema(description = "ID")
            private Long id;

            @Schema(description = "训练日ID")
            private Long planDayId;

            @Schema(description = "动作ID")
            private Long exerciseId;

            @Schema(description = "动作名称")
            private String exerciseName;

            @Schema(description = "组数")
            private Integer sets;

            @Schema(description = "每组次数")
            private Integer reps;

            @Schema(description = "建议重量kg")
            private java.math.BigDecimal weight;

            @Schema(description = "组间休息秒数")
            private Integer restSeconds;

            @Schema(description = "排序")
            private Integer sortOrder;
        }
    }
}
