package com.fitness.module.system.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.system.dto.AnnouncementCreateDTO;
import com.fitness.module.system.service.AnnouncementService;
import com.fitness.module.system.vo.AnnouncementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "公告管理")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "分页查询公告")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<PageResult<AnnouncementVO>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(announcementService.pageList(pageNum, pageSize, keyword));
    }

    @PostMapping
    @Operation(summary = "创建公告")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> create(@RequestBody @Valid AnnouncementCreateDTO dto) {
        Long userId = getCurrentUserId();
        announcementService.create(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid AnnouncementCreateDTO dto) {
        announcementService.update(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "切换置顶状态")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> toggleTop(@PathVariable Long id) {
        announcementService.toggleTop(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换发布/草稿状态")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        announcementService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
