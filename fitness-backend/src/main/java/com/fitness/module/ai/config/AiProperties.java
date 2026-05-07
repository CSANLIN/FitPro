package com.fitness.module.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "deepseek")
public class AiProperties {
    private String apiKey;
    private String apiBaseUrl = "https://api.openai.com/v1";
    private String model = "gpt-4o-mini";
    private int maxTokens = 1024;
    private double temperature = 0.7;
    private int timeoutMs = 30000;
}
