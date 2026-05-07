package com.fitness.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.course.entity.CourseBookingEntity;
import com.fitness.module.coach.vo.CoachBookingVO;
import com.fitness.module.course.vo.BookingVO;
import com.fitness.module.course.vo.MyBookingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseBookingMapper extends BaseMapper<CourseBookingEntity> {

    List<MyBookingVO> selectMyBookingVOByUserId(@Param("userId") Long userId);

    List<BookingVO> selectBookingVOByScheduleId(@Param("scheduleId") Long scheduleId);

    List<BookingVO> selectBookingVOAll();

    List<BookingVO> selectBookingVOByCoachId(@Param("coachId") Long coachId);

    List<CoachBookingVO> selectCoachBookingVOByScheduleId(@Param("scheduleId") Long scheduleId);
}
