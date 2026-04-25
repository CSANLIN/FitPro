package com.fitness.module.workout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.workout.dto.WorkoutPlanCreateDTO;
import com.fitness.module.workout.dto.WorkoutPlanQueryDTO;
import com.fitness.module.workout.entity.WorkoutPlanDayEntity;
import com.fitness.module.workout.entity.WorkoutPlanDayItemEntity;
import com.fitness.module.workout.entity.WorkoutPlanEntity;
import com.fitness.module.workout.mapper.WorkoutPlanDayItemMapper;
import com.fitness.module.workout.mapper.WorkoutPlanDayMapper;
import com.fitness.module.workout.mapper.WorkoutPlanMapper;
import com.fitness.module.workout.service.WorkoutPlanService;
import com.fitness.module.workout.vo.WorkoutPlanDetailVO;
import com.fitness.module.workout.vo.WorkoutPlanVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl extends ServiceImpl<WorkoutPlanMapper, WorkoutPlanEntity>
        implements WorkoutPlanService {

    private final WorkoutPlanDayMapper planDayMapper;
    private final WorkoutPlanDayItemMapper planDayItemMapper;

    @Override
    public PageResult<WorkoutPlanVO> pageList(WorkoutPlanQueryDTO query, Long userId) {
        IPage<WorkoutPlanVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<WorkoutPlanVO> result = this.baseMapper.selectPlanVOPage(
                page, query.getKeyword(), query.getStatus(), userId);
        return PageResult.of(result);
    }

    @Override
    public WorkoutPlanDetailVO getDetail(Long id) {
        WorkoutPlanDetailVO detail = this.baseMapper.selectPlanDetail(id);
        if (detail == null) {
            throw new BusinessException(404, "计划不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkoutPlanDetailVO create(WorkoutPlanCreateDTO dto, Long userId, Long coachId) {
        WorkoutPlanEntity entity = new WorkoutPlanEntity();
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : userId);
        entity.setCoachId(coachId);
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus("ACTIVE");
        this.save(entity);

        Long planId = entity.getId();

        // 保存训练日
        for (WorkoutPlanCreateDTO.PlanDayDTO day : dto.getDays()) {
            WorkoutPlanDayEntity dayEntity = new WorkoutPlanDayEntity();
            dayEntity.setPlanId(planId);
            dayEntity.setDayOfWeek(day.getDayOfWeek());
            dayEntity.setName(day.getName());
            dayEntity.setTemplateId(day.getTemplateId());
            planDayMapper.insert(dayEntity);

            // 保存训练日动作
            if (day.getItems() != null) {
                for (int i = 0; i < day.getItems().size(); i++) {
                    WorkoutPlanCreateDTO.PlanDayDTO.PlanDayItemDTO item = day.getItems().get(i);
                    WorkoutPlanDayItemEntity itemEntity = new WorkoutPlanDayItemEntity();
                    itemEntity.setPlanDayId(dayEntity.getId());
                    itemEntity.setExerciseId(item.getExerciseId());
                    itemEntity.setSets(item.getSets());
                    itemEntity.setReps(item.getReps());
                    itemEntity.setWeight(item.getWeight());
                    itemEntity.setRestSeconds(item.getRestSeconds());
                    itemEntity.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : i);
                    planDayItemMapper.insert(itemEntity);
                }
            }
        }

        log.info("训练计划创建成功: id={}, name={}", planId, dto.getName());
        return getDetail(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        WorkoutPlanEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "计划不存在");
        }

        entity.setStatus(status);
        this.updateById(entity);
        log.info("训练计划状态更新: id={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WorkoutPlanEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "计划不存在");
        }

        // 删除关联的训练日、训练日动作
        LambdaQueryWrapper<WorkoutPlanDayEntity> dayWrapper = new LambdaQueryWrapper<WorkoutPlanDayEntity>()
                .eq(WorkoutPlanDayEntity::getPlanId, id);
        for (WorkoutPlanDayEntity day : planDayMapper.selectList(dayWrapper)) {
            planDayItemMapper.delete(new LambdaQueryWrapper<WorkoutPlanDayItemEntity>()
                    .eq(WorkoutPlanDayItemEntity::getPlanDayId, day.getId()));
        }
        planDayMapper.delete(dayWrapper);
        this.removeById(id);

        log.info("训练计划删除成功: id={}", id);
    }
}
