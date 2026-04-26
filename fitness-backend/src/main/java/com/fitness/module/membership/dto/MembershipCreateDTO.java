package com.fitness.module.membership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "办理会籍请求")
public class MembershipCreateDTO {

    @NotNull(message = "会员ID不能为空")
    @Schema(description = "会员ID")
    private Long userId;

    @NotNull(message = "卡种ID不能为空")
    @Schema(description = "卡种ID")
    private Long cardId;
}
