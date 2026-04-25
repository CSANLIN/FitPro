package com.fitness.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.module.user.dto.BodyRecordCreateDTO;
import com.fitness.module.user.entity.BodyRecordEntity;
import com.fitness.module.user.mapper.BodyRecordMapper;
import com.fitness.module.user.service.BodyRecordService;
import com.fitness.module.user.vo.BodyRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BodyRecordServiceImpl extends ServiceImpl<BodyRecordMapper, BodyRecordEntity>
        implements BodyRecordService {

    /**
     * 计算 BMI = 体重(kg) / (身高(m))^2
     */
    private BigDecimal calculateBmi(BigDecimal weight, BigDecimal height) {
        if (weight == null || height == null || height.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        // 身高 cm 转换为 m
        BigDecimal heightInMeter = height.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        // BMI = weight / (height^2)
        return weight.divide(heightInMeter.multiply(heightInMeter), 2, RoundingMode.HALF_UP);
    }

    private BodyRecordVO toVO(BodyRecordEntity entity) {
        BodyRecordVO vo = new BodyRecordVO();
        vo.setId(entity.getId());
        vo.setWeight(entity.getWeight());
        vo.setHeight(entity.getHeight());
        vo.setBodyFat(entity.getBodyFat());
        vo.setBmi(entity.getBmi());
        vo.setChest(entity.getChest());
        vo.setWaist(entity.getWaist());
        vo.setHip(entity.getHip());
        vo.setRecordDate(entity.getRecordDate());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BodyRecordVO create(Long userId, BodyRecordCreateDTO dto) {
        BodyRecordEntity entity = new BodyRecordEntity();
        entity.setUserId(userId);
        entity.setWeight(dto.getWeight());
        entity.setHeight(dto.getHeight());
        entity.setBodyFat(dto.getBodyFat());
        entity.setChest(dto.getChest());
        entity.setWaist(dto.getWaist());
        entity.setHip(dto.getHip());
        entity.setRecordDate(dto.getRecordDate());
        entity.setRemark(dto.getRemark());

        // 自动计算 BMI
        entity.setBmi(calculateBmi(dto.getWeight(), dto.getHeight()));

        this.save(entity);
        log.info("身体数据录入成功: userId={}, recordDate={}", userId, dto.getRecordDate());

        return toVO(entity);
    }

    @Override
    public List<BodyRecordVO> listByUser(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<BodyRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyRecordEntity::getUserId, userId);
        if (startDate != null) {
            wrapper.ge(BodyRecordEntity::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(BodyRecordEntity::getRecordDate, endDate);
        }
        wrapper.orderByDesc(BodyRecordEntity::getRecordDate);

        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public BodyRecordVO getLatest(Long userId) {
        LambdaQueryWrapper<BodyRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyRecordEntity::getUserId, userId)
                .orderByDesc(BodyRecordEntity::getRecordDate)
                .orderByDesc(BodyRecordEntity::getCreatedAt)
                .last("LIMIT 1");

        BodyRecordEntity entity = this.getOne(wrapper);
        return entity != null ? toVO(entity) : null;
    }
}
