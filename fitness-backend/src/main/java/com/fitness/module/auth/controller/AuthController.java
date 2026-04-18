package com.fitness.module.auth.controller;

import com.fitness.common.Result;
import com.fitness.module.auth.dto.LoginDTO;
import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.service.AuthService;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<TokenVO> register(@RequestBody @Valid RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<TokenVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto.getUsername(), dto.getPassword()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新访问令牌")
    public Result<TokenVO> refresh(@RequestBody String refreshToken) {
        return Result.success(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestBody String refreshToken) {
        authService.logout(refreshToken);
        return Result.success();
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<UserInfoVO> getCurrentUserInfo() {
        return Result.success(authService.getCurrentUserInfo());
    }
}
