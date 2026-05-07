package com.fitness.module.coach.controller;

import com.fitness.common.Result;
import com.fitness.module.coach.service.CoachAnalysisService;
import com.fitness.module.coach.vo.AttendanceTrendVO;
import com.fitness.module.coach.vo.CoachSummaryVO;
import com.fitness.module.coach.vo.CourseRankVO;
import com.fitness.module.coach.vo.TimeDistributionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach/analysis")
@RequiredArgsConstructor
@Tag(name = "教练端 - 授课分析")
@PreAuthorize("hasRole('COACH')")
public class CoachAnalysisController {

    private final CoachAnalysisService coachAnalysisService;

    @GetMapping("/summary")
    @Operation(summary = "授课统计概览")
    public Result<CoachSummaryVO> getSummary() {
        Long coachId = getCurrentUserId();
        return Result.success(coachAnalysisService.getSummary(coachId));
    }

    @GetMapping("/attendance-trend")
    @Operation(summary = "出勤趋势")
    public Result<List<AttendanceTrendVO>> getAttendanceTrend(
            @RequestParam(defaultValue = "week") String period) {
        Long coachId = getCurrentUserId();
        return Result.success(coachAnalysisService.getAttendanceTrend(coachId, period));
    }

    @GetMapping("/course-rank")
    @Operation(summary = "热门课程排行")
    public Result<List<CourseRankVO>> getCourseRank() {
        Long coachId = getCurrentUserId();
        return Result.success(coachAnalysisService.getCourseRank(coachId));
    }

    @GetMapping("/time-distribution")
    @Operation(summary = "热门时段分布")
    public Result<List<TimeDistributionVO>> getTimeDistribution() {
        Long coachId = getCurrentUserId();
        return Result.success(coachAnalysisService.getTimeDistribution(coachId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
