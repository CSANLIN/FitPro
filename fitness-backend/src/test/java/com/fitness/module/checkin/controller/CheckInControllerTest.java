package com.fitness.module.checkin.controller;

import com.fitness.common.exception.BusinessException;
import com.fitness.common.exception.GlobalExceptionHandler;
import com.fitness.module.checkin.service.CheckInService;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.checkin.vo.CheckInVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CheckInService checkInService;

    @InjectMocks
    private CheckInController checkInController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))));

        mockMvc = MockMvcBuilders.standaloneSetup(checkInController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkIn_ShouldReturn200_WhenSuccessful() throws Exception {
        doNothing().when(checkInService).checkIn(anyLong());

        mockMvc.perform(post("/api/check-ins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void checkIn_ShouldReturnError_WhenAlreadyCheckedIn() throws Exception {
        doThrow(new BusinessException(1001, "今日已签到"))
                .when(checkInService).checkIn(anyLong());

        mockMvc.perform(post("/api/check-ins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("今日已签到"));
    }

    @Test
    void myStats_ShouldReturnStats() throws Exception {
        CheckInStatsVO stats = new CheckInStatsVO();
        stats.setMonthCount(10);
        stats.setStreakDays(3);

        when(checkInService.getStats(anyLong())).thenReturn(stats);

        mockMvc.perform(get("/api/check-ins/my/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.monthCount").value(10))
                .andExpect(jsonPath("$.data.streakDays").value(3));
    }

    @Test
    void myRecords_ShouldReturnList() throws Exception {
        CheckInVO vo = new CheckInVO();
        vo.setCheckInTime(LocalDateTime.now());

        when(checkInService.listByUser(anyLong()))
                .thenReturn(Collections.singletonList(vo));

        mockMvc.perform(get("/api/check-ins/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listAll_ShouldReturnList() throws Exception {
        when(checkInService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/check-ins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void listByUser_ShouldReturnList() throws Exception {
        when(checkInService.listByUser(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/check-ins/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
