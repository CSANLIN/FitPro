package com.fitness.module.workout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.workout.dto.WorkoutTemplateCreateDTO;
import com.fitness.module.workout.dto.WorkoutTemplateUpdateDTO;
import com.fitness.module.workout.entity.WorkoutTemplateEntity;
import com.fitness.module.workout.entity.WorkoutTemplateItemEntity;
import com.fitness.module.workout.mapper.WorkoutTemplateItemMapper;
import com.fitness.module.workout.mapper.WorkoutTemplateMapper;
import com.fitness.module.workout.service.WorkoutTemplateService;
import com.fitness.module.workout.vo.WorkoutTemplateDetailVO;
import com.fitness.module.workout.vo.WorkoutTemplateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutTemplateServiceImpl extends ServiceImpl<WorkoutTemplateMapper, WorkoutTemplateEntity>
        implements WorkoutTemplateService {

    private final WorkoutTemplateItemMapper templateItemMapper;

    @Override
    public List<WorkoutTemplateVO> listAll() {
        return this.baseMapper.selectList(null).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public WorkoutTemplateDetailVO getDetail(Long id) {
        WorkoutTemplateEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "模板不存在");
        }
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkoutTemplateDetailVO create(WorkoutTemplateCreateDTO dto, Long coachId) {
        // 保存模板
        WorkoutTemplateEntity entity = new WorkoutTemplateEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCoachId(coachId);
        entity.setTargetType(dto.getTargetType());
        entity.setDifficulty(dto.getDifficulty());
        this.save(entity);

        // 保存模板动作项
        saveItems(entity.getId(), dto.getItems());

        log.info("训练模板创建成功: id={}, name={}", entity.getId(), dto.getName());
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkoutTemplateDetailVO update(WorkoutTemplateUpdateDTO dto) {
        WorkoutTemplateEntity entity = this.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(404, "模板不存在");
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setTargetType(dto.getTargetType());
        entity.setDifficulty(dto.getDifficulty());
        this.updateById(entity);

        // 删除旧动作项，重新插入
        templateItemMapper.delete(new LambdaQueryWrapper<WorkoutTemplateItemEntity>()
                .eq(WorkoutTemplateItemEntity::getTemplateId, dto.getId()));
        saveItems(entity.getId(), dto.getItems());

        log.info("训练模板更新成功: id={}", entity.getId());
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WorkoutTemplateEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "模板不存在");
        }

        // 删除模板关联的动作项
        templateItemMapper.delete(new LambdaQueryWrapper<WorkoutTemplateItemEntity>()
                .eq(WorkoutTemplateItemEntity::getTemplateId, id));

        this.removeById(id);
        log.info("训练模板删除成功: id={}", id);
    }

    private void saveItems(Long templateId, List<WorkoutTemplateCreateDTO.TemplateItemDTO> items) {
        for (int i = 0; i < items.size(); i++) {
            WorkoutTemplateCreateDTO.TemplateItemDTO item = items.get(i);
            WorkoutTemplateItemEntity itemEntity = new WorkoutTemplateItemEntity();
            itemEntity.setTemplateId(templateId);
            itemEntity.setExerciseId(item.getExerciseId());
            itemEntity.setSets(item.getSets());
            itemEntity.setReps(item.getReps());
            itemEntity.setRestSeconds(item.getRestSeconds());
            itemEntity.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : i);
            templateItemMapper.insert(itemEntity);
        }
    }

    private WorkoutTemplateVO toVO(WorkoutTemplateEntity entity) {
        WorkoutTemplateVO vo = new WorkoutTemplateVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setTargetType(entity.getTargetType());
        vo.setDifficulty(entity.getDifficulty());
        vo.setCreatedAt(entity.getCreatedAt());

        long count = templateItemMapper.selectCount(
                new LambdaQueryWrapper<WorkoutTemplateItemEntity>()
                        .eq(WorkoutTemplateItemEntity::getTemplateId, entity.getId()));
        vo.setItemCount((int) count);

        return vo;
    }

    private WorkoutTemplateDetailVO toDetailVO(WorkoutTemplateEntity entity) {
        WorkoutTemplateDetailVO vo = new WorkoutTemplateDetailVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setTargetType(entity.getTargetType());
        vo.setDifficulty(entity.getDifficulty());
        vo.setCreatedAt(entity.getCreatedAt());

        List<WorkoutTemplateItemEntity> items = templateItemMapper.selectList(
                new LambdaQueryWrapper<WorkoutTemplateItemEntity>()
                        .eq(WorkoutTemplateItemEntity::getTemplateId, entity.getId())
                        .orderByAsc(WorkoutTemplateItemEntity::getSortOrder));

        vo.setItems(items.stream().map(item -> {
            WorkoutTemplateDetailVO.TemplateItemVO itemVO = new WorkoutTemplateDetailVO.TemplateItemVO();
            itemVO.setId(item.getId());
            itemVO.setExerciseId(item.getExerciseId());
            itemVO.setSets(item.getSets());
            itemVO.setReps(item.getReps());
            itemVO.setRestSeconds(item.getRestSeconds());
            itemVO.setSortOrder(item.getSortOrder());
            return itemVO;
        }).collect(Collectors.toList()));

        return vo;
    }
}
