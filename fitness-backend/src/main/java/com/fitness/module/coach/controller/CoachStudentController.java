package com.fitness.module.coach.controller;

import com.fitness.common.Result;
import com.fitness.module.coach.service.CoachStudentService;
import com.fitness.module.coach.vo.StudentStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach/students")
@RequiredArgsConstructor
@Tag(name = "教练端 - 学员分析")
@PreAuthorize("hasRole('COACH')")
public class CoachStudentController {

    private final CoachStudentService coachStudentService;

    @GetMapping
    @Operation(summary = "获取学员列表")
    public Result<List<StudentStatsVO>> listStudents() {
        Long coachId = getCurrentUserId();
        return Result.success(coachStudentService.listStudents(coachId));
    }

    @GetMapping("/{userId}/stats")
    @Operation(summary = "学员详细统计")
    public Result<StudentStatsVO> getStudentStats(@PathVariable Long userId) {
        Long coachId = getCurrentUserId();
        return Result.success(coachStudentService.getStudentDetail(coachId, userId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
