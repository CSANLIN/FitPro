USE fitpro;

-- 训练模板表
CREATE TABLE IF NOT EXISTS workout_template (
    id           BIGINT       NOT NULL COMMENT '主键',
    name         VARCHAR(100) NOT NULL COMMENT '模板名称',
    description  VARCHAR(255) DEFAULT NULL COMMENT '描述',
    coach_id     BIGINT       DEFAULT NULL COMMENT '创建教练ID',
    target_type  VARCHAR(20)  DEFAULT NULL COMMENT '目标 FAT_LOSS/MUSCLE_GAIN/SHAPE',
    difficulty   VARCHAR(20)  DEFAULT NULL COMMENT '难度',
    created_at   DATETIME     NOT NULL COMMENT '创建时间',
    updated_at   DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练模板表';

-- 训练模板动作表
CREATE TABLE IF NOT EXISTS workout_template_item (
    id           BIGINT NOT NULL COMMENT '主键',
    template_id  BIGINT NOT NULL COMMENT '模板ID',
    exercise_id  BIGINT NOT NULL COMMENT '动作ID',
    sets         INT    NOT NULL DEFAULT 3 COMMENT '组数',
    reps         INT    NOT NULL DEFAULT 10 COMMENT '每组次数',
    rest_seconds INT    DEFAULT 60 COMMENT '组间休息秒数',
    sort_order   INT    NOT NULL DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (id),
    KEY idx_template_item_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练模板动作表';

-- 训练计划表
CREATE TABLE IF NOT EXISTS workout_plan (
    id          BIGINT       NOT NULL COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '会员ID',
    coach_id    BIGINT       DEFAULT NULL COMMENT '教练ID',
    name        VARCHAR(100) NOT NULL COMMENT '计划名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    start_date  DATE         NOT NULL COMMENT '开始日期',
    end_date    DATE         DEFAULT NULL COMMENT '结束日期',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 ACTIVE/COMPLETED/CANCELLED',
    created_at  DATETIME     NOT NULL COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_plan_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划表';

-- 训练计划-日表
CREATE TABLE IF NOT EXISTS workout_plan_day (
    id           BIGINT       NOT NULL COMMENT '主键',
    plan_id      BIGINT       NOT NULL COMMENT '计划ID',
    day_of_week  INT          NOT NULL COMMENT '星期几 1-7',
    name         VARCHAR(100) DEFAULT NULL COMMENT '训练日名称',
    template_id  BIGINT       DEFAULT NULL COMMENT '关联模板ID',
    PRIMARY KEY (id),
    KEY idx_plan_day_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划-日表';

-- 训练计划-日-动作表
CREATE TABLE IF NOT EXISTS workout_plan_day_item (
    id            BIGINT        NOT NULL COMMENT '主键',
    plan_day_id   BIGINT        NOT NULL COMMENT '训练日ID',
    exercise_id   BIGINT        NOT NULL COMMENT '动作ID',
    sets          INT           NOT NULL DEFAULT 3 COMMENT '组数',
    reps          INT           NOT NULL DEFAULT 10 COMMENT '每组次数',
    weight        DECIMAL(6, 2) DEFAULT NULL COMMENT '建议重量 kg',
    rest_seconds  INT           DEFAULT 60 COMMENT '组间休息秒数',
    sort_order    INT           NOT NULL DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (id),
    KEY idx_plan_day_item_day_id (plan_day_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练计划-日-动作表';

-- 训练记录表
CREATE TABLE IF NOT EXISTS workout_record (
    id               BIGINT       NOT NULL COMMENT '主键',
    user_id          BIGINT       NOT NULL COMMENT '会员ID',
    plan_day_id      BIGINT       DEFAULT NULL COMMENT '训练日ID',
    name             VARCHAR(100) NOT NULL COMMENT '训练名称',
    start_time       DATETIME     NOT NULL COMMENT '开始时间',
    end_time         DATETIME     DEFAULT NULL COMMENT '结束时间',
    duration_minutes INT          DEFAULT NULL COMMENT '时长(分钟)',
    total_volume     INT          DEFAULT 0 COMMENT '总训练量 kg',
    note             VARCHAR(255) DEFAULT NULL COMMENT '备注',
    created_at       DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_record_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练记录表';

-- 训练记录-组表
CREATE TABLE IF NOT EXISTS workout_record_item (
    id               BIGINT        NOT NULL COMMENT '主键',
    record_id        BIGINT        NOT NULL COMMENT '训练记录ID',
    exercise_id      BIGINT        NOT NULL COMMENT '动作ID',
    set_number       INT           NOT NULL COMMENT '第几组',
    reps             INT           DEFAULT NULL COMMENT '次数',
    weight           DECIMAL(6, 2) DEFAULT NULL COMMENT '重量 kg',
    duration_seconds INT           DEFAULT NULL COMMENT '时长(有氧用)',
    completed        TINYINT       NOT NULL DEFAULT 1 COMMENT '是否完成',
    PRIMARY KEY (id),
    KEY idx_record_item_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练记录-组表';
