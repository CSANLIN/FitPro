package com.fitness.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.user.dto.BodyRecordCreateDTO;
import com.fitness.module.user.entity.BodyRecordEntity;
import com.fitness.module.user.vo.BodyRecordVO;

import java.time.LocalDate;
import java.util.List;

public interface BodyRecordService extends IService<BodyRecordEntity> {

    /**
     * 录入身体数据
     */
    BodyRecordVO create(Long userId, BodyRecordCreateDTO dto);

    /**
     * 查询当前用户的记录（按日期范围）
     */
    List<BodyRecordVO> listByUser(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户最新的一条记录
     */
    BodyRecordVO getLatest(Long userId);
}
