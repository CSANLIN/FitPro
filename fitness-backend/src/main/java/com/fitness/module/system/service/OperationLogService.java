package com.fitness.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.common.PageResult;
import com.fitness.module.system.entity.OperationLogEntity;
import com.fitness.module.system.vo.OperationLogVO;

public interface OperationLogService extends IService<OperationLogEntity> {

    PageResult<OperationLogVO> pageList(Integer pageNum, Integer pageSize, String module, String operation, String keyword);

    void saveLog(Long userId, String username, String module, String operation,
                 String method, String url, String ip, Integer duration, Integer status, String params, String result);
}
