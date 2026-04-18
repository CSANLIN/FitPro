USE fitpro;

-- 会员卡种表
CREATE TABLE IF NOT EXISTS membership_card (
    id             BIGINT        NOT NULL COMMENT '主键',
    card_name      VARCHAR(50)   NOT NULL COMMENT '卡种名称',
    card_type      VARCHAR(20)   NOT NULL COMMENT '类型 MONTH/QUARTER/YEAR/TIMES',
    duration_days  INT           DEFAULT NULL COMMENT '有效天数',
    total_times    INT           DEFAULT NULL COMMENT '总次数(次卡)',
    price          DECIMAL(10,2) NOT NULL COMMENT '价格',
    status         TINYINT       NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    created_at     DATETIME      NOT NULL COMMENT '创建时间',
    updated_at     DATETIME      NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡种表';

-- 会员会籍表
CREATE TABLE IF NOT EXISTS member_membership (
    id               BIGINT      NOT NULL COMMENT '主键',
    user_id          BIGINT      NOT NULL COMMENT '会员ID',
    card_id          BIGINT      NOT NULL COMMENT '卡种ID',
    start_date       DATETIME    NOT NULL COMMENT '开始日期',
    end_date         DATETIME    DEFAULT NULL COMMENT '到期日期',
    remaining_times  INT         DEFAULT NULL COMMENT '剩余次数',
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 ACTIVE/FROZEN/EXPIRED/CANCELLED',
    frozen_at        DATETIME    DEFAULT NULL COMMENT '冻结时间',
    created_at       DATETIME    NOT NULL COMMENT '创建时间',
    updated_at       DATETIME    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_membership_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员会籍表';

-- 签到记录表
CREATE TABLE IF NOT EXISTS check_in (
    id              BIGINT      NOT NULL COMMENT '主键',
    user_id         BIGINT      NOT NULL COMMENT '会员ID',
    membership_id   BIGINT      DEFAULT NULL COMMENT '会籍ID',
    check_in_time   DATETIME    NOT NULL COMMENT '签到时间',
    check_in_type   VARCHAR(20) NOT NULL DEFAULT 'MANUAL' COMMENT '签到方式 MANUAL/QR_CODE',
    created_at      DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_checkin_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';
