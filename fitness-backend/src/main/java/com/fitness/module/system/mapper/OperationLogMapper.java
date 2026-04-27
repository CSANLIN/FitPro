package com.fitness.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.system.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogEntity> {
}
