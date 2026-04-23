package com.fitness.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码修改请求参数
 */
@Data
@Schema(description = "密码修改请求参数")
public class PasswordUpdateDTO {

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "newPassword123")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", example = "newPassword123")
    private String confirmPassword;
}