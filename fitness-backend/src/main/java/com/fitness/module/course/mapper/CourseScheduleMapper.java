package com.fitness.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.vo.ScheduleVO;
import com.fitness.module.coach.vo.CoachScheduleVO;
import com.fitness.module.coach.vo.CourseRankVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseScheduleEntity> {

    List<ScheduleVO> selectScheduleVOByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("courseId") Long courseId,
            @Param("coachId") Long coachId,
            @Param("userId") Long userId);

    /**
     * 原子更新当前预约人数（乐观锁，防止超卖）
     */
    @Update("UPDATE course_schedule SET current_count = current_count + 1 " +
            "WHERE id = #{scheduleId} AND current_count = #{expectedCount} " +
            "AND current_count < max_capacity")
    int updateCurrentCount(@Param("scheduleId") Long scheduleId,
                           @Param("expectedCount") Integer expectedCount);

    List<CoachScheduleVO> selectCoachScheduleVO(
            @Param("coachId") Long coachId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<CourseRankVO> selectCourseRankByCoach(@Param("coachId") Long coachId);
}
