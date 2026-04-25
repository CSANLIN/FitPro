package com.fitness.module.exercise.controller;

import com.fitness.common.Result;
import com.fitness.module.exercise.dto.ExerciseCategoryCreateDTO;
import com.fitness.module.exercise.dto.ExerciseCategoryUpdateDTO;
import com.fitness.module.exercise.service.ExerciseCategoryService;
import com.fitness.module.exercise.vo.ExerciseCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-categories")
@RequiredArgsConstructor
@Tag(name = "运动分类管理")
public class ExerciseCategoryController {

    private final ExerciseCategoryService exerciseCategoryService;

    @GetMapping
    @Operation(summary = "获取全部分类列表（按排序升序）")
    public Result<List<ExerciseCategoryVO>> listAll() {
        return Result.success(exerciseCategoryService.listAll());
    }

    @PostMapping
    @Operation(summary = "创建运动分类（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<ExerciseCategoryVO> create(@RequestBody @Valid ExerciseCategoryCreateDTO dto) {
        return Result.success(exerciseCategoryService.create(dto));
    }

    @PutMapping
    @Operation(summary = "更新运动分类（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<ExerciseCategoryVO> update(@RequestBody @Valid ExerciseCategoryUpdateDTO dto) {
        return Result.success(exerciseCategoryService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运动分类（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        exerciseCategoryService.delete(id);
        return Result.success();
    }
}
