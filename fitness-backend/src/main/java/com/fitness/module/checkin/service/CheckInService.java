package com.fitness.module.checkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.module.checkin.entity.CheckInEntity;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.checkin.vo.CheckInVO;

import java.util.List;

public interface CheckInService extends IService<CheckInEntity> {

    void checkIn(Long userId);

    List<CheckInVO> listByUser(Long userId);

    List<CheckInVO> listAll();

    CheckInStatsVO getStats(Long userId);
}
