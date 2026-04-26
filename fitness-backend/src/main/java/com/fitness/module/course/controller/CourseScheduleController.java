package com.fitness.module.course.controller;

import com.fitness.common.Result;
import com.fitness.module.course.dto.ScheduleCreateDTO;
import com.fitness.module.course.dto.ScheduleQueryDTO;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.service.CourseScheduleService;
import com.fitness.module.course.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-schedules")
@RequiredArgsConstructor
@Tag(name = "排课管理")
public class CourseScheduleController {

    private final CourseScheduleService courseScheduleService;

    @GetMapping
    @Operation(summary = "按日期范围查询排课（支持按课程/教练筛选）")
    public Result<List<ScheduleVO>> list(ScheduleQueryDTO query) {
        Long userId = getCurrentUserId();
        return Result.success(courseScheduleService.listByDateRange(query, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取排课详情")
    public Result<ScheduleVO> getDetail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return Result.success(courseScheduleService.getDetail(id, userId));
    }

    @PostMapping
    @Operation(summary = "创建排课（管理端/教练端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<CourseScheduleEntity> create(@RequestBody @Valid ScheduleCreateDTO dto) {
        return Result.success(courseScheduleService.create(dto));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消排课")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<Void> cancel(@PathVariable Long id) {
        courseScheduleService.cancel(id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
