package com.fitness.module.system.controller;

import com.fitness.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "系统健康检查")
public class HealthController {

    @GetMapping
    @Operation(summary = "健康检查接口", description = "用于验证后端服务是否正常运行")
    public Result<String> health() {
        return Result.success("ok");
    }
}