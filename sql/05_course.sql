USE fitness_db;

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
