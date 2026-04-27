package com.fitness.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.module.system.entity.OperationLogEntity;
import com.fitness.module.system.mapper.OperationLogMapper;
import com.fitness.module.system.service.OperationLogService;
import com.fitness.module.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity>
        implements OperationLogService {

    @Override
    public PageResult<OperationLogVO> pageList(Integer pageNum, Integer pageSize, String module,
                                                String operation, String keyword) {
        IPage<OperationLogEntity> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<OperationLogEntity>()
                        .eq(module != null, OperationLogEntity::getModule, module)
                        .eq(operation != null, OperationLogEntity::getOperation, operation)
                        .and(keyword != null, w -> w
                                .like(OperationLogEntity::getUsername, keyword)
                                .or().like(OperationLogEntity::getUrl, keyword))
                        .orderByDesc(OperationLogEntity::getCreatedAt));

        return PageResult.of(
                page.getRecords().stream().map(this::toVO).collect(Collectors.toList()),
                page);
    }

    @Override
    public void saveLog(Long userId, String username, String module, String operation,
                         String method, String url, String ip, Integer duration,
                         Integer status, String params, String result) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.setUserId(userId);
        entity.setUsername(username);
        entity.setModule(module);
        entity.setOperation(operation);
        entity.setMethod(method);
        entity.setUrl(url);
        entity.setIp(ip);
        entity.setDuration(duration);
        entity.setStatus(status);
        entity.setParams(params);
        entity.setResult(result);
        entity.setCreatedAt(LocalDateTime.now());
        this.save(entity);
    }

    private OperationLogVO toVO(OperationLogEntity entity) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setUsername(entity.getUsername());
        vo.setModule(entity.getModule());
        vo.setOperation(entity.getOperation());
        vo.setMethod(entity.getMethod());
        vo.setUrl(entity.getUrl());
        vo.setIp(entity.getIp());
        vo.setDuration(entity.getDuration());
        vo.setStatus(entity.getStatus());
        vo.setParams(entity.getParams());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}
