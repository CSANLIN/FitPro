package com.fitness.module.workout.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.workout.dto.WorkoutRecordCreateDTO;
import com.fitness.module.workout.dto.WorkoutRecordQueryDTO;
import com.fitness.module.workout.service.WorkoutRecordService;
import com.fitness.module.workout.vo.WorkoutRecordDetailVO;
import com.fitness.module.workout.vo.WorkoutRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workout-records")
@RequiredArgsConstructor
@Tag(name = "训练记录管理")
public class WorkoutRecordController {

    private final WorkoutRecordService workoutRecordService;

    @GetMapping
    @Operation(summary = "分页查询当前用户训练记录")
    public Result<PageResult<WorkoutRecordVO>> list(WorkoutRecordQueryDTO query) {
        Long userId = getCurrentUserId();
        return Result.success(workoutRecordService.pageList(query, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取训练记录详情（含训练组）")
    public Result<WorkoutRecordDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(workoutRecordService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建训练记录")
    public Result<WorkoutRecordDetailVO> create(@RequestBody @Valid WorkoutRecordCreateDTO dto) {
        Long userId = getCurrentUserId();
        return Result.success(workoutRecordService.create(dto, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练记录")
    public Result<Void> delete(@PathVariable Long id) {
        workoutRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/stats/weekly")
    @Operation(summary = "本周训练统计（次数和总训练量）")
    public Result<Map<String, Object>> weeklyStats() {
        Long userId = getCurrentUserId();
        return Result.success(workoutRecordService.weeklyStats(userId));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new RuntimeException("用户未认证");
        }
        return Long.valueOf(principal.toString());
    }
}
