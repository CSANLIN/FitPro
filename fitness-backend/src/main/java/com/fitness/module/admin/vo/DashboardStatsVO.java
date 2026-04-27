package com.fitness.module.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "仪表盘统计数据")
public class DashboardStatsVO {

    @Schema(description = "会员总数")
    private Long totalMembers;

    @Schema(description = "今日签到数")
    private Long todayCheckIns;

    @Schema(description = "有效会籍数")
    private Long activeMemberships;

    @Schema(description = "课程总数")
    private Long totalCourses;

    @Schema(description = "待开始排课数")
    private Long upcomingSchedules;

    @Schema(description = "教练总数")
    private Long totalCoaches;

    @Schema(description = "近7日签到趋势")
    private List<ChartDataItem> checkInTrend;

    @Schema(description = "近7日注册趋势")
    private List<ChartDataItem> registerTrend;

    @Schema(description = "课程预约量排行")
    private List<CourseBookingRank> courseRankings;

    @Schema(description = "近期待办排课")
    private List<UpcomingSchedule> upcomingScheduleList;

    @Schema(description = "今日签到记录")
    private List<LatestCheckIn> todayCheckInList;

    @Data
    @Schema(description = "图表数据项")
    public static class ChartDataItem {
        private String date;
        private Long count;
    }

    @Data
    @Schema(description = "课程预约排行")
    public static class CourseBookingRank {
        private String courseName;
        private Long bookingCount;
    }

    @Data
    @Schema(description = "待办排课")
    public static class UpcomingSchedule {
        private Long id;
        private String courseName;
        private String coachName;
        private String scheduleDate;
        private String startTime;
        private String endTime;
        private String location;
    }

    @Data
    @Schema(description = "签到记录简要")
    public static class LatestCheckIn {
        private Long id;
        private String userName;
        private String checkInTime;
    }
}
