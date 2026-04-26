package com.fitness.module.course.controller;

import com.fitness.common.Result;
import com.fitness.module.course.dto.BookingCreateDTO;
import com.fitness.module.course.service.CourseBookingService;
import com.fitness.module.course.vo.BookingVO;
import com.fitness.module.course.vo.MyBookingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-bookings")
@RequiredArgsConstructor
@Tag(name = "课程预约管理")
public class CourseBookingController {

    private final CourseBookingService courseBookingService;

    @PostMapping
    @Operation(summary = "预约课程（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<Void> book(@RequestBody @Valid BookingCreateDTO dto) {
        Long userId = getCurrentUserId();
        courseBookingService.book(userId, dto.getScheduleId());
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消预约（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        courseBookingService.cancel(userId, id);
        return Result.success();
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的预约列表（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<List<MyBookingVO>> myBookings() {
        Long userId = getCurrentUserId();
        return Result.success(courseBookingService.listByUser(userId));
    }

    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "查看排课的预约列表（管理端/教练端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<BookingVO>> listBySchedule(@PathVariable Long scheduleId) {
        return Result.success(courseBookingService.listBySchedule(scheduleId));
    }

    @GetMapping
    @Operation(summary = "查看所有预约记录（管理端/教练端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<BookingVO>> listAll() {
        return Result.success(courseBookingService.listAll());
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
