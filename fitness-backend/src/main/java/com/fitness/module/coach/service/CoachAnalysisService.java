package com.fitness.module.coach.service;

import com.fitness.module.coach.vo.AttendanceTrendVO;
import com.fitness.module.coach.vo.CoachSummaryVO;
import com.fitness.module.coach.vo.CourseRankVO;
import com.fitness.module.coach.vo.TimeDistributionVO;

import java.util.List;

public interface CoachAnalysisService {

    CoachSummaryVO getSummary(Long coachId);

    List<AttendanceTrendVO> getAttendanceTrend(Long coachId, String period);

    List<CourseRankVO> getCourseRank(Long coachId);

    List<TimeDistributionVO> getTimeDistribution(Long coachId);
}
