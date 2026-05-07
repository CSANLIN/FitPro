package com.fitness.module.membership.service;

import com.fitness.module.membership.dto.CardCreateDTO;
import com.fitness.module.membership.dto.MembershipCreateDTO;
import com.fitness.module.membership.dto.MembershipRenewDTO;
import com.fitness.module.membership.vo.MembershipCardVO;
import com.fitness.module.membership.vo.MembershipVO;

import java.util.List;

public interface MembershipService {

    // ========== 卡种管理 ==========

    List<MembershipCardVO> listCards(String keyword);

    MembershipCardVO createCard(CardCreateDTO dto);

    MembershipCardVO updateCard(Long id, CardCreateDTO dto);

    void toggleCardStatus(Long id);

    // ========== 会籍管理 ==========

    MembershipVO assignMembership(MembershipCreateDTO dto);

    MembershipVO renew(MembershipRenewDTO dto);

    void freeze(Long membershipId);

    void unfreeze(Long membershipId);

    void cancel(Long membershipId);

    List<MembershipVO> listByUser(Long userId);

    MembershipVO getActiveMembership(Long userId);

    /**
     * 检查用户是否有有效会籍，没有则抛出 BusinessException
     */
    void requireActiveMembership(Long userId);

    List<MembershipVO> listAll();
}
