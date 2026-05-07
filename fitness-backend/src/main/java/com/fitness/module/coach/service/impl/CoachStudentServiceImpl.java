package com.fitness.module.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.module.coach.service.CoachStudentService;
import com.fitness.module.coach.vo.StudentStatsVO;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.user.entity.UserEntity;
import com.fitness.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachStudentServiceImpl implements CoachStudentService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseBookingMapper courseBookingMapper;
    private final UserMapper userMapper;

    @Override
    public List<StudentStatsVO> listStudents(Long coachId) {
        List<Long> scheduleIds = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .select(CourseScheduleEntity::getId)
                        .eq(CourseScheduleEntity::getCoachId, coachId))
                .stream().map(CourseScheduleEntity::getId).collect(Collectors.toList());

        if (scheduleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<CourseBookingEntity> bookings = courseBookingMapper.selectList(
                new LambdaQueryWrapper<CourseBookingEntity>()
                        .in(CourseBookingEntity::getScheduleId, scheduleIds));

        Map<Long, List<CourseBookingEntity>> userBookingsMap = bookings.stream()
                .collect(Collectors.groupingBy(CourseBookingEntity::getUserId));

        List<StudentStatsVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<CourseBookingEntity>> entry : userBookingsMap.entrySet()) {
            Long userId = entry.getKey();
            List<CourseBookingEntity> userBookings = entry.getValue();
            long total = userBookings.size();
            long attended = userBookings.stream()
                    .filter(b -> "ATTENDED".equals(b.getStatus())).count();

            LocalDate lastDate = userBookings.stream()
                    .map(b -> {
                        CourseScheduleEntity s = courseScheduleMapper.selectById(b.getScheduleId());
                        return s != null ? s.getScheduleDate() : null;
                    })
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            double rate = total > 0
                    ? Math.round((double) attended / total * 10000.0) / 100.0
                    : 0.0;

            // 查询用户信息
            UserEntity user = userMapper.selectById(userId);

            StudentStatsVO vo = new StudentStatsVO();
            vo.setUserId(userId);
            vo.setUserName(user != null ? user.getNickname() : "未知用户");
            vo.setUserAvatar(user != null ? user.getAvatar() : null);
            vo.setTotalBookings(total);
            vo.setAttendedCount(attended);
            vo.setAttendanceRate(rate);
            vo.setLastBookingDate(lastDate);
            result.add(vo);
        }

        result.sort((a, b) -> Long.compare(b.getTotalBookings(), a.getTotalBookings()));
        return result;
    }

    @Override
    public StudentStatsVO getStudentDetail(Long coachId, Long userId) {
        return listStudents(coachId).stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
