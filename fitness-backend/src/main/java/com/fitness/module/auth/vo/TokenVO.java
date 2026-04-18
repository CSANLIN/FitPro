package com.fitness.module.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 令牌响应
 */
@Data
@Schema(description = "令牌响应")
public class TokenVO {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "令牌类型", example = "Bearer", defaultValue = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "访问令牌过期时间（秒）", example = "7200")
    private Integer expiresIn;

    @Schema(description = "刷新令牌过期时间（秒）", example = "604800")
    private Integer refreshExpiresIn;
}