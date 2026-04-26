package com.fitness.module.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.checkin.entity.CheckInEntity;
import com.fitness.module.checkin.mapper.CheckInMapper;
import com.fitness.module.checkin.service.CheckInService;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.checkin.vo.CheckInVO;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.mapper.MemberMembershipMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckInEntity>
        implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final MemberMembershipMapper membershipMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long userId) {
        // 校验今日是否已签到
        LocalDate today = LocalDate.now();
        long todayCount = this.count(new LambdaQueryWrapper<CheckInEntity>()
                .eq(CheckInEntity::getUserId, userId)
                .apply("DATE(check_in_time) = {0}", today));
        if (todayCount > 0) {
            throw new BusinessException(1001, "今日已签到");
        }

        // 校验有效会籍
        MemberMembershipEntity activeMembership = getActiveMembership(userId);
        if (activeMembership == null) {
            throw new BusinessException(1001, "没有有效的会籍，无法签到");
        }

        // 次卡扣次数
        if (activeMembership.getRemainingTimes() != null) {
            if (activeMembership.getRemainingTimes() <= 0) {
                throw new BusinessException(1001, "次卡次数已用完");
            }
            activeMembership.setRemainingTimes(activeMembership.getRemainingTimes() - 1);
            membershipMapper.updateById(activeMembership);

            // 如果次数为0且是次卡，自动过期
            if (activeMembership.getRemainingTimes() == 0) {
                activeMembership.setStatus("EXPIRED");
                membershipMapper.updateById(activeMembership);
            }
        }

        // 创建签到记录
        CheckInEntity entity = new CheckInEntity();
        entity.setUserId(userId);
        entity.setMembershipId(activeMembership.getId());
        entity.setCheckInTime(LocalDateTime.now());
        entity.setCheckInType("MANUAL");
        entity.setCreatedAt(LocalDateTime.now());
        this.save(entity);

        log.info("签到成功: userId={}, membershipId={}", userId, activeMembership.getId());
    }

    @Override
    public List<CheckInVO> listByUser(Long userId) {
        return checkInMapper.selectCheckInVOByUserId(userId);
    }

    @Override
    public List<CheckInVO> listAll() {
        return checkInMapper.selectAllCheckInVO();
    }

    @Override
    public CheckInStatsVO getStats(Long userId) {
        LocalDate today = LocalDate.now();
        String yearMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        List<String> checkInDates = checkInMapper.selectCheckInDatesByMonth(userId, yearMonth);
        long monthCount = checkInDates.size();

        // 计算连续签到天数
        long streakDays = 0;
        LocalDate checkDate = today;
        while (true) {
            String dateStr = checkDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (checkInDates.contains(dateStr)) {
                streakDays++;
                checkDate = checkDate.minusDays(1);
            } else if (checkDate.equals(today)) {
                // 今天还没签到，从昨天开始算
                checkDate = checkDate.minusDays(1);
                continue;
            } else {
                break;
            }
        }

        CheckInStatsVO stats = new CheckInStatsVO();
        stats.setMonthCount(monthCount);
        stats.setStreakDays(streakDays);
        stats.setCheckInDates(checkInDates);
        return stats;
    }

    private MemberMembershipEntity getActiveMembership(Long userId) {
        List<MemberMembershipEntity> list = membershipMapper.selectList(
                new LambdaQueryWrapper<MemberMembershipEntity>()
                        .eq(MemberMembershipEntity::getUserId, userId)
                        .eq(MemberMembershipEntity::getStatus, "ACTIVE")
                        .orderByDesc(MemberMembershipEntity::getCreatedAt)
                        .last("LIMIT 1"));
        return list.isEmpty() ? null : list.get(0);
    }
}
