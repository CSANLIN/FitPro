package com.fitness.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
@Schema(description = "用户信息响应")
public class UserVO {

    @Schema(description = "用户ID", example = "123456789")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别：0未知，1男，2女", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;

    @Schema(description = "角色：SUPER_ADMIN, COACH, MEMBER", example = "MEMBER")
    private String role;

    @Schema(description = "状态：0正常，1禁用", example = "0")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedAt;
}