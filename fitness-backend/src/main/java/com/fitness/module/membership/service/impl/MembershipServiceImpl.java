package com.fitness.module.membership.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.common.exception.BusinessException;
import com.fitness.module.membership.dto.CardCreateDTO;
import com.fitness.module.membership.dto.MembershipCreateDTO;
import com.fitness.module.membership.dto.MembershipRenewDTO;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.entity.MembershipCardEntity;
import com.fitness.module.membership.mapper.MemberMembershipMapper;
import com.fitness.module.membership.mapper.MembershipCardMapper;
import com.fitness.module.membership.service.MembershipService;
import com.fitness.module.membership.vo.MembershipCardVO;
import com.fitness.module.membership.vo.MembershipVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipCardMapper cardMapper;
    private final MemberMembershipMapper membershipMapper;

    // ========== 卡种管理 ==========

    @Override
    public List<MembershipCardVO> listCards(String keyword) {
        LambdaQueryWrapper<MembershipCardEntity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(MembershipCardEntity::getCardName, keyword);
        }
        wrapper.orderByDesc(MembershipCardEntity::getCreatedAt);
        return cardMapper.selectList(wrapper).stream().map(this::toCardVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipCardVO createCard(CardCreateDTO dto) {
        // 校验同一名称不重复
        long count = cardMapper.selectCount(new LambdaQueryWrapper<MembershipCardEntity>()
                .eq(MembershipCardEntity::getCardName, dto.getCardName()));
        if (count > 0) {
            throw new BusinessException(1001, "已存在同名卡种");
        }

        MembershipCardEntity entity = new MembershipCardEntity();
        entity.setCardName(dto.getCardName());
        entity.setCardType(dto.getCardType());
        entity.setDurationDays(dto.getDurationDays());
        entity.setTotalTimes(dto.getTotalTimes());
        entity.setPrice(dto.getPrice());
        entity.setStatus(1);
        cardMapper.insert(entity);

        log.info("卡种创建成功: id={}, name={}", entity.getId(), dto.getCardName());
        return toCardVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipCardVO updateCard(Long id, CardCreateDTO dto) {
        MembershipCardEntity entity = cardMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "卡种不存在");
        }

        // 校验名称唯一
        long count = cardMapper.selectCount(new LambdaQueryWrapper<MembershipCardEntity>()
                .eq(MembershipCardEntity::getCardName, dto.getCardName())
                .ne(MembershipCardEntity::getId, id));
        if (count > 0) {
            throw new BusinessException(1001, "已存在同名卡种");
        }

        entity.setCardName(dto.getCardName());
        entity.setCardType(dto.getCardType());
        entity.setDurationDays(dto.getDurationDays());
        entity.setTotalTimes(dto.getTotalTimes());
        entity.setPrice(dto.getPrice());
        cardMapper.updateById(entity);

        log.info("卡种更新成功: id={}", id);
        return toCardVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleCardStatus(Long id) {
        MembershipCardEntity entity = cardMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "卡种不存在");
        }
        int newStatus = entity.getStatus() == 1 ? 0 : 1;
        entity.setStatus(newStatus);
        cardMapper.updateById(entity);
        log.info("卡种状态切换: id={}, status={}", id, newStatus);
    }

    // ========== 会籍管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipVO assignMembership(MembershipCreateDTO dto) {
        MembershipCardEntity card = cardMapper.selectById(dto.getCardId());
        if (card == null) {
            throw new BusinessException(404, "卡种不存在");
        }
        if (card.getStatus() != 1) {
            throw new BusinessException(1001, "该卡种已下架");
        }

        // 检查是否有进行中的会籍
        long activeCount = membershipMapper.selectCount(new LambdaQueryWrapper<MemberMembershipEntity>()
                .eq(MemberMembershipEntity::getUserId, dto.getUserId())
                .in(MemberMembershipEntity::getStatus, "ACTIVE", "FROZEN"));
        if (activeCount > 0) {
            throw new BusinessException(1001, "该会员已有进行中的会籍，不能重复办理");
        }

        LocalDateTime now = LocalDateTime.now();
        MemberMembershipEntity entity = new MemberMembershipEntity();
        entity.setUserId(dto.getUserId());
        entity.setCardId(dto.getCardId());
        entity.setStartDate(now);
        entity.setStatus("ACTIVE");

        if ("TIMES".equals(card.getCardType())) {
            // 次卡：设置剩余次数
            entity.setRemainingTimes(card.getTotalTimes());
        } else {
            // 期限卡：计算到期日
            entity.setEndDate(now.plusDays(card.getDurationDays()));
        }

        membershipMapper.insert(entity);
        log.info("会籍办理成功: membershipId={}, userId={}, cardName={}",
                entity.getId(), dto.getUserId(), card.getCardName());

        return getMembershipDetail(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipVO renew(MembershipRenewDTO dto) {
        MemberMembershipEntity membership = membershipMapper.selectById(dto.getMembershipId());
        if (membership == null) {
            throw new BusinessException(404, "会籍不存在");
        }
        if (!"ACTIVE".equals(membership.getStatus()) && !"EXPIRED".equals(membership.getStatus())) {
            throw new BusinessException(1001, "当前状态不可续费");
        }

        MembershipCardEntity card = cardMapper.selectById(dto.getCardId());
        if (card == null) {
            throw new BusinessException(404, "卡种不存在");
        }

        if ("TIMES".equals(card.getCardType())) {
            // 次卡续费：增加剩余次数
            int newTimes = (membership.getRemainingTimes() != null ? membership.getRemainingTimes() : 0)
                    + card.getTotalTimes();
            membership.setRemainingTimes(newTimes);
            if (!"ACTIVE".equals(membership.getStatus())) {
                membership.setStatus("ACTIVE");
            }
        } else {
            // 期限卡续费：延长到期日
            LocalDateTime baseDate = membership.getEndDate() != null
                    && membership.getEndDate().isAfter(LocalDateTime.now())
                    ? membership.getEndDate() : LocalDateTime.now();
            membership.setEndDate(baseDate.plusDays(card.getDurationDays()));
            membership.setStatus("ACTIVE");
        }

        membershipMapper.updateById(membership);
        log.info("会籍续费成功: membershipId={}", dto.getMembershipId());

        return getMembershipDetail(dto.getMembershipId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freeze(Long membershipId) {
        MemberMembershipEntity entity = membershipMapper.selectById(membershipId);
        if (entity == null) {
            throw new BusinessException(404, "会籍不存在");
        }
        if (!"ACTIVE".equals(entity.getStatus())) {
            throw new BusinessException(1001, "仅活跃状态的会籍可冻结");
        }

        entity.setStatus("FROZEN");
        entity.setFrozenAt(LocalDateTime.now());
        membershipMapper.updateById(entity);
        log.info("会籍冻结成功: membershipId={}", membershipId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreeze(Long membershipId) {
        MemberMembershipEntity entity = membershipMapper.selectById(membershipId);
        if (entity == null) {
            throw new BusinessException(404, "会籍不存在");
        }
        if (!"FROZEN".equals(entity.getStatus())) {
            throw new BusinessException(1001, "仅冻结状态的会籍可解冻");
        }

        entity.setStatus("ACTIVE");
        entity.setFrozenAt(null);
        membershipMapper.updateById(entity);
        log.info("会籍解冻成功: membershipId={}", membershipId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long membershipId) {
        MemberMembershipEntity entity = membershipMapper.selectById(membershipId);
        if (entity == null) {
            throw new BusinessException(404, "会籍不存在");
        }
        if (!"ACTIVE".equals(entity.getStatus()) && !"FROZEN".equals(entity.getStatus())) {
            throw new BusinessException(1001, "该会籍已结束或已取消");
        }

        entity.setStatus("CANCELLED");
        membershipMapper.updateById(entity);
        log.info("会籍退卡成功: membershipId={}", membershipId);
    }

    @Override
    public List<MembershipVO> listByUser(Long userId) {
        return membershipMapper.selectMembershipVOByUserId(userId);
    }

    @Override
    public MembershipVO getActiveMembership(Long userId) {
        List<MembershipVO> list = membershipMapper.selectMembershipVOByUserId(userId);
        return list.stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()) || "FROZEN".equals(m.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void requireActiveMembership(Long userId) {
        MembershipVO active = getActiveMembership(userId);
        if (active == null) {
            throw new BusinessException(1001, "请先办理会籍后再使用此功能");
        }
    }

    @Override
    public List<MembershipVO> listAll() {
        return membershipMapper.selectAllMembershipVO();
    }

    private MembershipVO getMembershipDetail(Long id) {
        MemberMembershipEntity entity = membershipMapper.selectById(id);
        if (entity == null) return null;
        // 重新查 VO
        List<MembershipVO> list = membershipMapper.selectMembershipVOByUserId(entity.getUserId());
        return list.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    private MembershipCardVO toCardVO(MembershipCardEntity entity) {
        MembershipCardVO vo = new MembershipCardVO();
        vo.setId(entity.getId());
        vo.setCardName(entity.getCardName());
        vo.setCardType(entity.getCardType());
        vo.setDurationDays(entity.getDurationDays());
        vo.setTotalTimes(entity.getTotalTimes());
        vo.setPrice(entity.getPrice());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}
