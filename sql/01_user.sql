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
