package com.fitness.module.course.service.impl;

import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseBookingServiceImplTest {

    @Mock
    private CourseScheduleMapper courseScheduleMapper;

    @Mock
    private CourseBookingMapper courseBookingMapper;

    @InjectMocks
    private CourseBookingServiceImpl courseBookingService;

    @Captor
    private ArgumentCaptor<CourseBookingEntity> bookingCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courseBookingService, "baseMapper", courseBookingMapper);
    }

    // ========== 预约测试 ==========

    @Test
    void book_ShouldSucceed_WhenScheduleAvailable() {
        CourseScheduleEntity schedule = createUpcomingSchedule();
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);
        when(courseBookingMapper.selectCount(any())).thenReturn(0L);
        when(courseScheduleMapper.updateCurrentCount(1L, 5)).thenReturn(1);

        courseBookingService.book(100L, 1L);

        verify(courseBookingMapper).insert(any(CourseBookingEntity.class));
    }

    @Test
    void book_ShouldThrow_WhenScheduleNotFound() {
        when(courseScheduleMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.book(100L, 999L));
        assertEquals(404, ex.getCode());
        assertEquals("排课不存在", ex.getMessage());
    }

    @Test
    void book_ShouldThrow_WhenScheduleNotUpcoming() {
        CourseScheduleEntity schedule = createUpcomingSchedule();
        schedule.setStatus("FINISHED");
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.book(100L, 1L));
        assertEquals(1001, ex.getCode());
        assertEquals("该课程已不在可预约状态", ex.getMessage());
    }

    @Test
    void book_ShouldThrow_WhenScheduleIsPast() {
        CourseScheduleEntity schedule = createUpcomingSchedule();
        schedule.setScheduleDate(LocalDate.now().minusDays(1));
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.book(100L, 1L));
        assertEquals(1001, ex.getCode());
        assertEquals("该课程已开始或已结束", ex.getMessage());
    }

    @Test
    void book_ShouldThrow_WhenAlreadyBooked() {
        CourseScheduleEntity schedule = createUpcomingSchedule();
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);
        when(courseBookingMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.book(100L, 1L));
        assertEquals(1001, ex.getCode());
        assertEquals("您已预约过该课程", ex.getMessage());
    }

    @Test
    void book_ShouldThrow_WhenCapacityFull() {
        CourseScheduleEntity schedule = createUpcomingSchedule();
        schedule.setCurrentCount(10);
        schedule.setMaxCapacity(10);
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);
        when(courseBookingMapper.selectCount(any())).thenReturn(0L);
        when(courseScheduleMapper.updateCurrentCount(1L, 10)).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.book(100L, 1L));
        assertEquals(1001, ex.getCode());
        assertEquals("预约已满，请选择其他课程", ex.getMessage());
    }

    // ========== 取消预约测试 ==========

    @Test
    void cancel_ShouldSucceed_WhenBookingExists() {
        CourseBookingEntity booking = createBooking(1L, 100L, "BOOKED");
        CourseScheduleEntity schedule = createUpcomingSchedule();

        when(courseBookingMapper.selectById(1L)).thenReturn(booking);
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        courseBookingService.cancel(100L, 1L);

        verify(courseBookingMapper).updateById(bookingCaptor.capture());
        assertEquals("CANCELLED", bookingCaptor.getValue().getStatus());
        verify(courseScheduleMapper).updateById(any(CourseScheduleEntity.class));
    }

    @Test
    void cancel_ShouldThrow_WhenBookingNotFound() {
        when(courseBookingMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.cancel(100L, 999L));
        assertEquals(404, ex.getCode());
        assertEquals("预约记录不存在", ex.getMessage());
    }

    @Test
    void cancel_ShouldThrow_WhenNotOwnBooking() {
        CourseBookingEntity booking = createBooking(1L, 999L, "BOOKED");
        when(courseBookingMapper.selectById(1L)).thenReturn(booking);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.cancel(100L, 1L));
        assertEquals(403, ex.getCode());
        assertEquals("只能取消自己的预约", ex.getMessage());
    }

    @Test
    void cancel_ShouldThrow_WhenAlreadyCancelled() {
        CourseBookingEntity booking = createBooking(1L, 100L, "CANCELLED");
        when(courseBookingMapper.selectById(1L)).thenReturn(booking);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseBookingService.cancel(100L, 1L));
        assertEquals(1001, ex.getCode());
        assertEquals("该预约已取消或已完成", ex.getMessage());
    }

    // ========== 工具方法 ==========

    private CourseScheduleEntity createUpcomingSchedule() {
        CourseScheduleEntity entity = new CourseScheduleEntity();
        entity.setId(1L);
        entity.setCourseId(10L);
        entity.setCoachId(20L);
        entity.setScheduleDate(LocalDate.now().plusDays(1));
        entity.setStartTime(LocalTime.of(10, 0));
        entity.setEndTime(LocalTime.of(11, 0));
        entity.setCurrentCount(5);
        entity.setMaxCapacity(20);
        entity.setStatus("UPCOMING");
        return entity;
    }

    private CourseBookingEntity createBooking(Long id, Long userId, String status) {
        CourseBookingEntity entity = new CourseBookingEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setScheduleId(1L);
        entity.setStatus(status);
        entity.setBookedAt(LocalDateTime.now());
        return entity;
    }
}
