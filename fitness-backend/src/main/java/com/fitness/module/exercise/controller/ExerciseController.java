package com.fitness.module.exercise.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.exercise.dto.ExerciseCreateDTO;
import com.fitness.module.exercise.dto.ExerciseQueryDTO;
import com.fitness.module.exercise.dto.ExerciseUpdateDTO;
import com.fitness.module.exercise.service.ExerciseService;
import com.fitness.module.exercise.vo.ExerciseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "运动动作管理")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    @Operation(summary = "分页查询运动动作（支持多条件筛选）")
    public Result<PageResult<ExerciseVO>> list(ExerciseQueryDTO query) {
        return Result.success(exerciseService.pageList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取动作详情")
    public Result<ExerciseVO> getDetail(@PathVariable Long id) {
        return Result.success(exerciseService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建运动动作（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<ExerciseVO> create(@RequestBody @Valid ExerciseCreateDTO dto) {
        return Result.success(exerciseService.create(dto));
    }

    @PutMapping
    @Operation(summary = "更新运动动作（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<ExerciseVO> update(@RequestBody @Valid ExerciseUpdateDTO dto) {
        return Result.success(exerciseService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运动动作（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return Result.success();
    }
}
