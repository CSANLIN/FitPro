package com.fitness.module.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.course.dto.CourseCreateDTO;
import com.fitness.module.course.dto.CourseUpdateDTO;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.mapper.CourseMapper;
import com.fitness.module.course.service.CourseService;
import com.fitness.module.course.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseEntity>
        implements CourseService {

    @Override
    public List<CourseVO> listAll(String keyword) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(CourseEntity::getName, keyword);
        }
        wrapper.orderByDesc(CourseEntity::getCreatedAt);
        return this.list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CourseVO> listPublished(String courseType) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<CourseEntity>()
                .eq(CourseEntity::getStatus, 1);
        if (courseType != null && !courseType.isEmpty()) {
            wrapper.eq(CourseEntity::getCourseType, courseType);
        }
        wrapper.orderByAsc(CourseEntity::getCreatedAt);
        return this.list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public CourseVO getDetail(Long id) {
        CourseEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "课程不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVO create(CourseCreateDTO dto) {
        CourseEntity entity = new CourseEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCoverImage(dto.getCoverImage());
        entity.setCourseType(dto.getCourseType());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setStatus(1); // 默认上架
        this.save(entity);

        log.info("课程创建成功: id={}, name={}", entity.getId(), dto.getName());
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVO update(CourseUpdateDTO dto) {
        CourseEntity entity = this.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(404, "课程不存在");
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCoverImage(dto.getCoverImage());
        entity.setCourseType(dto.getCourseType());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        this.updateById(entity);

        log.info("课程更新成功: id={}", dto.getId());
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id) {
        CourseEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "课程不存在");
        }

        int newStatus = entity.getStatus() == 1 ? 0 : 1;
        entity.setStatus(newStatus);
        this.updateById(entity);

        log.info("课程状态切换: id={}, status={}", id, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        CourseEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "课程不存在");
        }

        this.removeById(id);
        log.info("课程删除成功: id={}", id);
    }

    private CourseVO toVO(CourseEntity entity) {
        CourseVO vo = new CourseVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setCoverImage(entity.getCoverImage());
        vo.setCourseType(entity.getCourseType());
        vo.setDurationMinutes(entity.getDurationMinutes());
        vo.setMaxCapacity(entity.getMaxCapacity());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
