package com.fitness.module.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.course.dto.CourseCreateDTO;
import com.fitness.module.course.dto.CourseUpdateDTO;
import com.fitness.module.course.entity.CourseEntity;
import com.fitness.module.course.vo.CourseVO;

import java.util.List;

public interface CourseService extends IService<CourseEntity> {

    List<CourseVO> listAll(String keyword);

    List<CourseVO> listPublished(String courseType);

    CourseVO getDetail(Long id);

    CourseVO create(CourseCreateDTO dto);

    CourseVO update(CourseUpdateDTO dto);

    void toggleStatus(Long id);

    void delete(Long id);
}
