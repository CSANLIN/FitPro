package com.fitness.module.user.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.user.dto.PasswordUpdateDTO;
import com.fitness.module.user.dto.UserQueryDTO;
import com.fitness.module.user.dto.UserUpdateDTO;
import com.fitness.module.user.service.UserService;
import com.fitness.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户的 ID
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            throw new RuntimeException("用户未认证");
        }
        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("用户ID格式错误");
        }
    }

    @GetMapping
    @Operation(summary = "分页查询用户列表（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<PageResult<UserVO>> list(UserQueryDTO query) {
        return Result.success(userService.pageList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasRole('SUPER_ADMIN') or #id == authentication.principal")
    public Result<UserVO> getDetail(@PathVariable Long id) {
        return Result.success(userService.getDetail(id));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新当前用户资料（用户端）")
    public Result<Void> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        Long currentUserId = getCurrentUserId();
        userService.updateProfile(currentUserId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改当前用户密码（用户端）")
    public Result<Void> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto) {
        Long currentUserId = getCurrentUserId();
        userService.updatePassword(currentUserId, dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换用户状态（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        userService.toggleStatus(id);
        return Result.success();
    }
}