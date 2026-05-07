package com.fitness.module.coach.controller;

import com.fitness.common.Result;
import com.fitness.module.coach.service.CoachScheduleService;
import com.fitness.module.coach.vo.CoachBookingVO;
import com.fitness.module.coach.vo.CoachScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/coach/schedules")
@RequiredArgsConstructor
@Tag(name = "教练端 - 课程编排")
@PreAuthorize("hasRole('COACH')")
public class CoachScheduleController {

    private final CoachScheduleService coachScheduleService;

    @GetMapping
    @Operation(summary = "获取我的排课列表")
    public Result<List<CoachScheduleVO>> listMySchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long coachId = getCurrentUserId();
        return Result.success(coachScheduleService.listMySchedules(coachId, startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "创建排课")
    public Result<CoachScheduleVO> createSchedule(@RequestBody @NotNull CreateScheduleRequest request) {
        Long coachId = getCurrentUserId();
        return Result.success(coachScheduleService.createSchedule(
                coachId, request.courseId, request.scheduleDate,
                request.startTime, request.endTime, request.location));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消排课")
    public Result<Void> cancelSchedule(@PathVariable Long id) {
        Long coachId = getCurrentUserId();
        coachScheduleService.cancelSchedule(coachId, id);
        return Result.success();
    }

    @GetMapping("/{id}/bookings")
    @Operation(summary = "查看排课的预约学员列表")
    public Result<List<CoachBookingVO>> listBookings(@PathVariable Long id) {
        Long coachId = getCurrentUserId();
        return Result.success(coachScheduleService.listBookings(coachId, id));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }

    // 内部请求类，避免为单个方法创建 DTO
    public record CreateScheduleRequest(
            @NotNull Long courseId,
            @NotNull @Future LocalDate scheduleDate,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            String location) {}
}
