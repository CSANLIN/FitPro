package com.fitness.module.workout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.common.PageResult;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.workout.dto.WorkoutRecordCreateDTO;
import com.fitness.module.workout.dto.WorkoutRecordQueryDTO;
import com.fitness.module.workout.entity.WorkoutRecordEntity;
import com.fitness.module.workout.entity.WorkoutRecordItemEntity;
import com.fitness.module.workout.mapper.WorkoutRecordItemMapper;
import com.fitness.module.workout.mapper.WorkoutRecordMapper;
import com.fitness.module.workout.service.WorkoutRecordService;
import com.fitness.module.workout.vo.WorkoutRecordDetailVO;
import com.fitness.module.workout.vo.WorkoutRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutRecordServiceImpl extends ServiceImpl<WorkoutRecordMapper, WorkoutRecordEntity>
        implements WorkoutRecordService {

    private final WorkoutRecordItemMapper recordItemMapper;

    @Override
    public PageResult<WorkoutRecordVO> pageList(WorkoutRecordQueryDTO query, Long userId) {
        IPage<WorkoutRecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        String startDate = query.getStartDate() != null ? query.getStartDate().toString() : null;
        String endDate = query.getEndDate() != null ? query.getEndDate().toString() : null;
        IPage<WorkoutRecordVO> result = this.baseMapper.selectRecordVOPage(page, userId, startDate, endDate);
        return PageResult.of(result);
    }

    @Override
    public WorkoutRecordDetailVO getDetail(Long id) {
        WorkoutRecordDetailVO detail = this.baseMapper.selectRecordDetail(id);
        if (detail == null) {
            throw new BusinessException(404, "训练记录不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkoutRecordDetailVO create(WorkoutRecordCreateDTO dto, Long userId) {
        WorkoutRecordEntity entity = new WorkoutRecordEntity();
        entity.setUserId(userId);
        entity.setPlanDayId(dto.getPlanDayId());
        entity.setName(dto.getName());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());

        // 计算训练时长
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            long minutes = java.time.Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
            entity.setDurationMinutes((int) minutes);
        }

        // 计算总训练量
        int totalVolume = 0;
        if (dto.getItems() != null) {
            for (WorkoutRecordCreateDTO.RecordItemDTO item : dto.getItems()) {
                if (item.getWeight() != null && item.getReps() != null) {
                    totalVolume += item.getWeight().multiply(BigDecimal.valueOf(item.getReps())).intValue();
                }
            }
        }
        entity.setTotalVolume(totalVolume);
        entity.setNote(dto.getNote());
        this.save(entity);

        // 保存训练组
        if (dto.getItems() != null) {
            for (WorkoutRecordCreateDTO.RecordItemDTO item : dto.getItems()) {
                WorkoutRecordItemEntity itemEntity = new WorkoutRecordItemEntity();
                itemEntity.setRecordId(entity.getId());
                itemEntity.setExerciseId(item.getExerciseId());
                itemEntity.setSetNumber(item.getSetNumber());
                itemEntity.setReps(item.getReps());
                itemEntity.setWeight(item.getWeight());
                itemEntity.setDurationSeconds(item.getDurationSeconds());
                itemEntity.setCompleted(item.getCompleted() != null ? item.getCompleted() : 1);
                recordItemMapper.insert(itemEntity);
            }
        }

        log.info("训练记录创建成功: id={}, name={}, totalVolume={}", entity.getId(), dto.getName(), totalVolume);
        return getDetail(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WorkoutRecordEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "训练记录不存在");
        }

        recordItemMapper.delete(new LambdaQueryWrapper<WorkoutRecordItemEntity>()
                .eq(WorkoutRecordItemEntity::getRecordId, id));
        this.removeById(id);
        log.info("训练记录删除成功: id={}", id);
    }

    @Override
    public Map<String, Object> weeklyStats(Long userId) {
        // 本周一日期
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Long count = this.baseMapper.selectWeeklyCount(userId, weekStart.toString());
        Long volume = this.baseMapper.selectWeeklyVolume(userId, weekStart.toString());

        Map<String, Object> stats = new HashMap<>();
        stats.put("weeklyCount", count != null ? count : 0);
        stats.put("weeklyVolume", volume != null ? volume : 0);
        return stats;
    }
}
