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

-- 示例课程
INSERT INTO course (id, name, description, course_type, duration_minutes, max_capacity, price, status, created_at, updated_at) VALUES
(1000000000000000030, '基础瑜伽', '适合初学者的瑜伽课程，帮助放松身心', 'YOGA', 60, 20, 0.00, 1, NOW(), NOW()),
(1000000000000000031, '搏击操', '高强度的搏击训练，燃烧卡路里', 'BOXING', 45, 15, 0.00, 1, NOW(), NOW()),
(1000000000000000032, '动感单车', '跟随音乐节奏骑行，锻炼腿部力量', 'SPINNING', 45, 20, 0.00, 1, NOW(), NOW()),
(1000000000000000033, 'HIIT训练', '高强度间歇训练，快速燃脂', 'HIIT', 30, 12, 0.00, 1, NOW(), NOW()),
(1000000000000000034, '力量瑜伽', '进阶瑜伽课程，增强核心力量', 'YOGA', 60, 20, 0.00, 1, NOW(), NOW());

-- 示例动作（关联运动分类）
INSERT INTO exercise (id, category_id, name, description, muscle_group, difficulty, created_at, updated_at) VALUES
(1000000000000000040, 1000000000000000010, '杠铃卧推', '躺在卧推凳上，双手握杠铃推起', '胸大肌', 'INTERMEDIATE', NOW(), NOW()),
(1000000000000000041, 1000000000000000010, '哑铃飞鸟', '手持哑铃向两侧展开再收回', '胸大肌', 'BEGINNER', NOW(), NOW()),
(1000000000000000042, 1000000000000000011, '引体向上', '双手握杠，身体上拉至下巴过杠', '背阔肌', 'ADVANCED', NOW(), NOW()),
(1000000000000000043, 1000000000000000012, '深蹲', '双脚与肩同宽，屈膝下蹲', '股四头肌', 'BEGINNER', NOW(), NOW()),
(1000000000000000044, 1000000000000000016, '跑步', '在跑步机上进行有氧跑步', '全身', 'BEGINNER', NOW(), NOW());

-- 示例排课
INSERT INTO course_schedule (id, course_id, coach_id, schedule_date, start_time, end_time, location, max_capacity, status, created_at) VALUES
(1000000000000000050, 1000000000000000030, 1000000000000000002, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '10:00:00', '瑜伽室A', 20, 'UPCOMING', NOW()),
(1000000000000000051, 1000000000000000031, 1000000000000000002, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:30:00', '11:15:00', '搏击区', 15, 'UPCOMING', NOW()),
(1000000000000000052, 1000000000000000032, 1000000000000000002, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', '14:45:00', '单车房', 20, 'UPCOMING', NOW()),
(1000000000000000053, 1000000000000000033, 1000000000000000002, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:00:00', '18:30:00', '多功能训练区', 12, 'UPCOMING', NOW()),
(1000000000000000054, 1000000000000000034, 1000000000000000002, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00:00', '10:00:00', '瑜伽室B', 20, 'UPCOMING', NOW());

-- 示例会员卡种
INSERT INTO membership_card (id, card_name, card_type, duration_days, total_times, price, status, created_at, updated_at) VALUES
(1000000000000000020, '月卡', 'MONTH', 30, NULL, 299.00, 1, NOW(), NOW()),
(1000000000000000021, '季卡', 'QUARTER', 90, NULL, 799.00, 1, NOW(), NOW()),
(1000000000000000022, '年卡', 'YEAR', 365, NULL, 2599.00, 1, NOW(), NOW()),
(1000000000000000023, '次卡(10次)', 'TIMES', NULL, 10, 399.00, 1, NOW(), NOW()),
(1000000000000000024, '次卡(20次)', 'TIMES', NULL, 20, 699.00, 1, NOW(), NOW());
