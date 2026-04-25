package com.fitness.module.exercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.exercise.dto.ExerciseCreateDTO;
import com.fitness.module.exercise.dto.ExerciseQueryDTO;
import com.fitness.module.exercise.dto.ExerciseUpdateDTO;
import com.fitness.module.exercise.entity.ExerciseCategoryEntity;
import com.fitness.module.exercise.entity.ExerciseEntity;
import com.fitness.module.exercise.mapper.ExerciseCategoryMapper;
import com.fitness.module.exercise.mapper.ExerciseMapper;
import com.fitness.module.exercise.service.ExerciseService;
import com.fitness.module.exercise.vo.ExerciseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, ExerciseEntity>
        implements ExerciseService {

    private final ExerciseCategoryMapper exerciseCategoryMapper;

    @Override
    public PageResult<ExerciseVO> pageList(ExerciseQueryDTO query) {
        IPage<ExerciseVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<ExerciseVO> result = this.baseMapper.selectExerciseVOPage(
                page,
                query.getCategoryId(),
                query.getMuscleGroup(),
                query.getEquipment(),
                query.getDifficulty(),
                query.getKeyword()
        );
        return PageResult.of(result);
    }

    @Override
    public ExerciseVO getDetail(Long id) {
        ExerciseEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "动作不存在");
        }
        return toVO(entity);
    }

    private ExerciseVO toVO(ExerciseEntity entity) {
        ExerciseVO vo = new ExerciseVO();
        vo.setId(entity.getId());
        vo.setCategoryId(entity.getCategoryId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setMuscleGroup(entity.getMuscleGroup());
        vo.setEquipment(entity.getEquipment());
        vo.setDifficulty(entity.getDifficulty());
        vo.setVideoUrl(entity.getVideoUrl());
        vo.setImageUrl(entity.getImageUrl());
        vo.setCreatedAt(entity.getCreatedAt());

        // 查询分类名称
        ExerciseCategoryEntity category = exerciseCategoryMapper.selectById(entity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseVO create(ExerciseCreateDTO dto) {
        // 校验分类存在
        ExerciseCategoryEntity category = exerciseCategoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException(404, "所属分类不存在");
        }

        // 校验同分类下动作名称唯一
        long count = this.count(new LambdaQueryWrapper<ExerciseEntity>()
                .eq(ExerciseEntity::getCategoryId, dto.getCategoryId())
                .eq(ExerciseEntity::getName, dto.getName()));
        if (count > 0) {
            throw new BusinessException(1001, "该分类下已存在同名动作");
        }

        ExerciseEntity entity = new ExerciseEntity();
        entity.setCategoryId(dto.getCategoryId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setMuscleGroup(dto.getMuscleGroup());
        entity.setEquipment(dto.getEquipment());
        entity.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : "BEGINNER");
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setImageUrl(dto.getImageUrl());

        this.save(entity);
        log.info("运动动作创建成功: id={}, name={}", entity.getId(), dto.getName());

        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseVO update(ExerciseUpdateDTO dto) {
        ExerciseEntity entity = this.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(404, "动作不存在");
        }

        // 校验分类存在
        ExerciseCategoryEntity category = exerciseCategoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException(404, "所属分类不存在");
        }

        // 校验名称唯一（排除自身）
        long count = this.count(new LambdaQueryWrapper<ExerciseEntity>()
                .eq(ExerciseEntity::getCategoryId, dto.getCategoryId())
                .eq(ExerciseEntity::getName, dto.getName())
                .ne(ExerciseEntity::getId, dto.getId()));
        if (count > 0) {
            throw new BusinessException(1001, "该分类下已存在同名动作");
        }

        entity.setCategoryId(dto.getCategoryId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setMuscleGroup(dto.getMuscleGroup());
        entity.setEquipment(dto.getEquipment());
        entity.setDifficulty(dto.getDifficulty());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setImageUrl(dto.getImageUrl());

        this.updateById(entity);
        log.info("运动动作更新成功: id={}", entity.getId());

        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ExerciseEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "动作不存在");
        }

        this.removeById(id);
        log.info("运动动作删除成功: id={}", id);
    }
}
