package com.fitness.module.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.payment.entity.PaymentOrderEntity;
import com.fitness.module.payment.mapper.PaymentOrderMapper;
import com.fitness.module.payment.service.PaymentService;
import com.fitness.module.payment.vo.PaymentResultVO;
import com.fitness.module.payment.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrderEntity>
        implements PaymentService {

    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createOrder(Long userId, Long scheduleId) {
        // 校验排课存在
        CourseScheduleEntity schedule = courseScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "排课不存在");
        }

        // 获取课程价格
        CourseEntity course = courseMapper.selectById(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        BigDecimal amount = course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(1001, "免费课程无需支付");
        }

        // 校验是否有未支付的订单
        long pendingCount = this.count(new LambdaQueryWrapper<PaymentOrderEntity>()
                .eq(PaymentOrderEntity::getUserId, userId)
                .eq(PaymentOrderEntity::getScheduleId, scheduleId)
                .eq(PaymentOrderEntity::getStatus, "PENDING"));
        if (pendingCount > 0) {
            throw new BusinessException(1001, "您已有该课程的未支付订单");
        }

        // 校验是否已支付过
        long paidCount = this.count(new LambdaQueryWrapper<PaymentOrderEntity>()
                .eq(PaymentOrderEntity::getUserId, userId)
                .eq(PaymentOrderEntity::getScheduleId, scheduleId)
                .eq(PaymentOrderEntity::getStatus, "SUCCESS"));
        if (paidCount > 0) {
            throw new BusinessException(1001, "您已支付过该课程");
        }

        // 创建订单
        PaymentOrderEntity order = new PaymentOrderEntity();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setScheduleId(scheduleId);
        order.setAmount(amount);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        this.save(order);

        log.info("支付订单创建成功: orderNo={}, userId={}, amount={}",
                order.getOrderNo(), userId, amount);

        return toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO mockPay(String orderNo, Long userId) {
        PaymentOrderEntity order = this.getOne(new LambdaQueryWrapper<PaymentOrderEntity>()
                .eq(PaymentOrderEntity::getOrderNo, orderNo));

        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(1001, "订单状态异常，无法支付");
        }

        // 模拟支付成功
        order.setStatus("SUCCESS");
        order.setPaidAt(LocalDateTime.now());
        this.updateById(order);

        log.info("模拟支付成功: orderNo={}, userId={}, amount={}",
                orderNo, userId, order.getAmount());

        return new PaymentResultVO("SUCCESS", order.getAmount(), orderNo);
    }

    @Override
    public String getStatus(String orderNo) {
        PaymentOrderEntity order = this.getOne(new LambdaQueryWrapper<PaymentOrderEntity>()
                .eq(PaymentOrderEntity::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order.getStatus();
    }

    private PaymentVO toVO(PaymentOrderEntity entity) {
        PaymentVO vo = new PaymentVO();
        vo.setId(entity.getId());
        vo.setOrderNo(entity.getOrderNo());
        vo.setUserId(entity.getUserId());
        vo.setScheduleId(entity.getScheduleId());
        vo.setAmount(entity.getAmount());
        vo.setStatus(entity.getStatus());
        vo.setPaidAt(entity.getPaidAt());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    private String generateOrderNo() {
        LocalDate today = LocalDate.now();
        String datePart = String.format("%04d%02d%02d", today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        int random = ThreadLocalRandom.current().nextInt(10000000, 99999999);
        return datePart + random;
    }
}
