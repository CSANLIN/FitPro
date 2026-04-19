-- FitPro 数据库更新脚本
-- 修复管理员和教练账户状态问题（状态从1改为0）

USE fitpro;

-- 更新超级管理员账号状态为正常（0）
UPDATE sys_user SET status = 0 WHERE id = 1000000000000000001;

-- 更新示例教练账号状态为正常（0）
UPDATE sys_user SET status = 0 WHERE id = 1000000000000000002;

-- 验证更新结果
SELECT id, username, role, status FROM sys_user WHERE id IN (1000000000000000001, 1000000000000000002);