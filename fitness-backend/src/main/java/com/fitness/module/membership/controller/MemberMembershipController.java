package com.fitness.module.membership.controller;

import com.fitness.common.Result;
import com.fitness.module.membership.dto.MembershipCreateDTO;
import com.fitness.module.membership.dto.MembershipRenewDTO;
import com.fitness.module.membership.service.MembershipService;
import com.fitness.module.membership.vo.MembershipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "会籍管理")
public class MemberMembershipController {

    private final MembershipService membershipService;

    @GetMapping("/my")
    @Operation(summary = "获取我的会籍列表（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<List<MembershipVO>> myMemberships() {
        Long userId = getCurrentUserId();
        return Result.success(membershipService.listByUser(userId));
    }

    @GetMapping("/my/active")
    @Operation(summary = "获取当前活跃会籍（会员端）")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<MembershipVO> myActiveMembership() {
        Long userId = getCurrentUserId();
        MembershipVO vo = membershipService.getActiveMembership(userId);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "获取全部会籍（管理端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<MembershipVO>> listAll() {
        return Result.success(membershipService.listAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查看指定会员的会籍（管理端）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COACH')")
    public Result<List<MembershipVO>> listByUser(@PathVariable Long userId) {
        return Result.success(membershipService.listByUser(userId));
    }

    @PostMapping
    @Operation(summary = "办理会籍（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<MembershipVO> assign(@RequestBody @Valid MembershipCreateDTO dto) {
        return Result.success(membershipService.assignMembership(dto));
    }

    @PutMapping("/renew")
    @Operation(summary = "续费会籍（管理端）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<MembershipVO> renew(@RequestBody @Valid MembershipRenewDTO dto) {
        return Result.success(membershipService.renew(dto));
    }

    @PutMapping("/{id}/freeze")
    @Operation(summary = "冻结会籍")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> freeze(@PathVariable Long id) {
        membershipService.freeze(id);
        return Result.success();
    }

    @PutMapping("/{id}/unfreeze")
    @Operation(summary = "解冻会籍")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> unfreeze(@PathVariable Long id) {
        membershipService.unfreeze(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "退卡/取消会籍")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> cancel(@PathVariable Long id) {
        membershipService.cancel(id);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
