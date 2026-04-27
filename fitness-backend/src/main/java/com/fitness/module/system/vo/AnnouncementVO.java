package com.fitness.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "公告响应VO")
public class AnnouncementVO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型 NOTICE/ACTIVITY/MAINTENANCE")
    private String type;

    @Schema(description = "是否置顶 0否 1是")
    private Integer isTop;

    @Schema(description = "状态 0草稿 1发布")
    private Integer status;

    @Schema(description = "创建人ID")
    private Long createdBy;

    @Schema(description = "创建人名称")
    private String creatorName;

    @Schema(description = "发布时间")
    private LocalDateTime publishAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
