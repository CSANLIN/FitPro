package com.fitness.module.system.aop;

import com.fitness.module.system.entity.OperationLogEntity;
import com.fitness.module.system.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final OperationLogMapper operationLogMapper;

    @Pointcut("execution(* com.fitness.module..controller.*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut() && !execution(* com.fitness.module.system.controller.OperationLogController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        String method = request != null ? request.getMethod() : "UNKNOWN";
        String url = request != null ? request.getRequestURI() : "UNKNOWN";
        String ip = request != null ? getClientIp(request) : "UNKNOWN";

        // 获取当前用户
        Long userId = null;
        String username = "anonymous";
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof String && !"anonymousUser".equals(principal)) {
                userId = Long.valueOf(principal.toString());
                username = principal.toString();
            }
        } catch (Exception e) {
            // 忽略未认证用户
        }

        Object result;
        Integer status = 1;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            status = 0;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 只记录 POST/PUT/DELETE 操作
            if (request != null && !"GET".equalsIgnoreCase(request.getMethod())) {
                try {
                    String module = extractModule(url);
                    String operation = extractOperation(request.getMethod());

                    OperationLogEntity logEntity = new OperationLogEntity();
                    logEntity.setUserId(userId);
                    logEntity.setUsername(username);
                    logEntity.setModule(module);
                    logEntity.setOperation(operation);
                    logEntity.setMethod(method);
                    logEntity.setUrl(url);
                    logEntity.setIp(ip);
                    logEntity.setDuration((int) duration);
                    logEntity.setStatus(status);
                    logEntity.setParams(request.getQueryString());
                    logEntity.setCreatedAt(LocalDateTime.now());

                    operationLogMapper.insert(logEntity);
                } catch (Exception e) {
                    log.error("保存操作日志失败", e);
                }
            }
        }

        return result;
    }

    private String extractModule(String url) {
        if (url == null) return "未知";
        String[] parts = url.split("/");
        if (parts.length >= 3) {
            return parts[2]; // /api/{module}/...
        }
        return "未知";
    }

    private String extractOperation(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            default -> "其他";
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
