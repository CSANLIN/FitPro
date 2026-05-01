package com.fitness.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.common.exception.BusinessException;
import com.fitness.common.exception.GlobalExceptionHandler;
import com.fitness.module.auth.controller.AuthController;
import com.fitness.module.auth.dto.LoginDTO;
import com.fitness.module.auth.dto.RegisterDTO;
import com.fitness.module.auth.service.AuthService;
import com.fitness.module.auth.vo.TokenVO;
import com.fitness.module.auth.vo.UserInfoVO;
import com.fitness.module.checkin.controller.CheckInController;
import com.fitness.module.checkin.service.CheckInService;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.course.controller.CourseBookingController;
import com.fitness.module.course.dto.BookingCreateDTO;
import com.fitness.module.course.service.CourseBookingService;
import com.fitness.module.course.vo.MyBookingVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 端到端业务流程测试
 *
 * 验证完整业务链路：注册 → 登录 → 查看个人信息 → 预约课程 → 取消预约 → 签到 → 签到统计
 * 使用 Standalone MockMvc + Mock 服务层，验证 Controller 层 HTTP 映射、请求绑定、响应格式和异常处理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("端到端全业务流程测试")
class FullBusinessFlowE2ETest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @Mock
    private CourseBookingService courseBookingService;

    @Mock
    private CheckInService checkInService;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private CourseBookingController courseBookingController;

    @InjectMocks
    private CheckInController checkInController;

    @BeforeEach
    void setUp() {
        // 设置安全上下文，模拟已认证的 MEMBER 用户（ID=1）
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))));

        mockMvc = MockMvcBuilders.standaloneSetup(
                        authController,
                        courseBookingController,
                        checkInController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("完整业务流程：注册→登录→预约→签到→异常处理")
    void testFullBusinessFlow() throws Exception {
        // ===== Stage 1: 用户注册 =====
        TokenVO registerToken = new TokenVO();
        registerToken.setAccessToken("register_access_token");
        registerToken.setRefreshToken("register_refresh_token");
        registerToken.setTokenType("Bearer");
        registerToken.setExpiresIn(7200);

        when(authService.register(any(RegisterDTO.class))).thenReturn(registerToken);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("e2e_test_user");
        registerDTO.setPassword("password123");
        registerDTO.setConfirmPassword("password123");
        registerDTO.setNickname("E2E用户");
        registerDTO.setPhone("13900139000");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("register_access_token"));

        // ===== Stage 2: 用户登录 =====
        TokenVO loginToken = new TokenVO();
        loginToken.setAccessToken("login_access_token");
        loginToken.setRefreshToken("login_refresh_token");
        loginToken.setTokenType("Bearer");
        loginToken.setExpiresIn(7200);

        when(authService.login(eq("e2e_test_user"), eq("password123"))).thenReturn(loginToken);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("e2e_test_user");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("login_access_token"));

        // ===== Stage 3: 获取当前用户信息 =====
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(1L);
        userInfo.setUsername("e2e_test_user");
        userInfo.setNickname("E2E用户");
        userInfo.setRole("MEMBER");

        when(authService.getCurrentUserInfo()).thenReturn(userInfo);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("e2e_test_user"))
                .andExpect(jsonPath("$.data.role").value("MEMBER"));

        // ===== Stage 4: 预约课程 =====
        doNothing().when(courseBookingService).book(anyLong(), eq(1L));

        BookingCreateDTO bookingDTO = new BookingCreateDTO();
        bookingDTO.setScheduleId(1L);

        mockMvc.perform(post("/api/course-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // ===== Stage 5: 查看我的预约 =====
        MyBookingVO myBooking = new MyBookingVO();
        myBooking.setId(1L);
        myBooking.setCourseName("瑜伽基础");
        myBooking.setScheduleDate(LocalDate.now().plusDays(1));
        myBooking.setStartTime(LocalTime.of(10, 0));
        myBooking.setEndTime(LocalTime.of(11, 0));
        myBooking.setStatus("BOOKED");

        when(courseBookingService.listByUser(anyLong()))
                .thenReturn(Collections.singletonList(myBooking));

        mockMvc.perform(get("/api/course-bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].courseName").value("瑜伽基础"))
                .andExpect(jsonPath("$.data[0].status").value("BOOKED"));

        // ===== Stage 6: 取消预约 =====
        doNothing().when(courseBookingService).cancel(anyLong(), eq(1L));

        mockMvc.perform(put("/api/course-bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // ===== Stage 7: 签到 =====
        doNothing().when(checkInService).checkIn(anyLong());

        mockMvc.perform(post("/api/check-ins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // ===== Stage 8: 签到统计 =====
        CheckInStatsVO stats = new CheckInStatsVO();
        stats.setMonthCount(1);
        stats.setStreakDays(1);

        when(checkInService.getStats(anyLong())).thenReturn(stats);

        mockMvc.perform(get("/api/check-ins/my/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.monthCount").value(1));

        // ===== Stage 9: 业务异常处理验证 =====
        doThrow(new BusinessException(1001, "预约已满，请选择其他课程"))
                .when(courseBookingService).book(anyLong(), eq(999L));

        BookingCreateDTO fullDTO = new BookingCreateDTO();
        fullDTO.setScheduleId(999L);

        mockMvc.perform(post("/api/course-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fullDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("预约已满，请选择其他课程"));

        // 验证所有核心服务调用
        verify(authService).register(any(RegisterDTO.class));
        verify(authService).login(anyString(), anyString());
        verify(authService).getCurrentUserInfo();
        verify(courseBookingService).book(anyLong(), eq(1L));
        verify(courseBookingService).listByUser(anyLong());
        verify(courseBookingService).cancel(anyLong(), eq(1L));
        verify(checkInService).checkIn(anyLong());
        verify(checkInService).getStats(anyLong());
        verify(courseBookingService, times(2)).book(anyLong(), anyLong());
    }
}
