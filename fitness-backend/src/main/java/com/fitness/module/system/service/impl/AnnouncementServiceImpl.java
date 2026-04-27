package com.fitness.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.module.system.dto.AnnouncementCreateDTO;
import com.fitness.module.system.entity.AnnouncementEntity;
import com.fitness.module.system.mapper.AnnouncementMapper;
import com.fitness.module.system.service.AnnouncementService;
import com.fitness.module.system.vo.AnnouncementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity>
        implements AnnouncementService {

    @Override
    public PageResult<AnnouncementVO> pageList(Integer pageNum, Integer pageSize, String keyword) {
        IPage<AnnouncementEntity> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<AnnouncementEntity>()
                        .like(keyword != null, AnnouncementEntity::getTitle, keyword)
                        .orderByDesc(AnnouncementEntity::getIsTop)
                        .orderByDesc(AnnouncementEntity::getCreatedAt));

        return PageResult.of(
                page.getRecords().stream().map(this::toVO).collect(Collectors.toList()),
                page);
    }

    @Override
    public void create(AnnouncementCreateDTO dto, Long userId) {
        AnnouncementEntity entity = new AnnouncementEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType() != null ? dto.getType() : "NOTICE");
        entity.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        entity.setStatus(0); // 默认为草稿
        entity.setCreatedBy(userId);
        this.save(entity);
    }

    @Override
    public void update(Long id, AnnouncementCreateDTO dto) {
        AnnouncementEntity entity = this.getById(id);
        if (entity != null) {
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
            entity.setType(dto.getType() != null ? dto.getType() : entity.getType());
            entity.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : entity.getIsTop());
            this.updateById(entity);
        }
    }

    @Override
    public void toggleTop(Long id) {
        AnnouncementEntity entity = this.getById(id);
        if (entity != null) {
            entity.setIsTop(entity.getIsTop() == 1 ? 0 : 1);
            this.updateById(entity);
        }
    }

    @Override
    public void toggleStatus(Long id) {
        AnnouncementEntity entity = this.getById(id);
        if (entity != null) {
            int newStatus = entity.getStatus() == 1 ? 0 : 1;
            entity.setStatus(newStatus);
            if (newStatus == 1) {
                entity.setPublishAt(LocalDateTime.now());
            }
            this.updateById(entity);
        }
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }

    private AnnouncementVO toVO(AnnouncementEntity entity) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setType(entity.getType());
        vo.setIsTop(entity.getIsTop());
        vo.setStatus(entity.getStatus());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setPublishAt(entity.getPublishAt());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
