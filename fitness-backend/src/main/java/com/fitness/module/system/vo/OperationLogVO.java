package com.fitness.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "操作日志响应VO")
public class OperationLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "操作人ID")
    private Long userId;

    @Schema(description = "操作人用户名")
    private String username;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "耗时ms")
    private Integer duration;

    @Schema(description = "状态 0失败 1成功")
    private Integer status;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "操作时间")
    private LocalDateTime createdAt;
}
