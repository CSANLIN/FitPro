package com.fitness.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.module.admin.service.DashboardService;
import com.fitness.module.admin.vo.DashboardStatsVO;
import com.fitness.module.admin.vo.DashboardStatsVO.ChartDataItem;
import com.fitness.module.admin.vo.DashboardStatsVO.CourseBookingRank;
import com.fitness.module.admin.vo.DashboardStatsVO.UpcomingSchedule;
import com.fitness.module.admin.vo.DashboardStatsVO.LatestCheckIn;
import com.fitness.module.checkin.entity.CheckInEntity;
import com.fitness.module.checkin.mapper.CheckInMapper;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.mapper.CourseBookingMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.mapper.MemberMembershipMapper;
import com.fitness.module.user.entity.UserEntity;
import com.fitness.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final CheckInMapper checkInMapper;
    private final MemberMembershipMapper membershipMapper;
    private final CourseMapper courseMapper;
    private final CourseScheduleMapper scheduleMapper;
    private final CourseBookingMapper bookingMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 基础统计
        stats.setTotalMembers(countMembers());
        stats.setTodayCheckIns(countTodayCheckIns());
        stats.setActiveMemberships(countActiveMemberships());
        stats.setTotalCourses(countCourses());
        stats.setUpcomingSchedules(countUpcomingSchedules());
        stats.setTotalCoaches(countCoaches());

        // 趋势数据
        stats.setCheckInTrend(getCheckInTrend());
        stats.setRegisterTrend(getRegisterTrend());

        // 排行数据
        stats.setCourseRankings(getCourseRankings());

        // 列表数据
        stats.setUpcomingScheduleList(getUpcomingScheduleList());
        stats.setTodayCheckInList(getTodayCheckInList());

        return stats;
    }

    private Long countMembers() {
        return userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getRole, "MEMBER")
                        .eq(UserEntity::getDeleted, 0));
    }

    private Long countCoaches() {
        return userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getRole, "COACH")
                        .eq(UserEntity::getDeleted, 0));
    }

    private Long countTodayCheckIns() {
        LocalDate today = LocalDate.now();
        return checkInMapper.selectCount(
                new LambdaQueryWrapper<CheckInEntity>()
                        .apply("DATE(check_in_time) = {0}", today));
    }

    private Long countActiveMemberships() {
        return membershipMapper.selectCount(
                new LambdaQueryWrapper<MemberMembershipEntity>()
                        .eq(MemberMembershipEntity::getStatus, "ACTIVE"));
    }

    private Long countCourses() {
        return courseMapper.selectCount(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getStatus, 1));
    }

    private Long countUpcomingSchedules() {
        LocalDate today = LocalDate.now();
        return scheduleMapper.selectCount(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .eq(CourseScheduleEntity::getStatus, "UPCOMING")
                        .ge(CourseScheduleEntity::getScheduleDate, today));
    }

    private List<ChartDataItem> getCheckInTrend() {
        List<ChartDataItem> trend = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Long count = checkInMapper.selectCount(
                    new LambdaQueryWrapper<CheckInEntity>()
                            .apply("DATE(check_in_time) = {0}", date));
            ChartDataItem item = new ChartDataItem();
            item.setDate(date.format(fmt));
            item.setCount(count);
            trend.add(item);
        }
        return trend;
    }

    private List<ChartDataItem> getRegisterTrend() {
        List<ChartDataItem> trend = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<UserEntity>()
                            .eq(UserEntity::getRole, "MEMBER")
                            .apply("DATE(created_at) = {0}", date));
            ChartDataItem item = new ChartDataItem();
            item.setDate(date.format(fmt));
            item.setCount(count);
            trend.add(item);
        }
        return trend;
    }

    private List<CourseBookingRank> getCourseRankings() {
        // 统计各课程预约量（近30天）
        LocalDate monthAgo = LocalDate.now().minusDays(30);
        List<CourseBookingEntity> bookings = bookingMapper.selectList(
                new LambdaQueryWrapper<CourseBookingEntity>()
                        .ge(CourseBookingEntity::getCreatedAt, monthAgo.atStartOfDay())
                        .eq(CourseBookingEntity::getStatus, "BOOKED"));

        Map<Long, Long> scheduleIdCounts = bookings.stream()
                .collect(Collectors.groupingBy(CourseBookingEntity::getScheduleId, Collectors.counting()));

        // 获取排课对应的课程信息
        List<CourseBookingRank> rankings = new ArrayList<>();
        // 简单返回：按数量排序取前10
        scheduleIdCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    CourseBookingRank rank = new CourseBookingRank();
                    rank.setCourseName("课程#" + entry.getKey());
                    rank.setBookingCount(entry.getValue());
                    rankings.add(rank);
                });

        return rankings;
    }

    private List<UpcomingSchedule> getUpcomingScheduleList() {
        LocalDate today = LocalDate.now();
        List<CourseScheduleEntity> entities = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseScheduleEntity>()
                        .eq(CourseScheduleEntity::getStatus, "UPCOMING")
                        .ge(CourseScheduleEntity::getScheduleDate, today)
                        .orderByAsc(CourseScheduleEntity::getScheduleDate)
                        .orderByAsc(CourseScheduleEntity::getStartTime)
                        .last("LIMIT 10"));

        // 批量加载课程信息
        Set<Long> courseIds = entities.stream().map(CourseScheduleEntity::getCourseId).collect(Collectors.toSet());
        Map<Long, String> courseNames = new HashMap<>();
        if (!courseIds.isEmpty()) {
            courseMapper.selectList(new LambdaQueryWrapper<CourseEntity>().in(CourseEntity::getId, courseIds))
                    .forEach(c -> courseNames.put(c.getId(), c.getName()));
        }

        List<UpcomingSchedule> list = new ArrayList<>();
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (CourseScheduleEntity entity : entities) {
            UpcomingSchedule item = new UpcomingSchedule();
            item.setId(entity.getId());
            item.setCourseName(courseNames.getOrDefault(entity.getCourseId(), "课程#" + entity.getCourseId()));
            item.setScheduleDate(entity.getScheduleDate() != null ? entity.getScheduleDate().format(dateFmt) : "");
            item.setStartTime(entity.getStartTime() != null ? entity.getStartTime().format(timeFmt) : "");
            item.setEndTime(entity.getEndTime() != null ? entity.getEndTime().format(timeFmt) : "");
            item.setLocation(entity.getLocation());
            list.add(item);
        }
        return list;
    }

    private List<LatestCheckIn> getTodayCheckInList() {
        LocalDate today = LocalDate.now();
        List<CheckInEntity> entities = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckInEntity>()
                        .apply("DATE(check_in_time) = {0}", today)
                        .orderByDesc(CheckInEntity::getCheckInTime)
                        .last("LIMIT 10"));

        List<LatestCheckIn> list = new ArrayList<>();
        // 由于没有直接联查用户名的字段，只返回签到时间和ID
        for (CheckInEntity entity : entities) {
            LatestCheckIn ci = new LatestCheckIn();
            ci.setId(entity.getId());
            ci.setUserName("用户" + entity.getUserId());
            ci.setCheckInTime(entity.getCheckInTime() != null
                    ? entity.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : "");
            list.add(ci);
        }
        return list;
    }
}
