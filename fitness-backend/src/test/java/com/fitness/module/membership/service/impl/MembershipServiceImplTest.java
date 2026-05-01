package com.fitness.module.membership.service.impl;

import com.fitness.common.exception.BusinessException;
import com.fitness.module.membership.dto.CardCreateDTO;
import com.fitness.module.membership.dto.MembershipCreateDTO;
import com.fitness.module.membership.dto.MembershipRenewDTO;
import com.fitness.module.membership.entity.MemberMembershipEntity;
import com.fitness.module.membership.entity.MembershipCardEntity;
import com.fitness.module.membership.mapper.MemberMembershipMapper;
import com.fitness.module.membership.mapper.MembershipCardMapper;
import com.fitness.module.membership.vo.MembershipCardVO;
import com.fitness.module.membership.vo.MembershipVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {

    @Mock
    private MembershipCardMapper cardMapper;

    @Mock
    private MemberMembershipMapper membershipMapper;

    @InjectMocks
    private MembershipServiceImpl membershipService;

    @Captor
    private ArgumentCaptor<MemberMembershipEntity> membershipCaptor;

    @Captor
    private ArgumentCaptor<MembershipCardEntity> cardCaptor;

    // ========== 卡种管理测试 ==========

    @Test
    void createCard_ShouldSucceed() {
        CardCreateDTO dto = createCardDTO();
        when(cardMapper.selectCount(any())).thenReturn(0L);

        MembershipCardVO vo = membershipService.createCard(dto);

        assertNotNull(vo);
        assertEquals("月卡", vo.getCardName());
        verify(cardMapper).insert(any(MembershipCardEntity.class));
    }

    @Test
    void createCard_ShouldThrow_WhenNameDuplicate() {
        CardCreateDTO dto = createCardDTO();
        when(cardMapper.selectCount(any())).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.createCard(dto));
        assertEquals(1001, ex.getCode());
        assertEquals("已存在同名卡种", ex.getMessage());
    }

    @Test
    void listCards_ShouldReturnList_WithKeyword() {
        MembershipCardEntity entity = createCardEntity();
        when(cardMapper.selectList(any())).thenReturn(Collections.singletonList(entity));

        List<MembershipCardVO> result = membershipService.listCards("月卡");

        assertEquals(1, result.size());
        assertEquals("月卡", result.get(0).getCardName());
    }

    @Test
    void listCards_ShouldReturnAll_WithoutKeyword() {
        MembershipCardEntity entity = createCardEntity();
        when(cardMapper.selectList(any())).thenReturn(Collections.singletonList(entity));

        List<MembershipCardVO> result = membershipService.listCards(null);

        assertEquals(1, result.size());
    }

    @Test
    void updateCard_ShouldSucceed() {
        MembershipCardEntity existing = createCardEntity();
        when(cardMapper.selectById(1L)).thenReturn(existing);
        when(cardMapper.selectCount(any())).thenReturn(0L);

        CardCreateDTO dto = createCardDTO();
        dto.setCardName("新名称");

        MembershipCardVO result = membershipService.updateCard(1L, dto);

        assertNotNull(result);
        verify(cardMapper).updateById(any(MembershipCardEntity.class));
    }

    @Test
    void updateCard_ShouldThrow_WhenNotFound() {
        when(cardMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.updateCard(999L, createCardDTO()));
        assertEquals(404, ex.getCode());
    }

    @Test
    void toggleCardStatus_ShouldToggleFromOnToOff() {
        MembershipCardEntity entity = createCardEntity();
        entity.setStatus(1);
        when(cardMapper.selectById(1L)).thenReturn(entity);

        membershipService.toggleCardStatus(1L);

        verify(cardMapper).updateById(cardCaptor.capture());
        assertEquals(0, cardCaptor.getValue().getStatus());
    }

    @Test
    void toggleCardStatus_ShouldToggleFromOffToOn() {
        MembershipCardEntity entity = createCardEntity();
        entity.setStatus(0);
        when(cardMapper.selectById(1L)).thenReturn(entity);

        membershipService.toggleCardStatus(1L);

        verify(cardMapper).updateById(cardCaptor.capture());
        assertEquals(1, cardCaptor.getValue().getStatus());
    }

    @Test
    void toggleCardStatus_ShouldThrow_WhenNotFound() {
        when(cardMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.toggleCardStatus(999L));
        assertEquals(404, ex.getCode());
    }

    // ========== 办理会籍测试 ==========

    @Test
    void assignMembership_ShouldSucceed_ForTimedCard() {
        MembershipCardEntity card = createCardEntity();
        when(cardMapper.selectById(1L)).thenReturn(card);
        when(membershipMapper.selectCount(any())).thenReturn(0L);

        // 模拟 insert 后会籍带 ID
        doAnswer(invocation -> {
            MemberMembershipEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        }).when(membershipMapper).insert(any(MemberMembershipEntity.class));

        // getMembershipDetail 需要根据 ID 查询回实体，再查 VO
        when(membershipMapper.selectById(100L)).thenAnswer(inv -> {
            MemberMembershipEntity e = new MemberMembershipEntity();
            e.setId(100L);
            e.setUserId(200L);
            return e;
        });
        MembershipVO mockVO = new MembershipVO();
        mockVO.setId(100L);
        mockVO.setCardName("月卡");
        mockVO.setCardType("MONTH");
        mockVO.setStatus("ACTIVE");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(mockVO));

        MembershipCreateDTO dto = new MembershipCreateDTO();
        dto.setUserId(200L);
        dto.setCardId(1L);

        MembershipVO result = membershipService.assignMembership(dto);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(membershipMapper).insert(any(MemberMembershipEntity.class));
    }

    @Test
    void assignMembership_ShouldSetRemainingTimes_ForTimesCard() {
        MembershipCardEntity card = createCardEntity();
        card.setCardType("TIMES");
        card.setDurationDays(null);
        card.setTotalTimes(30);
        when(cardMapper.selectById(1L)).thenReturn(card);
        when(membershipMapper.selectCount(any())).thenReturn(0L);

        // 模拟 insert 后会籍带 ID
        doAnswer(invocation -> {
            MemberMembershipEntity entity = invocation.getArgument(0);
            entity.setId(101L);
            return 1;
        }).when(membershipMapper).insert(any(MemberMembershipEntity.class));

        // getMembershipDetail 返回 mock
        when(membershipMapper.selectById(101L)).thenAnswer(inv -> {
            MemberMembershipEntity e = new MemberMembershipEntity();
            e.setId(101L);
            e.setUserId(200L);
            return e;
        });
        MembershipVO mockVO = new MembershipVO();
        mockVO.setId(101L);
        mockVO.setCardName("次卡");
        mockVO.setCardType("TIMES");
        mockVO.setRemainingTimes(30);
        mockVO.setStatus("ACTIVE");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(mockVO));

        MembershipCreateDTO dto = new MembershipCreateDTO();
        dto.setUserId(200L);
        dto.setCardId(1L);

        membershipService.assignMembership(dto);

        verify(membershipMapper).insert(membershipCaptor.capture());
        assertEquals(30, membershipCaptor.getValue().getRemainingTimes());
        assertNull(membershipCaptor.getValue().getEndDate());
    }

    @Test
    void assignMembership_ShouldThrow_WhenCardNotFound() {
        when(cardMapper.selectById(999L)).thenReturn(null);

        MembershipCreateDTO dto = new MembershipCreateDTO();
        dto.setUserId(200L);
        dto.setCardId(999L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.assignMembership(dto));
        assertEquals(404, ex.getCode());
    }

    @Test
    void assignMembership_ShouldThrow_WhenCardOffline() {
        MembershipCardEntity card = createCardEntity();
        card.setStatus(0);
        when(cardMapper.selectById(1L)).thenReturn(card);

        MembershipCreateDTO dto = new MembershipCreateDTO();
        dto.setUserId(200L);
        dto.setCardId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.assignMembership(dto));
        assertEquals(1001, ex.getCode());
    }

    @Test
    void assignMembership_ShouldThrow_WhenActiveMembershipExists() {
        MembershipCardEntity card = createCardEntity();
        when(cardMapper.selectById(1L)).thenReturn(card);
        when(membershipMapper.selectCount(any())).thenReturn(1L);

        MembershipCreateDTO dto = new MembershipCreateDTO();
        dto.setUserId(200L);
        dto.setCardId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.assignMembership(dto));
        assertEquals(1001, ex.getCode());
    }

    // ========== 续费测试 ==========

    @Test
    void renew_ShouldExtendEndDate_ForTimedCard() {
        MemberMembershipEntity membership = createTimedMemberMembership();
        MembershipCardEntity renewCard = createCardEntity();

        when(membershipMapper.selectById(100L)).thenReturn(membership);
        when(cardMapper.selectById(1L)).thenReturn(renewCard);
        MembershipVO vo = new MembershipVO();
        vo.setId(100L);
        vo.setStatus("ACTIVE");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(vo));

        MembershipRenewDTO dto = new MembershipRenewDTO();
        dto.setMembershipId(100L);
        dto.setCardId(1L);

        MembershipVO result = membershipService.renew(dto);

        assertNotNull(result);
        verify(membershipMapper).updateById(any(MemberMembershipEntity.class));
    }

    @Test
    void renew_ShouldAddTimes_ForTimesCard() {
        MemberMembershipEntity membership = createTimesMemberMembership();
        membership.setRemainingTimes(5);
        MembershipCardEntity renewCard = createCardEntity();
        renewCard.setCardType("TIMES");
        renewCard.setTotalTimes(10);

        when(membershipMapper.selectById(100L)).thenReturn(membership);
        when(cardMapper.selectById(2L)).thenReturn(renewCard);
        MembershipVO vo = new MembershipVO();
        vo.setId(100L);
        vo.setStatus("ACTIVE");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(vo));

        MembershipRenewDTO dto = new MembershipRenewDTO();
        dto.setMembershipId(100L);
        dto.setCardId(2L);

        membershipService.renew(dto);

        verify(membershipMapper).updateById(membershipCaptor.capture());
        assertEquals(Integer.valueOf(15), membershipCaptor.getValue().getRemainingTimes());
    }

    @Test
    void renew_ShouldThrow_WhenMembershipNotFound() {
        when(membershipMapper.selectById(999L)).thenReturn(null);

        MembershipRenewDTO dto = new MembershipRenewDTO();
        dto.setMembershipId(999L);
        dto.setCardId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.renew(dto));
        assertEquals(404, ex.getCode());
    }

    // ========== 冻结/解冻测试 ==========

    @Test
    void freeze_ShouldSucceed_WhenActive() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("ACTIVE");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        membershipService.freeze(100L);

        verify(membershipMapper).updateById(membershipCaptor.capture());
        assertEquals("FROZEN", membershipCaptor.getValue().getStatus());
    }

    @Test
    void freeze_ShouldThrow_WhenNotActive() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("FROZEN");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.freeze(100L));
        assertEquals(1001, ex.getCode());
    }

    @Test
    void unfreeze_ShouldSucceed_WhenFrozen() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("FROZEN");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        membershipService.unfreeze(100L);

        verify(membershipMapper).updateById(membershipCaptor.capture());
        assertEquals("ACTIVE", membershipCaptor.getValue().getStatus());
    }

    @Test
    void unfreeze_ShouldThrow_WhenNotFrozen() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("ACTIVE");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.unfreeze(100L));
        assertEquals(1001, ex.getCode());
    }

    // ========== 退卡测试 ==========

    @Test
    void cancel_ShouldSucceed_WhenActive() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("ACTIVE");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        membershipService.cancel(100L);

        verify(membershipMapper).updateById(membershipCaptor.capture());
        assertEquals("CANCELLED", membershipCaptor.getValue().getStatus());
    }

    @Test
    void cancel_ShouldThrow_WhenExpired() {
        MemberMembershipEntity entity = createTimedMemberMembership();
        entity.setStatus("EXPIRED");
        when(membershipMapper.selectById(100L)).thenReturn(entity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> membershipService.cancel(100L));
        assertEquals(1001, ex.getCode());
    }

    // ========== 查询测试 ==========

    @Test
    void listByUser_ShouldReturnList() {
        MembershipVO vo = new MembershipVO();
        vo.setUserId(200L);
        vo.setStatus("ACTIVE");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(vo));

        List<MembershipVO> result = membershipService.listByUser(200L);

        assertEquals(1, result.size());
    }

    @Test
    void listAll_ShouldReturnList() {
        MembershipVO vo = new MembershipVO();
        vo.setStatus("ACTIVE");
        when(membershipMapper.selectAllMembershipVO())
                .thenReturn(Collections.singletonList(vo));

        List<MembershipVO> result = membershipService.listAll();

        assertEquals(1, result.size());
    }

    @Test
    void getActiveMembership_ShouldReturnActive_WhenBothActiveAndFrozen() {
        MembershipVO active = new MembershipVO();
        active.setStatus("ACTIVE");
        MembershipVO frozen = new MembershipVO();
        frozen.setStatus("FROZEN");
        // 查询结果按 created_at 降序，最新的在前面
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(List.of(active, frozen));

        MembershipVO result = membershipService.getActiveMembership(200L);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void getActiveMembership_ShouldReturnNull_WhenNoneActive() {
        MembershipVO expired = new MembershipVO();
        expired.setStatus("EXPIRED");
        when(membershipMapper.selectMembershipVOByUserId(200L))
                .thenReturn(Collections.singletonList(expired));

        MembershipVO result = membershipService.getActiveMembership(200L);

        assertNull(result);
    }

    // ========== 工具方法 ==========

    private CardCreateDTO createCardDTO() {
        CardCreateDTO dto = new CardCreateDTO();
        dto.setCardName("月卡");
        dto.setCardType("MONTH");
        dto.setDurationDays(30);
        dto.setPrice(new BigDecimal("299.00"));
        return dto;
    }

    private MembershipCardEntity createCardEntity() {
        MembershipCardEntity entity = new MembershipCardEntity();
        entity.setId(1L);
        entity.setCardName("月卡");
        entity.setCardType("MONTH");
        entity.setDurationDays(30);
        entity.setPrice(new BigDecimal("299.00"));
        entity.setStatus(1);
        return entity;
    }

    private MemberMembershipEntity createTimedMemberMembership() {
        MemberMembershipEntity entity = new MemberMembershipEntity();
        entity.setId(100L);
        entity.setUserId(200L);
        entity.setCardId(1L);
        entity.setStartDate(LocalDateTime.now().minusDays(5));
        entity.setEndDate(LocalDateTime.now().plusDays(25));
        entity.setStatus("ACTIVE");
        return entity;
    }

    private MemberMembershipEntity createTimesMemberMembership() {
        MemberMembershipEntity entity = new MemberMembershipEntity();
        entity.setId(100L);
        entity.setUserId(200L);
        entity.setCardId(2L);
        entity.setStartDate(LocalDateTime.now());
        entity.setRemainingTimes(30);
        entity.setStatus("ACTIVE");
        return entity;
    }
}
