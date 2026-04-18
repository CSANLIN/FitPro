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
