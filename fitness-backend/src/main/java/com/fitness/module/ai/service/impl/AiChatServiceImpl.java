package com.fitness.module.ai.service.impl;

import com.fitness.module.ai.client.AiApiClient;
import com.fitness.module.ai.service.AiChatService;
import com.fitness.module.ai.service.AiPromptBuilder;
import com.fitness.module.ai.service.UserDataCollector;
import com.fitness.module.ai.service.UserDataCollector.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final UserDataCollector userDataCollector;
    private final AiPromptBuilder promptBuilder;
    private final AiApiClient aiApiClient;

    // 用户对话历史缓存（最多保留10轮）
    private final Map<Long, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY_ROUNDS = 10;

    @Override
    public void chatStream(Long userId, String message, SseEmitter emitter) {
        try {
            UserContext ctx = userDataCollector.collect(userId);
            String systemPrompt = promptBuilder.buildSystemPrompt(ctx);

            // 构建消息列表：system + 历史 + 当前消息
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));

            // 添加历史对话记录
            List<Map<String, String>> history = conversationHistory.getOrDefault(userId, new ArrayList<>());
            messages.addAll(history);

            // 添加当前用户消息
            Map<String, String> userMsg = Map.of("role", "user", "content", message);
            messages.add(userMsg);

            aiApiClient.callStream(messages, emitter);

            // 对话成功后，将用户消息加入历史（用于上下文连贯）
            history.add(userMsg);
            // 限制历史轮数（system 不计入，只计 user/assistant 对）
            while (history.size() > MAX_HISTORY_ROUNDS * 2) {
                history.remove(0);
            }
            conversationHistory.put(userId, history);
        } catch (Exception e) {
            log.error("AI chat stream failed for userId={}", userId, e);
            try {
                emitter.send(SseEmitter.event().data("抱歉，AI 服务暂时不可用，请稍后重试。"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    @Override
    public void saveAssistantResponse(Long userId, String response) {
        List<Map<String, String>> history = conversationHistory.get(userId);
        if (history != null && response != null && !response.isEmpty()) {
            history.add(Map.of("role", "assistant", "content", response));
            // 再次限制轮数
            while (history.size() > MAX_HISTORY_ROUNDS * 2) {
                history.remove(0);
            }
        }
    }

    @Override
    public String generateWeeklySummary(Long userId) {
        try {
            UserContext ctx = userDataCollector.collect(userId);
            String prompt = promptBuilder.buildWeeklySummaryPrompt(ctx);

            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "你是一个专业的健身教练，擅长分析训练数据并给出建议。请用中文回复。"),
                    Map.of("role", "user", "content", prompt)
            );

            return aiApiClient.callSync(messages);
        } catch (Exception e) {
            log.error("Failed to generate weekly summary for userId={}", userId, e);
            return "生成周总结失败，请稍后重试。";
        }
    }

    @Override
    public String generateMonthlySummary(Long userId) {
        try {
            UserContext ctx = userDataCollector.collect(userId);
            String prompt = promptBuilder.buildMonthlySummaryPrompt(ctx);

            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "你是一个专业的健身教练，擅长分析训练数据并给出建议。请用中文回复。"),
                    Map.of("role", "user", "content", prompt)
            );

            return aiApiClient.callSync(messages);
        } catch (Exception e) {
            log.error("Failed to generate monthly summary for userId={}", userId, e);
            return "生成月总结失败，请稍后重试。";
        }
    }
}
