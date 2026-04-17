USE fitness_db;

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
