package com.fitness.module.checkin.controller;

import com.fitness.common.Result;
import com.fitness.module.checkin.service.CheckInService;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.checkin.vo.CheckInVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/check-ins")
@RequiredArgsConstructor
@Tag(name = "签到管理")
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    @Operation(summary = "签到（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<Void> checkIn() {
        Long userId = getCurrentUserId();
        checkInService.checkIn(userId);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的签到记录（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<List<CheckInVO>> myRecords() {
        Long userId = getCurrentUserId();
        return Result.success(checkInService.listByUser(userId));
    }

    @GetMapping("/my/stats")
    @Operation(summary = "获取签到统计（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<CheckInStatsVO> myStats() {
        Long userId = getCurrentUserId();
        return Result.success(checkInService.getStats(userId));
    }

    @GetMapping
    @Operation(summary = "查看所有签到记录（管理端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<CheckInVO>> listAll() {
        return Result.success(checkInService.listAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查看指定会员的签到记录（管理员端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<CheckInVO>> listByUser(@PathVariable Long userId) {
        return Result.success(checkInService.listByUser(userId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
