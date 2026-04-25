package com.fitness.module.workout.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "训练记录详情响应")
public class WorkoutRecordDetailVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "训练名称")
    private String name;

    @Schema(description = "训练日ID")
    private Long planDayId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时长(分钟)")
    private Integer durationMinutes;

    @Schema(description = "总训练量kg")
    private Integer totalVolume;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "训练组列表")
    private List<RecordItemVO> items;

    @Data
    @Schema(description = "训练记录组项")
    public static class RecordItemVO {

        @Schema(description = "ID")
        private Long id;

        @Schema(description = "记录ID")
        private Long recordId;

        @Schema(description = "动作ID")
        private Long exerciseId;

        @Schema(description = "动作名称")
        private String exerciseName;

        @Schema(description = "第几组")
        private Integer setNumber;

        @Schema(description = "次数")
        private Integer reps;

        @Schema(description = "重量kg")
        private java.math.BigDecimal weight;

        @Schema(description = "时长秒数")
        private Integer durationSeconds;

        @Schema(description = "是否完成")
        private Integer completed;
    }
}
