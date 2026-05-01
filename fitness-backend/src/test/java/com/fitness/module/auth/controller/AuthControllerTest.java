package com.fitness.module.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.common.Result;
import com.fitness.common.exception.BusinessException;
import com.fitness.common.exception.GlobalExceptionHandler;
import com.fitness.module.auth.dto.LoginDTO;
import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.service.AuthService;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ========== 登录测试 ==========

    @Test
    void login_ShouldReturn200_WhenCredentialsValid() throws Exception {
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken("access_token");
        tokenVO.setRefreshToken("refresh_token");
        tokenVO.setTokenType("Bearer");
        tokenVO.setExpiresIn(7200);

        when(authService.login(eq("testuser"), eq("password123")))
                .thenReturn(tokenVO);

        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh_token"));
    }

    @Test
    void login_ShouldReturn400_WhenUsernameBlank() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void login_ShouldReturnError_WhenCredentialsInvalid() throws Exception {
        when(authService.login(eq("wrong"), eq("wrong")))
                .thenThrow(new BusinessException(1004, "用户名或密码错误"));

        LoginDTO dto = new LoginDTO();
        dto.setUsername("wrong");
        dto.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1004))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    // ========== 注册测试 ==========

    @Test
    void register_ShouldReturn200_WhenDataValid() throws Exception {
        TokenVO tokenVO = new TokenVO();
        tokenVO.setAccessToken("new_access_token");

        when(authService.register(any(RegisterDTO.class))).thenReturn(tokenVO);

        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setNickname("NewUser");
        dto.setPhone("13800138000");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("new_access_token"));
    }

    @Test
    void register_ShouldReturn400_WhenValidationFails() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("");
        dto.setPassword("123");
        dto.setConfirmPassword("456");
        dto.setNickname("");
        dto.setPhone("invalid_phone");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ========== 获取当前用户信息测试 ==========

    @Test
    void getMe_ShouldReturnUserInfo() throws Exception {
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(1L);
        userInfo.setUsername("testuser");
        userInfo.setNickname("TestUser");
        userInfo.setRole("MEMBER");

        when(authService.getCurrentUserInfo()).thenReturn(userInfo);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("MEMBER"));
    }
}
