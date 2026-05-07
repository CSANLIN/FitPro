package com.fitness.module.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.module.coach.service.CoachAnalysisService;
import com.fitness.module.coach.vo.AttendanceTrendVO;
import com.fitness.module.coach.vo.CoachSummaryVO;
import com.fitness.module.coach.vo.CourseRankVO;
import com.fitness.module.coach.vo.TimeDistributionVO;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachAnalysisServiceImpl implements CoachAnalysisService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseBookingMapper courseBookingMapper;

    @Override
    public CoachSummaryVO getSummary(Long coachId) {
        long totalSchedules = courseScheduleMapper.selectCount(new LambdaQueryWrapper<CourseScheduleEntity>()
                .eq(CourseScheduleEntity::getCoachId, coachId)
                .ne(CourseScheduleEntity::getStatus, "CANCELLED"));

        List<Long> scheduleIds = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .select(CourseScheduleEntity::getId)
                        .eq(CourseScheduleEntity::getCoachId, coachId))
                .stream().map(CourseScheduleEntity::getId).collect(Collectors.toList());

        long totalBookings = 0;
        long attendedCount = 0;
        Set<Long> studentIds = new HashSet<>();

        if (!scheduleIds.isEmpty()) {
            List<CourseBookingEntity> bookings = courseBookingMapper.selectList(
                    new LambdaQueryWrapper<CourseBookingEntity>()
                            .in(CourseBookingEntity::getScheduleId, scheduleIds));

            totalBookings = bookings.size();
            for (CourseBookingEntity b : bookings) {
                if ("ATTENDED".equals(b.getStatus())) {
                    attendedCount++;
                }
                studentIds.add(b.getUserId());
            }
        }

        double attendanceRate = totalBookings > 0
                ? Math.round((double) attendedCount / totalBookings * 10000.0) / 100.0
                : 0.0;

        CoachSummaryVO vo = new CoachSummaryVO();
        vo.setTotalSchedules(totalSchedules);
        vo.setTotalBookings(totalBookings);
        vo.setAttendanceRate(attendanceRate);
        vo.setTotalStudents(studentIds.size());
        return vo;
    }

    @Override
    public List<AttendanceTrendVO> getAttendanceTrend(Long coachId, String period) {
        List<CourseScheduleEntity> schedules = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .eq(CourseScheduleEntity::getCoachId, coachId)
                        .ne(CourseScheduleEntity::getStatus, "CANCELLED")
                        .orderByAsc(CourseScheduleEntity::getScheduleDate));

        if (schedules.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<CourseBookingEntity>> periodBookingsMap = new LinkedHashMap<>();

        for (CourseScheduleEntity schedule : schedules) {
            List<CourseBookingEntity> bookings = courseBookingMapper.selectList(
                    new LambdaQueryWrapper<CourseBookingEntity>()
                            .eq(CourseBookingEntity::getScheduleId, schedule.getId()));

            String key;
            if ("month".equals(period)) {
                key = schedule.getScheduleDate().toString().substring(0, 7);
            } else {
                key = String.format("%04d-W%02d",
                        schedule.getScheduleDate().getYear(),
                        (schedule.getScheduleDate().getDayOfYear() - 1) / 7 + 1);
            }

            periodBookingsMap.computeIfAbsent(key, k -> new ArrayList<>()).addAll(bookings);
        }

        return periodBookingsMap.entrySet().stream().map(entry -> {
            List<CourseBookingEntity> bookings = entry.getValue();
            long total = bookings.size();
            long attended = bookings.stream()
                    .filter(b -> "ATTENDED".equals(b.getStatus())).count();
            double rate = total > 0
                    ? Math.round((double) attended / total * 10000.0) / 100.0
                    : 0.0;
            return new AttendanceTrendVO(entry.getKey(), total, attended, rate);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseRankVO> getCourseRank(Long coachId) {
        return courseScheduleMapper.selectCourseRankByCoach(coachId);
    }

    @Override
    public List<TimeDistributionVO> getTimeDistribution(Long coachId) {
        List<CourseScheduleEntity> schedules = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .eq(CourseScheduleEntity::getCoachId, coachId)
                        .ne(CourseScheduleEntity::getStatus, "CANCELLED"));

        long morningCount = 0, afternoonCount = 0, eveningCount = 0;
        long morningBooking = 0, afternoonBooking = 0, eveningBooking = 0;

        for (CourseScheduleEntity schedule : schedules) {
            int hour = schedule.getStartTime().getHour();
            long bookingCount = courseBookingMapper.selectCount(
                    new LambdaQueryWrapper<CourseBookingEntity>()
                            .eq(CourseBookingEntity::getScheduleId, schedule.getId()));

            if (hour < 12) {
                morningCount++;
                morningBooking += bookingCount;
            } else if (hour < 18) {
                afternoonCount++;
                afternoonBooking += bookingCount;
            } else {
                eveningCount++;
                eveningBooking += bookingCount;
            }
        }

        return Arrays.asList(
                new TimeDistributionVO("上午(06-12)", morningCount, morningBooking),
                new TimeDistributionVO("下午(12-18)", afternoonCount, afternoonBooking),
                new TimeDistributionVO("晚上(18-22)", eveningCount, eveningBooking));
    }
}
