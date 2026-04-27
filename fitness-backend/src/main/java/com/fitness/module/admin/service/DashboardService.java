package com.fitness.module.admin.service;

import com.fitness.module.admin.vo.DashboardStatsVO;

public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsVO getStats();
}
