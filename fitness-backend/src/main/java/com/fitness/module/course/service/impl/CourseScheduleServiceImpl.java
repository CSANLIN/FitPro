package com.fitness.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.dto.ScheduleCreateDTO;
import com.fitness.module.course.dto.ScheduleQueryDTO;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.entity.CourseScheduleEntity;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.mapper.CourseScheduleMapper;
import com.fitness.module.course.service.CourseScheduleService;
import com.fitness.module.course.vo.ScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseScheduleEntity>
        implements CourseScheduleService {

    private final CourseMapper courseMapper;
    private final CourseScheduleMapper courseScheduleMapper;

    @Override
    public List<ScheduleVO> listByDateRange(ScheduleQueryDTO query, Long currentUserId) {
        return courseScheduleMapper.selectScheduleVOByDateRange(
                query.getStartDate(),
                query.getEndDate(),
                query.getCourseId(),
                query.getCoachId(),
                currentUserId);
    }

    @Override
    public ScheduleVO getDetail(Long id, Long currentUserId) {
        ScheduleQueryDTO query = new ScheduleQueryDTO();
        List<ScheduleVO> list = courseScheduleMapper.selectScheduleVOByDateRange(
                null, null, null, null, currentUserId);
        return list.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "排课不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseScheduleEntity create(ScheduleCreateDTO dto) {
        // 校验课程存在且已上架
        CourseEntity course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        if (course.getStatus() != 1) {
            throw new BusinessException(1001, "该课程已下架，无法排课");
        }

        // 校验同一教练同一时间段没有其他排课
        long conflictCount = this.count(new LambdaQueryWrapper<CourseScheduleEntity>()
                .eq(CourseScheduleEntity::getCoachId, dto.getCoachId())
                .eq(CourseScheduleEntity::getScheduleDate, dto.getScheduleDate())
                .eq(CourseScheduleEntity::getStatus, "UPCOMING")
                .and(w -> w
                        .lt(CourseScheduleEntity::getStartTime, dto.getEndTime())
                        .gt(CourseScheduleEntity::getEndTime, dto.getStartTime())));
        if (conflictCount > 0) {
            throw new BusinessException(1001, "该教练在此时间段已有排课");
        }

        CourseScheduleEntity entity = new CourseScheduleEntity();
        entity.setCourseId(dto.getCourseId());
        entity.setCoachId(dto.getCoachId());
        entity.setScheduleDate(dto.getScheduleDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setLocation(dto.getLocation());
        entity.setCurrentCount(0);
        entity.setMaxCapacity(course.getMaxCapacity());
        entity.setStatus("UPCOMING");
        entity.setCreatedAt(LocalDateTime.now());
        this.save(entity);

        log.info("排课创建成功: id={}, courseId={}, date={}",
                entity.getId(), dto.getCourseId(), dto.getScheduleDate());
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        CourseScheduleEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "排课不存在");
        }
        if (!"UPCOMING".equals(entity.getStatus())) {
            throw new BusinessException(1001, "只能取消待开始的排课");
        }

        entity.setStatus("CANCELLED");
        this.updateById(entity);
        log.info("排课已取消: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheduleStatus() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 将今天的排课：已到开始时间 → ONGOING，已到结束时间 → FINISHED
        List<CourseScheduleEntity> todaySchedules = this.list(new LambdaQueryWrapper<CourseScheduleEntity>()
                .eq(CourseScheduleEntity::getScheduleDate, today)
                .in(CourseScheduleEntity::getStatus, "UPCOMING", "ONGOING"));

        for (CourseScheduleEntity schedule : todaySchedules) {
            if ("UPCOMING".equals(schedule.getStatus()) && !now.isBefore(schedule.getStartTime())) {
                schedule.setStatus("ONGOING");
                this.updateById(schedule);
            }
            if ("ONGOING".equals(schedule.getStatus()) && !now.isBefore(schedule.getEndTime())) {
                schedule.setStatus("FINISHED");
                this.updateById(schedule);
            }
        }
    }
}
