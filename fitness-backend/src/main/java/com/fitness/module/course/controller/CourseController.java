package com.fitness.module.course.controller;

import com.fitness.common.Result;
import com.fitness.module.course.dto.CourseCreateDTO;
import com.fitness.module.course.dto.CourseUpdateDTO;
import com.fitness.module.course.service.CourseService;
import com.fitness.module.course.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "获取课程列表（管理端:全部，会员端:已上架）")
    public Result<List<CourseVO>> list(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String courseType,
                                       @RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return Result.success(courseService.listAll(keyword));
        }
        return Result.success(courseService.listPublished(courseType));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情")
    public Result<CourseVO> getDetail(@PathVariable Long id) {
        return Result.success(courseService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建课程")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<CourseVO> create(@RequestBody @Valid CourseCreateDTO dto) {
        return Result.success(courseService.create(dto));
    }

    @PutMapping
    @Operation(summary = "更新课程")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<CourseVO> update(@RequestBody @Valid CourseUpdateDTO dto) {
        return Result.success(courseService.update(dto));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换课程上下架状态")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        courseService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }
}
