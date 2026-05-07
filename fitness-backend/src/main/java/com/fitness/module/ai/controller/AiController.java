package com.fitness.module.ai.controller;

import com.fitness.common.Result;
import com.fitness.module.ai.dto.ChatMessageDTO;
import com.fitness.module.ai.service.AiChatService;
import com.fitness.module.membership.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI 健身助手")
public class AiController {

    private final AiChatService aiChatService;
    private final MembershipService membershipService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 健身助手对话（SSE 流式响应）")
    public SseEmitter chat(@RequestBody @Valid ChatMessageDTO dto) {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        if ("MEMBER".equals(role)) {
            membershipService.requireActiveMembership(userId);
        }
        SseEmitter emitter = new SseEmitter(300_000L);
        emitter.onTimeout(() -> log.warn("SSE timeout for userId={}", userId));
        emitter.onError(e -> log.error("SSE error for userId={}: {}", userId, e.getMessage()));
        emitter.onCompletion(() -> log.debug("SSE completed for userId={}", userId));
        new Thread(() -> aiChatService.chatStream(userId != null ? userId : -1L, dto.getContent(), emitter)).start();
        return emitter;
    }

    @PostMapping("/chat/save-response")
    @Operation(summary = "保存 AI 回复到对话历史")
    public Result<Void> saveResponse(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            aiChatService.saveAssistantResponse(userId, body.get("response"));
        }
        return Result.success();
    }

    @GetMapping("/summary/weekly")
    @Operation(summary = "生成本周训练总结")
    public Result<String> weeklySummary() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.success("请先登录后再查看训练总结");
        String summary = aiChatService.generateWeeklySummary(userId);
        return Result.success(summary);
    }

    @GetMapping("/summary/monthly")
    @Operation(summary = "生成本月训练总结")
    public Result<String> monthlySummary() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.success("请先登录后再查看训练总结");
        String summary = aiChatService.generateMonthlySummary(userId);
        return Result.success(summary);
    }

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }

    private String getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");
    }
}
