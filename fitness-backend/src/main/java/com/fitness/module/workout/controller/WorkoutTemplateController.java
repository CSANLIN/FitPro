package com.fitness.module.workout.controller;

import com.fitness.common.Result;
import com.fitness.module.workout.dto.WorkoutTemplateCreateDTO;
import com.fitness.module.workout.dto.WorkoutTemplateUpdateDTO;
import com.fitness.module.workout.service.WorkoutTemplateService;
import com.fitness.module.workout.vo.WorkoutTemplateDetailVO;
import com.fitness.module.workout.vo.WorkoutTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-templates")
@RequiredArgsConstructor
@Tag(name = "训练模板管理")
public class WorkoutTemplateController {

    private final WorkoutTemplateService workoutTemplateService;

    @GetMapping
    @Operation(summary = "获取全部训练模板")
    public Result<List<WorkoutTemplateVO>> listAll() {
        return Result.success(workoutTemplateService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模板详情（含动作列表）")
    public Result<WorkoutTemplateDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(workoutTemplateService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建训练模板")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<WorkoutTemplateDetailVO> create(@RequestBody @Valid WorkoutTemplateCreateDTO dto) {
        Long coachId = getCurrentUserId();
        return Result.success(workoutTemplateService.create(dto, coachId));
    }

    @PutMapping
    @Operation(summary = "更新训练模板")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<WorkoutTemplateDetailVO> update(@RequestBody @Valid WorkoutTemplateUpdateDTO dto) {
        return Result.success(workoutTemplateService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除训练模板")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<Void> delete(@PathVariable Long id) {
        workoutTemplateService.delete(id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new RuntimeException("用户未认证");
        }
        return Long.valueOf(principal.toString());
    }
}
