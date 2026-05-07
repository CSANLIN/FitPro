package com.fitness.module.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.vo.BookingVO;
import com.fitness.module.course.vo.MyBookingVO;

import java.util.List;

public interface CourseBookingService extends IService<CourseBookingEntity> {

    void book(Long userId, Long scheduleId);

    void cancel(Long userId, Long bookingId);

    List<MyBookingVO> listByUser(Long userId);

    List<BookingVO> listBySchedule(Long scheduleId);

    List<BookingVO> listAll();

    List<BookingVO> listByCoach(Long coachId);
}
