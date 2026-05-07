package com.fitness.module.payment.controller;

import com.fitness.common.Result;
import com.fitness.module.payment.dto.PaymentCreateDTO;
import com.fitness.module.payment.service.PaymentService;
import com.fitness.module.payment.vo.PaymentResultVO;
import com.fitness.module.payment.vo.PaymentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "支付管理")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<PaymentVO> createOrder(@RequestBody @Valid PaymentCreateDTO dto) {
        Long userId = getCurrentUserId();
        return Result.success(paymentService.createOrder(userId, dto.getScheduleId()));
    }

    @PostMapping("/mock-pay/{orderNo}")
    @Operation(summary = "模拟支付")
    @PreAuthorize("hasRole('MEMBER')")
    public Result<PaymentResultVO> mockPay(@PathVariable String orderNo) {
        Long userId = getCurrentUserId();
        return Result.success(paymentService.mockPay(orderNo, userId));
    }

    @GetMapping("/status/{orderNo}")
    @Operation(summary = "查询支付状态")
    public Result<String> getStatus(@PathVariable String orderNo) {
        return Result.success(paymentService.getStatus(orderNo));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(principal.toString());
    }
}
