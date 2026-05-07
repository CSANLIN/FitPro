USE fitpro;

-- 支付订单表
CREATE TABLE IF NOT EXISTS payment_order (
    id              BIGINT         NOT NULL COMMENT '主键',
    order_no        VARCHAR(64)    NOT NULL COMMENT '订单编号',
    user_id         BIGINT         NOT NULL COMMENT '用户ID',
    schedule_id     BIGINT         DEFAULT NULL COMMENT '排课ID（课程预约支付）',
    amount          DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING' COMMENT '状态 PENDING/SUCCESS/FAILED/REFUNDED',
    paid_at         DATETIME       DEFAULT NULL COMMENT '支付时间',
    deleted         TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    created_at      DATETIME       NOT NULL COMMENT '创建时间',
    updated_at      DATETIME       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_payment_order_no (order_no),
    KEY idx_payment_user_id (user_id),
    KEY idx_payment_schedule_id (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
