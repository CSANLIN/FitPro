package com.fitness.module.membership.controller;

import com.fitness.common.Result;
import com.fitness.module.membership.dto.CardCreateDTO;
import com.fitness.module.membership.service.MembershipService;
import com.fitness.module.membership.vo.MembershipCardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-cards")
@RequiredArgsConstructor
@Tag(name = "会员卡种管理")
public class MembershipCardController {

    private final MembershipService membershipService;

    @GetMapping
    @Operation(summary = "获取卡种列表")
    public Result<List<MembershipCardVO>> list(@RequestParam(required = false) String keyword) {
        return Result.success(membershipService.listCards(keyword));
    }

    @PostMapping
    @Operation(summary = "创建卡种")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<MembershipCardVO> create(@RequestBody @Valid CardCreateDTO dto) {
        return Result.success(membershipService.createCard(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新卡种")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<MembershipCardVO> update(@PathVariable Long id, @RequestBody @Valid CardCreateDTO dto) {
        return Result.success(membershipService.updateCard(id, dto));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "切换卡种上下架")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        membershipService.toggleCardStatus(id);
        return Result.success();
    }
}
