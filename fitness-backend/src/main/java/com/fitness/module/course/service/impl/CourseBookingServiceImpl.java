package com.fitness.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.course.service.CourseBookingService;
import com.fitness.module.course.vo.BookingVO;
import com.fitness.module.course.vo.MyBookingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseBookingServiceImpl extends ServiceImpl<CourseBookingMapper, CourseBookingEntity>
        implements CourseBookingService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseBookingMapper courseBookingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void book(Long userId, Long scheduleId) {
        // 校验排课存在且可预约
        CourseScheduleEntity schedule = courseScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "排课不存在");
        }
        if (!"UPCOMING".equals(schedule.getStatus())) {
            throw new BusinessException(1001, "该课程已不在可预约状态");
        }
        if (!schedule.getScheduleDate().isAfter(LocalDate.now())) {
            throw new BusinessException(1001, "该课程已开始或已结束");
        }

        // 校验不可重复预约
        long existingCount = this.count(new LambdaQueryWrapper<CourseBookingEntity>()
                .eq(CourseBookingEntity::getUserId, userId)
                .eq(CourseBookingEntity::getScheduleId, scheduleId)
                .eq(CourseBookingEntity::getStatus, "BOOKED"));
        if (existingCount > 0) {
            throw new BusinessException(1001, "您已预约过该课程");
        }

        // 乐观锁：原子更新当前预约人数（容量控制）
        int updatedRows = courseScheduleMapper.updateCurrentCount(scheduleId, schedule.getCurrentCount());

        if (updatedRows == 0) {
            throw new BusinessException(1001, "预约已满，请选择其他课程");
        }

        // 创建预约记录
        CourseBookingEntity booking = new CourseBookingEntity();
        booking.setUserId(userId);
        booking.setScheduleId(scheduleId);
        booking.setStatus("BOOKED");
        booking.setBookedAt(LocalDateTime.now());
        booking.setCreatedAt(LocalDateTime.now());
        this.save(booking);

        log.info("课程预约成功: userId={}, scheduleId={}, bookingId={}",
                userId, scheduleId, booking.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long bookingId) {
        CourseBookingEntity booking = this.getById(bookingId);
        if (booking == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        if (!booking.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能取消自己的预约");
        }
        if (!"BOOKED".equals(booking.getStatus())) {
            throw new BusinessException(1001, "该预约已取消或已完成");
        }

        // 更新预约状态
        booking.setStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());
        this.updateById(booking);

        // 减少排课当前人数
        CourseScheduleEntity schedule = courseScheduleMapper.selectById(booking.getScheduleId());
        if (schedule != null && schedule.getCurrentCount() > 0) {
            schedule.setCurrentCount(schedule.getCurrentCount() - 1);
            courseScheduleMapper.updateById(schedule);
        }

        log.info("课程预约取消: userId={}, bookingId={}", userId, bookingId);
    }

    @Override
    public List<MyBookingVO> listByUser(Long userId) {
        return courseBookingMapper.selectMyBookingVOByUserId(userId);
    }

    @Override
    public List<BookingVO> listBySchedule(Long scheduleId) {
        return courseBookingMapper.selectBookingVOByScheduleId(scheduleId);
    }

    @Override
    public List<BookingVO> listAll() {
        return courseBookingMapper.selectBookingVOAll();
    }
}
