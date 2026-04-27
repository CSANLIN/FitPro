package com.fitness.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.common.PageResult;
import com.fitness.module.system.dto.AnnouncementCreateDTO;
import com.fitness.module.system.entity.AnnouncementEntity;
import com.fitness.module.system.vo.AnnouncementVO;

public interface AnnouncementService extends IService<AnnouncementEntity> {

    PageResult<AnnouncementVO> pageList(Integer pageNum, Integer pageSize, String keyword);

    void create(AnnouncementCreateDTO dto, Long userId);

    void update(Long id, AnnouncementCreateDTO dto);

    void toggleTop(Long id);

    void toggleStatus(Long id);

    void delete(Long id);
}
