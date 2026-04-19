-- 创建数据库
CREATE DATABASE IF NOT EXISTS fitpro
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE fitpro;
USE fitpro;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id           BIGINT       NOT NULL COMMENT '主键',
    username     VARCHAR(50)  NOT NULL COMMENT '用户名',
    password     VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
    nickname     VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar       VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    email        VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone        VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    gender       TINYINT      DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    birthday     DATE         DEFAULT NULL COMMENT '生日',
    role         VARCHAR(20)  NOT NULL DEFAULT 'MEMBER' COMMENT '角色 SUPER_ADMIN/COACH/MEMBER',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    created_at   DATETIME     NOT NULL COMMENT '创建时间',
    updated_at   DATETIME     NOT NULL COMMENT '更新时间',
    deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    UNIQUE KEY idx_user_username (username),
    KEY idx_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 身体数据记录表
CREATE TABLE IF NOT EXISTS body_record (
    id          BIGINT         NOT NULL COMMENT '主键',
    user_id     BIGINT         NOT NULL COMMENT '用户ID',
    weight      DECIMAL(5, 2)  DEFAULT NULL COMMENT '体重 kg',
    height      DECIMAL(5, 2)  DEFAULT NULL COMMENT '身高 cm',
    body_fat    DECIMAL(5, 2)  DEFAULT NULL COMMENT '体脂率 %',
    bmi         DECIMAL(5, 2)  DEFAULT NULL COMMENT 'BMI',
    chest       DECIMAL(5, 2)  DEFAULT NULL COMMENT '胸围 cm',
    waist       DECIMAL(5, 2)  DEFAULT NULL COMMENT '腰围 cm',
    hip         DECIMAL(5, 2)  DEFAULT NULL COMMENT '臀围 cm',
    record_date DATE           NOT NULL COMMENT '记录日期',
    remark      VARCHAR(255)   DEFAULT NULL COMMENT '备注',
    created_at  DATETIME       NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_body_record_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='身体数据记录表';
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
USE fitpro;

-- 运动分类表
CREATE TABLE IF NOT EXISTS exercise_category (
    id          BIGINT      NOT NULL COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    icon        VARCHAR(100) DEFAULT NULL COMMENT '图标',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序',
    created_at  DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动分类表';

-- 运动动作表
CREATE TABLE IF NOT EXISTS exercise (
    id           BIGINT       NOT NULL COMMENT '主键',
    category_id  BIGINT       NOT NULL COMMENT '分类ID',
    name         VARCHAR(100) NOT NULL COMMENT '动作名称',
    description  TEXT         DEFAULT NULL COMMENT '动作描述',
    muscle_group VARCHAR(100) DEFAULT NULL COMMENT '目标肌群',
    equipment    VARCHAR(100) DEFAULT NULL COMMENT '所需器械',
    difficulty   VARCHAR(20)  NOT NULL DEFAULT 'BEGINNER' COMMENT '难度 BEGINNER/INTERMEDIATE/ADVANCED',
    video_url    VARCHAR(255) DEFAULT NULL COMMENT '教学视频URL',
    image_url    VARCHAR(255) DEFAULT NULL COMMENT '示意图URL',
    created_at   DATETIME     NOT NULL COMMENT '创建时间',
    updated_at   DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_exercise_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动动作表';
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
USE fitpro;

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id               BIGINT       NOT NULL COMMENT '主键',
    name             VARCHAR(100) NOT NULL COMMENT '课程名称',
    description      TEXT         DEFAULT NULL COMMENT '课程描述',
    cover_image      VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    course_type      VARCHAR(20)  NOT NULL DEFAULT 'OTHER' COMMENT '类型 YOGA/BOXING/SPINNING/HIIT/OTHER',
    duration_minutes INT          NOT NULL COMMENT '课程时长(分钟)',
    max_capacity     INT          NOT NULL DEFAULT 20 COMMENT '最大容量',
    status           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    created_at       DATETIME     NOT NULL COMMENT '创建时间',
    updated_at       DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 排课表
CREATE TABLE IF NOT EXISTS course_schedule (
    id             BIGINT      NOT NULL COMMENT '主键',
    course_id      BIGINT      NOT NULL COMMENT '课程ID',
    coach_id       BIGINT      NOT NULL COMMENT '教练ID',
    schedule_date  DATE        NOT NULL COMMENT '排课日期',
    start_time     TIME        NOT NULL COMMENT '开始时间',
    end_time       TIME        NOT NULL COMMENT '结束时间',
    location       VARCHAR(100) DEFAULT NULL COMMENT '上课地点',
    current_count  INT         NOT NULL DEFAULT 0 COMMENT '当前预约人数',
    max_capacity   INT         NOT NULL COMMENT '最大容量',
    status         VARCHAR(20) NOT NULL DEFAULT 'UPCOMING' COMMENT '状态 UPCOMING/ONGOING/FINISHED/CANCELLED',
    created_at     DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_schedule_date (schedule_date),
    KEY idx_schedule_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课表';

-- 课程预约表
CREATE TABLE IF NOT EXISTS course_booking (
    id            BIGINT      NOT NULL COMMENT '主键',
    user_id       BIGINT      NOT NULL COMMENT '会员ID',
    schedule_id   BIGINT      NOT NULL COMMENT '排课ID',
    status        VARCHAR(20) NOT NULL DEFAULT 'BOOKED' COMMENT '状态 BOOKED/CANCELLED/ATTENDED/ABSENT',
    booked_at     DATETIME    NOT NULL COMMENT '预约时间',
    cancelled_at  DATETIME    DEFAULT NULL COMMENT '取消时间',
    created_at    DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_booking_user_schedule (user_id, schedule_id),
    KEY idx_booking_user_id (user_id),
    KEY idx_booking_schedule_id (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程预约表';
USE fitpro;

-- 系统公告表
CREATE TABLE IF NOT EXISTS sys_announcement (
    id          BIGINT       NOT NULL COMMENT '主键',
    title       VARCHAR(100) NOT NULL COMMENT '标题',
    content     TEXT         NOT NULL COMMENT '内容',
    type        VARCHAR(20)  NOT NULL DEFAULT 'NOTICE' COMMENT '类型 NOTICE/ACTIVITY/MAINTENANCE',
    is_top      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0草稿 1发布',
    created_by  BIGINT       NOT NULL COMMENT '创建人ID',
    publish_at  DATETIME     DEFAULT NULL COMMENT '发布时间',
    created_at  DATETIME     NOT NULL COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_announcement_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id          BIGINT       NOT NULL COMMENT '主键',
    user_id     BIGINT       DEFAULT NULL COMMENT '操作人ID',
    username    VARCHAR(50)  DEFAULT NULL COMMENT '操作人用户名',
    module      VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
    operation   VARCHAR(50)  DEFAULT NULL COMMENT '操作类型',
    method      VARCHAR(10)  DEFAULT NULL COMMENT '请求方法',
    url         VARCHAR(255) DEFAULT NULL COMMENT '请求URL',
    ip          VARCHAR(50)  DEFAULT NULL COMMENT 'IP地址',
    duration    INT          DEFAULT NULL COMMENT '耗时ms',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0失败 1成功',
    params      TEXT         DEFAULT NULL COMMENT '请求参数',
    result      TEXT         DEFAULT NULL COMMENT '返回结果',
    created_at  DATETIME     NOT NULL COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_operation_log_user_id (user_id),
    KEY idx_operation_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
USE fitpro;

-- 超级管理员账号 (密码: admin123)
INSERT INTO sys_user (id, username, password, nickname, role, status, created_at, updated_at, deleted)
VALUES (
    1000000000000000001,
    'admin',
    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
    '超级管理员',
    'SUPER_ADMIN',
    0,
    NOW(),
    NOW(),
    0
);

-- 示例教练账号 (密码: coach123)
INSERT INTO sys_user (id, username, password, nickname, role, status, created_at, updated_at, deleted)
VALUES (
    1000000000000000002,
    'coach01',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8RDkfAO.NAVHnko.Oy',
    '张教练',
    'COACH',
    0,
    NOW(),
    NOW(),
    0
);

-- 运动分类种子数据
INSERT INTO exercise_category (id, name, icon, sort_order, created_at) VALUES
(1000000000000000010, '胸部', 'chest', 1, NOW()),
(1000000000000000011, '背部', 'back', 2, NOW()),
(1000000000000000012, '腿部', 'legs', 3, NOW()),
(1000000000000000013, '肩部', 'shoulder', 4, NOW()),
(1000000000000000014, '手臂', 'arms', 5, NOW()),
(1000000000000000015, '核心', 'core', 6, NOW()),
(1000000000000000016, '有氧', 'cardio', 7, NOW());

-- 示例会员卡种
INSERT INTO membership_card (id, card_name, card_type, duration_days, total_times, price, status, created_at, updated_at) VALUES
(1000000000000000020, '月卡', 'MONTH', 30, NULL, 299.00, 1, NOW(), NOW()),
(1000000000000000021, '季卡', 'QUARTER', 90, NULL, 799.00, 1, NOW(), NOW()),
(1000000000000000022, '年卡', 'YEAR', 365, NULL, 2599.00, 1, NOW(), NOW()),
(1000000000000000023, '次卡(10次)', 'TIMES', NULL, 10, 399.00, 1, NOW(), NOW()),
(1000000000000000024, '次卡(20次)', 'TIMES', NULL, 20, 699.00, 1, NOW(), NOW());
