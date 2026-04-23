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
