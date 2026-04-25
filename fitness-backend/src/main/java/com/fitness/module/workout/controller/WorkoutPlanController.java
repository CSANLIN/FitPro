package com.fitness.module.workout.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.workout.dto.WorkoutPlanCreateDTO;
import com.fitness.module.workout.dto.WorkoutPlanQueryDTO;
import com.fitness.module.workout.service.WorkoutPlanService;
import com.fitness.module.workout.vo.WorkoutPlanDetailVO;
import com.fitness.module.workout.vo.WorkoutPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-plans")
@RequiredArgsConstructor
@Tag(name = "训练计划管理")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @GetMapping
    @Operation(summary = "分页查询训练计划（会员端查看自己的，教练/管理端查看全部）")
    public Result<PageResult<WorkoutPlanVO>> list(WorkoutPlanQueryDTO query) {
        Long userId = getCurrentUserId();
        // 如果是会员，只能查看自己的计划
        String role = getCurrentUserRole();
        Long filterUserId = role.contains("MEMBER") ? userId : null;
        return Result.success(workoutPlanService.pageList(query, filterUserId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取计划详情（含训练日和动作）")
    public Result<WorkoutPlanDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(workoutPlanService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建训练计划")
    public Result<WorkoutPlanDetailVO> create(@RequestBody @Valid WorkoutPlanCreateDTO dto) {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        Long coachId = role.contains("MEMBER") ? null : userId;
        return Result.success(workoutPlanService.create(dto, userId, coachId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新计划状态 ACTIVE/COMPLETED/CANCELLED")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        workoutPlanService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练计划")
    public Result<Void> delete(@PathVariable Long id) {
        workoutPlanService.delete(id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new RuntimeException("用户未认证");
        }
        return Long.valueOf(principal.toString());
    }

    private String getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("");
    }
}
