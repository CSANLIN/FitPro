package com.fitness.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户信息更新请求参数
 */
@Data
@Schema(description = "用户信息更新请求参数")
public class UserUpdateDTO {

    @Size(min = 1, max = 20, message = "昵称长度必须在1-20个字符之间")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别：0未知，1男，2女", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDate birthday;
}