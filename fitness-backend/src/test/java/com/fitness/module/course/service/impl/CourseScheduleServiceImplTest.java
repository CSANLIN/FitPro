package com.fitness.module.course.service.impl;

import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.dto.ScheduleCreateDTO;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseMapper;
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
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseScheduleServiceImplTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseScheduleMapper courseScheduleMapper;

    @InjectMocks
    private CourseScheduleServiceImpl courseScheduleService;

    @Captor
    private ArgumentCaptor<CourseScheduleEntity> scheduleCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courseScheduleService, "baseMapper", courseScheduleMapper);
    }

    // ========== 创建排课测试 ==========

    @Test
    void create_ShouldSucceed_WhenDataValid() {
        CourseEntity course = createNormalCourse();
        when(courseMapper.selectById(10L)).thenReturn(course);
        when(courseScheduleMapper.selectCount(any())).thenReturn(0L);

        ScheduleCreateDTO dto = createValidScheduleDTO();
        CourseScheduleEntity result = courseScheduleService.create(dto);

        assertNotNull(result);
        assertEquals("UPCOMING", result.getStatus());
        assertEquals(0, result.getCurrentCount().intValue());
        assertEquals(course.getMaxCapacity(), result.getMaxCapacity());
        verify(courseScheduleMapper).insert(any(CourseScheduleEntity.class));
    }

    @Test
    void create_ShouldThrow_WhenCourseNotFound() {
        when(courseMapper.selectById(999L)).thenReturn(null);

        ScheduleCreateDTO dto = createValidScheduleDTO();
        dto.setCourseId(999L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.create(dto));
        assertEquals(404, ex.getCode());
        assertEquals("课程不存在", ex.getMessage());
    }

    @Test
    void create_ShouldThrow_WhenCourseOffline() {
        CourseEntity course = createNormalCourse();
        course.setStatus(0);
        when(courseMapper.selectById(10L)).thenReturn(course);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.create(createValidScheduleDTO()));
        assertEquals(1001, ex.getCode());
        assertEquals("该课程已下架，无法排课", ex.getMessage());
    }

    @Test
    void create_ShouldThrow_WhenTimeConflict() {
        CourseEntity course = createNormalCourse();
        when(courseMapper.selectById(10L)).thenReturn(course);
        when(courseScheduleMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.create(createValidScheduleDTO()));
        assertEquals(1001, ex.getCode());
        assertEquals("该教练在此时间段已有排课", ex.getMessage());
    }

    // ========== 取消排课测试 ==========

    @Test
    void cancel_ShouldSucceed_WhenScheduleUpcoming() {
        CourseScheduleEntity schedule = new CourseScheduleEntity();
        schedule.setId(1L);
        schedule.setStatus("UPCOMING");
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        courseScheduleService.cancel(1L);

        verify(courseScheduleMapper).updateById(scheduleCaptor.capture());
        assertEquals("CANCELLED", scheduleCaptor.getValue().getStatus());
    }

    @Test
    void cancel_ShouldThrow_WhenScheduleNotFound() {
        when(courseScheduleMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.cancel(999L));
        assertEquals(404, ex.getCode());
        assertEquals("排课不存在", ex.getMessage());
    }

    @Test
    void cancel_ShouldThrow_WhenNotUpcoming() {
        CourseScheduleEntity schedule = new CourseScheduleEntity();
        schedule.setId(1L);
        schedule.setStatus("FINISHED");
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.cancel(1L));
        assertEquals(1001, ex.getCode());
        assertEquals("只能取消待开始的排课", ex.getMessage());
    }

    @Test
    void cancel_ShouldThrow_WhenAlreadyCancelled() {
        CourseScheduleEntity schedule = new CourseScheduleEntity();
        schedule.setId(1L);
        schedule.setStatus("CANCELLED");
        when(courseScheduleMapper.selectById(1L)).thenReturn(schedule);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> courseScheduleService.cancel(1L));
        assertEquals(1001, ex.getCode());
    }

    // ========== 工具方法 ==========

    private CourseEntity createNormalCourse() {
        CourseEntity course = new CourseEntity();
        course.setId(10L);
        course.setName("瑜伽基础");
        course.setStatus(1);
        course.setMaxCapacity(20);
        course.setDurationMinutes(60);
        return course;
    }

    private ScheduleCreateDTO createValidScheduleDTO() {
        ScheduleCreateDTO dto = new ScheduleCreateDTO();
        dto.setCourseId(10L);
        dto.setCoachId(20L);
        dto.setScheduleDate(LocalDate.now().plusDays(3));
        dto.setStartTime(LocalTime.of(10, 0));
        dto.setEndTime(LocalTime.of(11, 0));
        dto.setLocation("B馆 瑜伽室");
        return dto;
    }
}
