package com.fitness.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "公告创建/更新DTO")
public class AnnouncementCreateDTO {

    @NotBlank(message = "公告标题不能为空")
    @Schema(description = "标题")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型 NOTICE/ACTIVITY/MAINTENANCE")
    private String type;

    @Schema(description = "是否置顶 0否 1是")
    private Integer isTop;
}
