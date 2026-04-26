package com.fitness.module.membership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "续费会籍请求")
public class MembershipRenewDTO {

    @NotNull(message = "会籍ID不能为空")
    @Schema(description = "会籍ID")
    private Long membershipId;

    @NotNull(message = "卡种ID不能为空")
    @Schema(description = "新卡种ID")
    private Long cardId;
}
