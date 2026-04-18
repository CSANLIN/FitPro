package com.fitness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FitPro 健身管理系统 API 文档")
                        .description("FitPro 是一个完整的健身管理系统，包含会员管理、课程预约、训练计划、会籍签到等模块。")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FitPro 开发团队")
                                .email("dev@fitpro.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}