package com.fitness.module.exercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.exercise.dto.ExerciseCategoryCreateDTO;
import com.fitness.module.exercise.dto.ExerciseCategoryUpdateDTO;
import com.fitness.module.exercise.entity.ExerciseCategoryEntity;
import com.fitness.module.exercise.mapper.ExerciseCategoryMapper;
import com.fitness.module.exercise.mapper.ExerciseMapper;
import com.fitness.module.exercise.service.ExerciseCategoryService;
import com.fitness.module.exercise.vo.ExerciseCategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseCategoryServiceImpl extends ServiceImpl<ExerciseCategoryMapper, ExerciseCategoryEntity>
        implements ExerciseCategoryService {

    private final ExerciseMapper exerciseMapper;

    private ExerciseCategoryVO toVO(ExerciseCategoryEntity entity, Integer exerciseCount) {
        ExerciseCategoryVO vo = new ExerciseCategoryVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setIcon(entity.getIcon());
        vo.setSortOrder(entity.getSortOrder());
        vo.setExerciseCount(exerciseCount);
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    @Override
    public List<ExerciseCategoryVO> listAll() {
        List<ExerciseCategoryEntity> categories = this.list(new LambdaQueryWrapper<ExerciseCategoryEntity>()
                .orderByAsc(ExerciseCategoryEntity::getSortOrder));

        return categories.stream().map(cat -> {
            long count = exerciseMapper.selectCount(
                    new LambdaQueryWrapper<com.fitness.module.exercise.entity.ExerciseEntity>()
                            .eq(com.fitness.module.exercise.entity.ExerciseEntity::getCategoryId, cat.getId()));
            return toVO(cat, (int) count);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseCategoryVO create(ExerciseCategoryCreateDTO dto) {
        // 检查分类名称是否已存在
        long count = this.count(new LambdaQueryWrapper<ExerciseCategoryEntity>()
                .eq(ExerciseCategoryEntity::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException(1001, "分类名称已存在");
        }

        ExerciseCategoryEntity entity = new ExerciseCategoryEntity();
        entity.setName(dto.getName());
        entity.setIcon(dto.getIcon());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        this.save(entity);
        log.info("运动分类创建成功: id={}, name={}", entity.getId(), dto.getName());

        return toVO(entity, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseCategoryVO update(ExerciseCategoryUpdateDTO dto) {
        ExerciseCategoryEntity entity = this.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(404, "分类不存在");
        }

        // 检查名称是否与其他分类冲突
        long count = this.count(new LambdaQueryWrapper<ExerciseCategoryEntity>()
                .eq(ExerciseCategoryEntity::getName, dto.getName())
                .ne(ExerciseCategoryEntity::getId, dto.getId()));
        if (count > 0) {
            throw new BusinessException(1001, "分类名称已存在");
        }

        entity.setName(dto.getName());
        entity.setIcon(dto.getIcon());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        this.updateById(entity);
        log.info("运动分类更新成功: id={}", entity.getId());

        return toVO(entity, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ExerciseCategoryEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "分类不存在");
        }

        // 检查分类下是否有动作
        long exerciseCount = exerciseMapper.selectCount(
                new LambdaQueryWrapper<com.fitness.module.exercise.entity.ExerciseEntity>()
                        .eq(com.fitness.module.exercise.entity.ExerciseEntity::getCategoryId, id));
        if (exerciseCount > 0) {
            throw new BusinessException(1002, "该分类下还有" + exerciseCount + "个动作，无法删除");
        }

        this.removeById(id);
        log.info("运动分类删除成功: id={}", id);
    }
}
