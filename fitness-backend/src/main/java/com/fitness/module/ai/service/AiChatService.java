package com.fitness.module.ai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatService {

    /**
     * 流式对话，通过 SseEmitter 推送 AI 回复
     */
    void chatStream(Long userId, String message, SseEmitter emitter);

    /**
     * 生成周训练总结
     */
    String generateWeeklySummary(Long userId);

    /**
     * 生成月训练总结
     */
    String generateMonthlySummary(Long userId);

    /**
     * 保存 AI 回复到对话历史（由前端在流结束后调用）
     */
    void saveAssistantResponse(Long userId, String response);
}
