package com.fitness.module.payment.service;

import com.fitness.module.payment.vo.PaymentResultVO;
import com.fitness.module.payment.vo.PaymentVO;

public interface PaymentService {

    PaymentVO createOrder(Long userId, Long scheduleId);

    PaymentResultVO mockPay(String orderNo, Long userId);

    String getStatus(String orderNo);
}
