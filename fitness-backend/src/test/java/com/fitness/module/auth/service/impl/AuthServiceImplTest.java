package com.fitness.module.auth.service.impl;

import com.fitness.common.exception.BusinessException;
import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;
import com.fitness.module.user.entity.UserEntity;
import com.fitness.module.user.mapper.UserMapper;
import com.fitness.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    // ========== 注册测试 ==========

    @Test
    void register_ShouldSucceed_WhenAllValid() {
        RegisterDTO dto = createValidRegisterDTO();
        when(userMapper.selectCount(any())).thenReturn(0L, 0L);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_pwd");

        // 模拟 insert 后设置用户 ID
        doAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(UserEntity.class));

        when(jwtTokenProvider.generateAccessToken(100L, "MEMBER")).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(100L)).thenReturn("refresh_token");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(7200L);
        when(jwtTokenProvider.getRefreshTokenExpire()).thenReturn(604800L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        TokenVO result = authService.register(dto);

        assertNotNull(result);
        assertEquals("access_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(7200, result.getExpiresIn());

        verify(userMapper).insert(userCaptor.capture());
        assertEquals("MEMBER", userCaptor.getValue().getRole());
    }

    @Test
    void register_ShouldThrow_WhenUsernameExists() {
        RegisterDTO dto = createValidRegisterDTO();
        when(userMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(dto));
        assertEquals(1001, ex.getCode());
        assertEquals("用户名已存在", ex.getMessage());
    }

    @Test
    void register_ShouldThrow_WhenPhoneExists() {
        RegisterDTO dto = createValidRegisterDTO();
        when(userMapper.selectCount(any())).thenReturn(0L, 1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(dto));
        assertEquals(1002, ex.getCode());
        assertEquals("手机号已存在", ex.getMessage());
    }

    @Test
    void register_ShouldThrow_WhenPasswordMismatch() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("different");
        dto.setNickname("New");
        dto.setPhone("13800138111");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(dto));
        assertEquals(1003, ex.getCode());
        assertEquals("两次输入的密码不一致", ex.getMessage());
    }

    @Test
    void register_ShouldDefaultToMEMBER_WhenRoleIsInvalid() {
        RegisterDTO dto = createValidRegisterDTO();
        dto.setRole("INVALID_ROLE");
        when(userMapper.selectCount(any())).thenReturn(0L, 0L);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_pwd");

        doAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(100L);
            return 1;
        }).when(userMapper).insert(any(UserEntity.class));

        when(jwtTokenProvider.generateAccessToken(100L, "MEMBER")).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(100L)).thenReturn("refresh_token");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(7200L);
        when(jwtTokenProvider.getRefreshTokenExpire()).thenReturn(604800L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        TokenVO result = authService.register(dto);

        assertNotNull(result);
        verify(userMapper).insert(userCaptor.capture());
        assertEquals("MEMBER", userCaptor.getValue().getRole());
    }

    // ========== 登录测试 ==========

    @Test
    void login_ShouldSucceed_WhenCredentialsValid() {
        UserEntity user = createNormalUser();
        when(userMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("password123"), eq("encoded_pwd"))).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(1L, "MEMBER")).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(1L)).thenReturn("refresh_token");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(7200L);
        when(jwtTokenProvider.getRefreshTokenExpire()).thenReturn(604800L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        TokenVO result = authService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("access_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login("nonexistent", "password123"));
        assertEquals(1004, ex.getCode());
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void login_ShouldThrow_WhenPasswordWrong() {
        UserEntity user = createNormalUser();
        when(userMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("wrong"), eq("encoded_pwd"))).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login("testuser", "wrong"));
        assertEquals(1004, ex.getCode());
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void login_ShouldThrow_WhenUserDisabled() {
        UserEntity user = createNormalUser();
        user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login("testuser", "password123"));
        assertEquals(1005, ex.getCode());
        assertEquals("用户账户已被禁用，请联系管理员", ex.getMessage());
    }

    // ========== 刷新Token测试 ==========

    @Test
    void refresh_ShouldSucceed_WhenTokenValid() {
        when(jwtTokenProvider.validateToken("valid_refresh_token", "refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn("valid_refresh_token");

        UserEntity user = createNormalUser();
        when(userMapper.selectById(1L)).thenReturn(user);
        when(jwtTokenProvider.generateAccessToken(1L, "MEMBER")).thenReturn("new_access_token");
        when(jwtTokenProvider.getAccessTokenExpire()).thenReturn(7200L);
        when(jwtTokenProvider.getRefreshTokenExpire()).thenReturn(604800L);

        TokenVO result = authService.refresh("valid_refresh_token");

        assertNotNull(result);
        assertEquals("new_access_token", result.getAccessToken());
        assertEquals("valid_refresh_token", result.getRefreshToken());
    }

    @Test
    void refresh_ShouldThrow_WhenTokenInvalid() {
        when(jwtTokenProvider.validateToken("invalid", "refresh")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.refresh("invalid"));
        assertEquals(1006, ex.getCode());
        assertEquals("无效的刷新令牌", ex.getMessage());
    }

    @Test
    void refresh_ShouldThrow_WhenTokenNotInRedis() {
        when(jwtTokenProvider.validateToken("valid_refresh_token", "refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.refresh("valid_refresh_token"));
        assertEquals(1007, ex.getCode());
        assertEquals("刷新令牌已失效，请重新登录", ex.getMessage());
    }

    @Test
    void refresh_ShouldThrow_WhenTokenMismatch() {
        when(jwtTokenProvider.validateToken("valid_refresh_token", "refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn("different_token");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.refresh("valid_refresh_token"));
        assertEquals(1007, ex.getCode());
    }

    @Test
    void refresh_ShouldThrow_WhenUserDisabled() {
        when(jwtTokenProvider.validateToken("valid_refresh_token", "refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:1")).thenReturn("valid_refresh_token");

        UserEntity user = createNormalUser();
        user.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.refresh("valid_refresh_token"));
        assertEquals(1005, ex.getCode());
    }

    // ========== 登出测试 ==========

    @Test
    void logout_ShouldSucceed_WhenTokenValid() {
        when(jwtTokenProvider.validateToken("valid_refresh_token", "refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn(1L);
        when(redisTemplate.delete("refresh_token:1")).thenReturn(true);

        authService.logout("valid_refresh_token");

        verify(redisTemplate).delete("refresh_token:1");
    }

    @Test
    void logout_ShouldThrow_WhenTokenInvalid() {
        when(jwtTokenProvider.validateToken("invalid", "refresh")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.logout("invalid"));
        assertEquals(1006, ex.getCode());
    }

    // ========== 获取当前用户信息测试 ==========

    @Test
    void getCurrentUserInfo_ShouldSucceed() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn("1");

            UserEntity user = createNormalUser();
            user.setId(1L);
            when(userMapper.selectById(1L)).thenReturn(user);

            UserInfoVO result = authService.getCurrentUserInfo();

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("testuser", result.getUsername());
            assertEquals("TestUser", result.getNickname());
            assertEquals("MEMBER", result.getRole());
        }
    }

    @Test
    void getCurrentUserInfo_ShouldThrow_WhenPrincipalInvalid() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn("not_a_number");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.getCurrentUserInfo());
            assertEquals(1009, ex.getCode());
        }
    }



    // ========== 工具方法 ==========

    private RegisterDTO createValidRegisterDTO() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
        dto.setNickname("NewUser");
        dto.setPhone("13800138000");
        dto.setRole("MEMBER");
        return dto;
    }

    private UserEntity createNormalUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded_pwd");
        user.setNickname("TestUser");
        user.setRole("MEMBER");
        user.setStatus(0);
        return user;
    }
}
