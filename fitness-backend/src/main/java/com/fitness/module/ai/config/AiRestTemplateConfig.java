package com.fitness.module.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AiRestTemplateConfig {

    private final AiProperties aiProperties;

    @Bean
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(aiProperties.getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(aiProperties.getTimeoutMs() * 2))
                .build();
        // AI API 的错误响应由业务层处理，不需要 RestTemplate 抛异常
        restTemplate.setErrorHandler(new org.springframework.web.client.ResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
            @Override
            public void handleError(org.springframework.http.client.ClientHttpResponse response) {
                // 不处理，由调用方自行判断
            }
        });
        return restTemplate;
    }
}
