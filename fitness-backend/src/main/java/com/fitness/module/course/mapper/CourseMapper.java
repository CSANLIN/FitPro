package com.fitness.module.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.course.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {
}
