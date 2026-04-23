package com.fitness.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询请求参数
 */
@Data
@Schema(description = "用户查询请求参数")
public class UserQueryDTO {

    @Schema(description = "关键词（用户名/昵称/手机号）", example = "张三")
    private String keyword;

    @Schema(description = "角色：SUPER_ADMIN, COACH, MEMBER", example = "MEMBER")
    private String role;

    @Schema(description = "状态：0正常，1禁用", example = "0")
    private Integer status;

    @Schema(description = "页码，默认1", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数，默认10", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
}