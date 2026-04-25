package com.fitness.module.user.controller;

import com.fitness.common.Result;
import com.fitness.module.user.dto.BodyRecordCreateDTO;
import com.fitness.module.user.service.BodyRecordService;
import com.fitness.module.user.vo.BodyRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/body-records")
@RequiredArgsConstructor
@Tag(name = "身体数据管理")
public class BodyRecordController {

    private final BodyRecordService bodyRecordService;

    /**
     * 获取当前登录用户的 ID
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new RuntimeException("用户未认证");
        }
        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("用户ID格式错误");
        }
    }

    @PostMapping
    @Operation(summary = "录入身体数据")
    public Result<BodyRecordVO> create(@RequestBody @Valid BodyRecordCreateDTO dto) {
        Long currentUserId = getCurrentUserId();
        return Result.success(bodyRecordService.create(currentUserId, dto));
    }

    @GetMapping
    @Operation(summary = "查询当前用户的身体数据记录")
    public Result<List<BodyRecordVO>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long currentUserId = getCurrentUserId();
        return Result.success(bodyRecordService.listByUser(currentUserId, startDate, endDate));
    }

    @GetMapping("/latest")
    @Operation(summary = "获取用户最新的身体数据")
    public Result<BodyRecordVO> getLatest() {
        Long currentUserId = getCurrentUserId();
        BodyRecordVO latest = bodyRecordService.getLatest(currentUserId);
        return Result.success(latest);
    }
}
