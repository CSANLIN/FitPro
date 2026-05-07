package com.fitness.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.module.payment.entity.PaymentOrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrderEntity> {
}
