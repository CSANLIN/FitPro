package com.fitness.module.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.common.Result;
import com.fitness.common.exception.BusinessException;
import com.fitness.common.exception.GlobalExceptionHandler;
import com.fitness.module.course.dto.BookingCreateDTO;
import com.fitness.module.course.service.CourseBookingService;
import com.fitness.module.course.vo.MyBookingVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourseBookingControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CourseBookingService courseBookingService;

    @InjectMocks
    private CourseBookingController courseBookingController;

    @BeforeEach
    void setUp() {
        // 模拟已认证用户（userId=1, role=MEMBER），供 @PreAuthorize 和 getCurrentUserId() 使用
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))));

        mockMvc = MockMvcBuilders.standaloneSetup(courseBookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void book_ShouldReturn200_WhenSuccessful() throws Exception {
        doNothing().when(courseBookingService).book(anyLong(), anyLong());

        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setScheduleId(1L);

        mockMvc.perform(post("/api/course-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void book_ShouldReturnError_WhenScheduleFull() throws Exception {
        doThrow(new BusinessException(1001, "预约已满，请选择其他课程"))
                .when(courseBookingService).book(anyLong(), eq(1L));

        BookingCreateDTO dto = new BookingCreateDTO();
        dto.setScheduleId(1L);

        mockMvc.perform(post("/api/course-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    void cancel_ShouldReturn200_WhenSuccessful() throws Exception {
        doNothing().when(courseBookingService).cancel(anyLong(), eq(1L));

        mockMvc.perform(put("/api/course-bookings/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void myBookings_ShouldReturnList() throws Exception {
        MyBookingVO vo = new MyBookingVO();
        vo.setId(1L);
        vo.setCourseName("瑜伽基础");
        vo.setScheduleDate(LocalDate.now().plusDays(1));
        vo.setStartTime(LocalTime.of(10, 0));
        vo.setEndTime(LocalTime.of(11, 0));
        vo.setStatus("BOOKED");

        when(courseBookingService.listByUser(1L))
                .thenReturn(Collections.singletonList(vo));

        mockMvc.perform(get("/api/course-bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].courseName").value("瑜伽基础"))
                .andExpect(jsonPath("$.data[0].status").value("BOOKED"));
    }

    @Test
    void listAll_ShouldReturnList() throws Exception {
        when(courseBookingService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/course-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
