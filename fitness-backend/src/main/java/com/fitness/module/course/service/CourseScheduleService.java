package com.fitness.module.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.course.dto.ScheduleCreateDTO;
import com.fitness.module.course.dto.ScheduleQueryDTO;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.vo.ScheduleVO;

import java.util.List;

public interface CourseScheduleService extends IService<CourseScheduleEntity> {

    List<ScheduleVO> listByDateRange(ScheduleQueryDTO query, Long currentUserId);

    ScheduleVO getDetail(Long id, Long currentUserId);

    CourseScheduleEntity create(ScheduleCreateDTO dto);

    void cancel(Long id);

    void updateScheduleStatus();
}
