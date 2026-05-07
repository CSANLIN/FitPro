package com.fitness.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.course.service.CourseBookingService;
import com.fitness.module.course.vo.BookingVO;
import com.fitness.module.course.vo.MyBookingVO;
import com.fitness.module.payment.entity.PaymentOrderEntity;
import com.fitness.module.payment.mapper.PaymentOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseBookingServiceImpl extends ServiceImpl<CourseBookingMapper, CourseBookingEntity>
        implements CourseBookingService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseBookingMapper courseBookingMapper;
    private final CourseMapper courseMapper;
    private final PaymentOrderMapper paymentOrderMapper;

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
        // 过去日期的课程不可预约
        if (schedule.getScheduleDate().isBefore(LocalDate.now())) {
            throw new BusinessException(1001, "该课程已结束");
        }
        // 今天的课程，若已过开始时间则不可预约
        if (schedule.getScheduleDate().isEqual(LocalDate.now())
                && schedule.getStartTime() != null
                && LocalTime.now().isAfter(schedule.getStartTime())) {
            throw new BusinessException(1001, "该课程已开始");
        }

        // 校验不可重复预约
        CourseBookingEntity existingBooking = this.getOne(new LambdaQueryWrapper<CourseBookingEntity>()
                .eq(CourseBookingEntity::getUserId, userId)
                .eq(CourseBookingEntity::getScheduleId, scheduleId));
        if (existingBooking != null) {
            if ("BOOKED".equals(existingBooking.getStatus())) {
                throw new BusinessException(1001, "您已预约过该课程");
            }
            // 取消后重新预约：复用已有记录，更新状态和时间为新预约
            existingBooking.setStatus("BOOKED");
            existingBooking.setBookedAt(LocalDateTime.now());
            existingBooking.setCancelledAt(null);
            this.updateById(existingBooking);
            return;
        }

        // 校验付费课程已支付
        CourseEntity course = courseMapper.selectById(schedule.getCourseId());
        if (course != null && course.getPrice() != null
                && course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            long paidCount = paymentOrderMapper.selectCount(new LambdaQueryWrapper<PaymentOrderEntity>()
                    .eq(PaymentOrderEntity::getUserId, userId)
                    .eq(PaymentOrderEntity::getScheduleId, scheduleId)
                    .eq(PaymentOrderEntity::getStatus, "SUCCESS"));
            if (paidCount == 0) {
                throw new BusinessException(1001, "该课程需先支付才能预约");
            }
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

    @Override
    public List<BookingVO> listByCoach(Long coachId) {
        return courseBookingMapper.selectBookingVOByCoachId(coachId);
    }
}
