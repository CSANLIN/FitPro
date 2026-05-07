package com.fitness.module.coach.service;

import com.fitness.module.coach.vo.CoachBookingVO;
import com.fitness.module.coach.vo.CoachScheduleVO;

import java.time.LocalDate;
import java.util.List;

public interface CoachScheduleService {

    List<CoachScheduleVO> listMySchedules(Long coachId, LocalDate startDate, LocalDate endDate);

    CoachScheduleVO createSchedule(Long coachId, Long courseId, LocalDate scheduleDate,
                                   java.time.LocalTime startTime, java.time.LocalTime endTime, String location);

    void cancelSchedule(Long coachId, Long scheduleId);

    List<CoachBookingVO> listBookings(Long coachId, Long scheduleId);
}
