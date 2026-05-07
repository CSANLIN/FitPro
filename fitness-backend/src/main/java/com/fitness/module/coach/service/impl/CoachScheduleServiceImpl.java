package com.fitness.module.coach.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.coach.service.CoachScheduleService;
import com.fitness.module.coach.vo.CoachBookingVO;
import com.fitness.module.coach.vo.CoachScheduleVO;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoachScheduleServiceImpl implements CoachScheduleService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseMapper courseMapper;
    private final CourseBookingMapper courseBookingMapper;

    @Override
    public List<CoachScheduleVO> listMySchedules(Long coachId, LocalDate startDate, LocalDate endDate) {
        return courseScheduleMapper.selectCoachScheduleVO(coachId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoachScheduleVO createSchedule(Long coachId, Long courseId, LocalDate scheduleDate,
                                          LocalTime startTime, LocalTime endTime, String location) {
        // 校验课程存在且已上架
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        if (course.getStatus() != 1) {
            throw new BusinessException(1001, "该课程已下架，无法排课");
        }

        // 校验同一教练同一时间段没有其他排课
        long conflictCount = courseScheduleMapper.selectCount(new LambdaQueryWrapper<CourseScheduleEntity>()
                .eq(CourseScheduleEntity::getCoachId, coachId)
                .eq(CourseScheduleEntity::getScheduleDate, scheduleDate)
                .eq(CourseScheduleEntity::getStatus, "UPCOMING")
                .and(w -> w
                        .lt(CourseScheduleEntity::getStartTime, endTime)
                        .gt(CourseScheduleEntity::getEndTime, startTime)));
        if (conflictCount > 0) {
            throw new BusinessException(1001, "您在此时间段已有排课");
        }

        // 创建排课
        CourseScheduleEntity entity = new CourseScheduleEntity();
        entity.setCourseId(courseId);
        entity.setCoachId(coachId);
        entity.setScheduleDate(scheduleDate);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setLocation(location);
        entity.setCurrentCount(0);
        entity.setMaxCapacity(course.getMaxCapacity());
        entity.setStatus("UPCOMING");
        entity.setCreatedAt(LocalDateTime.now());
        courseScheduleMapper.insert(entity);

        log.info("教练排课创建成功: id={}, coachId={}, courseId={}, date={}",
                entity.getId(), coachId, courseId, scheduleDate);

        // 返回新创建的排课
        List<CoachScheduleVO> list = courseScheduleMapper.selectCoachScheduleVO(coachId, scheduleDate, scheduleDate);
        return list.stream().filter(s -> s.getId().equals(entity.getId())).findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSchedule(Long coachId, Long scheduleId) {
        CourseScheduleEntity entity = courseScheduleMapper.selectById(scheduleId);
        if (entity == null) {
            throw new BusinessException(404, "排课不存在");
        }
        if (!entity.getCoachId().equals(coachId)) {
            throw new BusinessException(403, "只能取消自己的排课");
        }
        if (!"UPCOMING".equals(entity.getStatus())) {
            throw new BusinessException(1001, "只能取消待开始的排课");
        }

        entity.setStatus("CANCELLED");
        courseScheduleMapper.updateById(entity);
        log.info("教练排课已取消: id={}, coachId={}", scheduleId, coachId);
    }

    @Override
    public List<CoachBookingVO> listBookings(Long coachId, Long scheduleId) {
        // 校验排课属于当前教练
        CourseScheduleEntity schedule = courseScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "排课不存在");
        }
        if (!schedule.getCoachId().equals(coachId)) {
            throw new BusinessException(403, "只能查看自己排课的预约");
        }
        return courseBookingMapper.selectCoachBookingVOByScheduleId(scheduleId);
    }
}
