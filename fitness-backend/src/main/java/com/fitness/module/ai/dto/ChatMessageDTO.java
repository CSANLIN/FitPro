package com.fitness.module.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "AI 对话请求")
public class ChatMessageDTO {

    @NotBlank(message = "消息不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000字")
    @Schema(description = "用户消息内容", example = "如何正确做深蹲？")
    private String content;
}
