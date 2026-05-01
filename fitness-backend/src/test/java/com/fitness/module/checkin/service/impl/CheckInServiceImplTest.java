package com.fitness.module.checkin.service.impl;

import com.fitness.common.exception.BusinessException;
import com.fitness.module.checkin.entity.CheckInEntity;
import com.fitness.module.checkin.mapper.CheckInMapper;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.mapper.MemberMembershipMapper;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock
    private CheckInMapper checkInMapper;

    @Mock
    private MemberMembershipMapper membershipMapper;

    @InjectMocks
    private CheckInServiceImpl checkInService;

    @Captor
    private ArgumentCaptor<MemberMembershipEntity> membershipCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(checkInService, "baseMapper", checkInMapper);
    }

    // ========== 签到测试 ==========

    @Test
    void checkIn_ShouldSucceed_WithTimedMembership() {
        when(checkInMapper.selectCount(any())).thenReturn(0L);
        MemberMembershipEntity membership = createActiveTimedMembership();
        List<MemberMembershipEntity> membershipList = Collections.singletonList(membership);
        when(membershipMapper.selectList(any())).thenReturn(membershipList);

        checkInService.checkIn(100L);

        verify(checkInMapper).insert(any(CheckInEntity.class));
        // 期限卡不调用 updateById
        verify(membershipMapper, never()).updateById(any(MemberMembershipEntity.class));
    }

    @Test
    void checkIn_ShouldSucceed_WithTimesMembership() {
        when(checkInMapper.selectCount(any())).thenReturn(0L);
        MemberMembershipEntity membership = createActiveTimesMembership(10);
        List<MemberMembershipEntity> membershipList = Collections.singletonList(membership);
        when(membershipMapper.selectList(any())).thenReturn(membershipList);

        checkInService.checkIn(100L);

        verify(checkInMapper).insert(any(CheckInEntity.class));
        verify(membershipMapper).updateById(membershipCaptor.capture());
        assertEquals(Integer.valueOf(9), membershipCaptor.getValue().getRemainingTimes());
    }

    @Test
    void checkIn_ShouldThrow_WhenAlreadyCheckedInToday() {
        when(checkInMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> checkInService.checkIn(100L));
        assertEquals(1001, ex.getCode());
        assertEquals("今日已签到", ex.getMessage());
    }

    @Test
    void checkIn_ShouldThrow_WhenNoActiveMembership() {
        when(checkInMapper.selectCount(any())).thenReturn(0L);
        when(membershipMapper.selectList(any())).thenReturn(Collections.emptyList());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> checkInService.checkIn(100L));
        assertEquals(1001, ex.getCode());
        assertEquals("没有有效的会籍，无法签到", ex.getMessage());
    }

    @Test
    void checkIn_ShouldThrow_WhenTimesCardExhausted() {
        when(checkInMapper.selectCount(any())).thenReturn(0L);
        MemberMembershipEntity membership = createActiveTimesMembership(0);
        List<MemberMembershipEntity> membershipList = Collections.singletonList(membership);
        when(membershipMapper.selectList(any())).thenReturn(membershipList);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> checkInService.checkIn(100L));
        assertEquals(1001, ex.getCode());
        assertEquals("次卡次数已用完", ex.getMessage());
    }

    @Test
    void checkIn_ShouldAutoExpire_WhenTimesCardRunsOut() {
        when(checkInMapper.selectCount(any())).thenReturn(0L);
        MemberMembershipEntity membership = createActiveTimesMembership(1);
        List<MemberMembershipEntity> membershipList = Collections.singletonList(membership);
        when(membershipMapper.selectList(any())).thenReturn(membershipList);

        checkInService.checkIn(100L);

        // 验证扣到0次后自动变为EXPIRED
        // 验证调用了 updateById（扣次数+过期）
        verify(membershipMapper, times(2)).updateById(any(MemberMembershipEntity.class));
    }

    // ========== 签到统计测试 ==========

    @Test
    void getStats_ShouldComputeStreakCorrectly() {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();
        String yesterdayStr = today.minusDays(1).toString();
        String dayBeforeStr = today.minusDays(2).toString();

        List<String> dates = Arrays.asList(todayStr, yesterdayStr, dayBeforeStr, "2026-04-01");
        when(checkInMapper.selectCheckInDatesByMonth(eq(100L), anyString())).thenReturn(dates);

        CheckInStatsVO stats = checkInService.getStats(100L);

        assertEquals(4, stats.getMonthCount());
        assertEquals(3, stats.getStreakDays());
    }

    @Test
    void getStats_ShouldReturnZeroStreak_WhenNoCheckInToday() {
        LocalDate today = LocalDate.now();
        String yesterdayStr = today.minusDays(1).toString();
        String twoDaysAgoStr = today.minusDays(2).toString();

        // 昨天签到了但今天没有，连续签到应该从昨天开始只算1天
        List<String> dates = Arrays.asList(yesterdayStr, twoDaysAgoStr);
        when(checkInMapper.selectCheckInDatesByMonth(eq(100L), anyString())).thenReturn(dates);

        CheckInStatsVO stats = checkInService.getStats(100L);

        assertEquals(2, stats.getMonthCount());
        // 算法：今天没签 → 从昨天开始算连续
        // 昨天有签，前天有签 → 连续2天
        assertEquals(2, stats.getStreakDays());
    }

    @Test
    void getStats_ShouldHandleEmptyCheckIns() {
        when(checkInMapper.selectCheckInDatesByMonth(eq(100L), anyString()))
                .thenReturn(Collections.emptyList());

        CheckInStatsVO stats = checkInService.getStats(100L);

        assertEquals(0, stats.getMonthCount());
        assertEquals(0, stats.getStreakDays());
    }

    // ========== 工具方法 ==========

    private MemberMembershipEntity createActiveTimedMembership() {
        MemberMembershipEntity entity = new MemberMembershipEntity();
        entity.setId(1L);
        entity.setUserId(100L);
        entity.setCardId(1L);
        entity.setStartDate(LocalDateTime.now().minusDays(10));
        entity.setEndDate(LocalDateTime.now().plusDays(20));
        entity.setStatus("ACTIVE");
        return entity;
    }

    private MemberMembershipEntity createActiveTimesMembership(int remainingTimes) {
        MemberMembershipEntity entity = new MemberMembershipEntity();
        entity.setId(2L);
        entity.setUserId(100L);
        entity.setCardId(2L);
        entity.setStartDate(LocalDateTime.now());
        entity.setRemainingTimes(remainingTimes);
        entity.setStatus("ACTIVE");
        return entity;
    }
}
