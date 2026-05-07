package com.fitness.module.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.module.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiApiClient {

    private final AiProperties aiProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 非流式调用 AI API，返回完整响应文本
     */
    public String callSync(List<Map<String, String>> messages) {
        String url = aiProperties.getApiBaseUrl() + "/chat/completions";
        Map<String, Object> body = Map.of(
                "model", aiProperties.getModel(),
                "messages", messages,
                "max_tokens", aiProperties.getMaxTokens(),
                "temperature", aiProperties.getTemperature(),
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, JsonNode.class);
            JsonNode bodyNode = response.getBody();
            if (bodyNode == null) {
                throw new RuntimeException("AI API 返回空响应");
            }
            // 检查 API 错误响应
            if (bodyNode.has("error")) {
                String errMsg = bodyNode.path("error").path("message").asText("未知错误");
                log.warn("AI API returned error: {}", errMsg);
                throw new RuntimeException("AI 服务错误: " + errMsg);
            }
            JsonNode choice = bodyNode.path("choices").get(0);
            return choice != null ? choice.path("message").path("content").asText() : "";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI API sync call failed: {}", e.getMessage());
            throw new RuntimeException("AI 服务暂时不可用，请稍后重试", e);
        }
    }

    /**
     * 流式调用 AI API，通过 SseEmitter 逐块推送响应
     * 使用 HttpURLConnection 直接调用，避免 RestTemplate 的流式处理限制
     */
    public void callStream(List<Map<String, String>> messages, SseEmitter emitter) {
        String apiUrl = aiProperties.getApiBaseUrl() + "/chat/completions";

        Map<String, Object> body = Map.of(
                "model", aiProperties.getModel(),
                "messages", messages,
                "max_tokens", aiProperties.getMaxTokens(),
                "temperature", aiProperties.getTemperature(),
                "stream", true
        );

        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            log.debug("Calling DeepSeek API: {}", apiUrl);

            java.net.URL url = new java.net.URL(apiUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + aiProperties.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(aiProperties.getTimeoutMs());
            conn.setReadTimeout(aiProperties.getTimeoutMs() * 2);

            // 写入请求体
            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));

            int responseCode = conn.getResponseCode();
            log.debug("DeepSeek response code: {}", responseCode);

            java.io.InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = conn.getInputStream();
            } else {
                String errorBody = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                log.warn("DeepSeek API error: {} {}", responseCode, errorBody);
                emitter.send(SseEmitter.event().data("AI 服务返回错误，请检查 API Key 和配置。"));
                emitter.complete();
                return;
            }

            // 读取 SSE 流
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    log.debug("DeepSeek raw line {}: {}", lineCount, line);
                    if (!line.startsWith("data: ")) continue;

                    String data = line.substring(6).trim();
                    if ("[DONE]".equals(data)) {
                        emitter.send(SseEmitter.event().data("[DONE]"));
                        break;
                    }

                    try {
                        JsonNode jsonNode = objectMapper.readTree(data);
                        JsonNode delta = jsonNode.path("choices").get(0).path("delta");
                        if (delta == null) continue;

                        String content = delta.path("content").asText();
                        if (!content.isEmpty()) {
                            emitter.send(SseEmitter.event().data(content));
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse SSE line: {}", line, e);
                    }
                }
                log.info("DeepSeek stream ended, total lines: {}", lineCount);
                emitter.complete();
            }
        } catch (java.net.SocketTimeoutException e) {
            log.error("DeepSeek API timeout: {}", e.getMessage());
            try {
                emitter.send(SseEmitter.event().data("AI 服务响应超时，请稍后重试。"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        } catch (Exception e) {
            log.error("DeepSeek API call failed", e);
            try {
                emitter.send(SseEmitter.event().data("AI 服务暂时不可用，请稍后重试。"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }
    }
}
