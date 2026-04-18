# FitPro 数据库导入指南

## 已生成文件

- `all_tables.sql` — 合并的SQL文件，包含所有表结构和种子数据
- `import-database.ps1` — PowerShell一键导入脚本
- `import-database.bat` — 批处理一键导入脚本

## 方法一：使用一键导入脚本（推荐）

### PowerShell脚本 (Windows 10/11)
1. **确保MySQL服务正在运行**
   ```cmd
   net start mysql
   ```
   或在"服务"应用中启动MySQL服务

2. **运行导入脚本**
   - 右键点击 `import-database.ps1` → 选择"使用PowerShell运行"
   - 或打开PowerShell，切换到项目目录，执行：
     ```powershell
     .\import-database.ps1
     ```

3. **输入MySQL密码**
   - 密码：`Nan123123`（从 application-dev.yml 读取）
   - 用户名：`root`

### 批处理脚本 (兼容旧版Windows)
1. 双击 `import-database.bat`
2. 按照提示操作

## 方法二：使用Navicat手动导入

### 步骤1：连接MySQL
1. 打开Navicat for MySQL
2. 点击"连接" → MySQL
3. 填写连接信息：
   - 连接名: `FitPro_Local`
   - 主机: `localhost` 或 `127.0.0.1`
   - 端口: `3306`
   - 用户名: `root`
   - 密码: `Nan123123`
4. 点击"测试连接"，确认连接成功

### 步骤2：运行SQL文件
1. 右键点击新建的连接 → "运行SQL文件"
2. 点击"..."按钮，选择 `all_tables.sql`
3. 目标数据库保持为空（脚本会创建 `fitpro` 数据库）
4. 编码选择：`UTF-8`
5. 点击"开始"执行导入

### 步骤3：验证导入结果
1. 刷新连接，查看 `fitpro` 数据库
2. 展开数据库，应看到19张表：
   - `sys_user` (用户表)
   - `body_record` (身体数据)
   - `membership_card` (会员卡种)
   - `member_membership` (会籍)
   - `check_in` (签到)
   - `exercise_category` (运动分类)
   - `exercise` (运动动作)
   - 训练模块的7张表
   - 课程模块的3张表
   - 系统模块的2张表

## 方法三：使用MySQL命令行

```bash
# 切换到项目目录
cd F:\Project

# 导入数据库（使用Nan123123密码）
mysql -u root -pNan123123 < all_tables.sql

# 或交互式输入密码
mysql -u root -p < all_tables.sql
# 然后输入密码：Nan123123
```

## 导入内容概览

### 数据库结构
- 数据库名: `fitpro`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_general_ci`

### 表结构 (19张表)
1. **用户模块** (2张)
   - `sys_user` — 用户信息，支持角色：SUPER_ADMIN/COACH/MEMBER
   - `body_record` — 身体数据记录

2. **会籍模块** (3张)
   - `membership_card` — 卡种定义（月卡、季卡、年卡、次卡）
   - `member_membership` — 会员会籍记录
   - `check_in` — 签到记录

3. **运动库模块** (2张)
   - `exercise_category` — 运动分类（胸部、背部、腿部等）
   - `exercise` — 具体运动动作

4. **训练模块** (7张)
   - `workout_template` — 训练模板
   - `workout_template_item` — 模板项
   - `workout_plan` — 训练计划
   - `workout_plan_day` — 计划日期
   - `workout_plan_day_item` — 计划项
   - `workout_record` — 训练记录
   - `workout_record_item` — 记录项

5. **课程模块** (3张)
   - `course` — 课程定义
   - `course_schedule` — 排课
   - `course_booking` — 预约（防重唯一索引）

6. **系统模块** (2张)
   - `sys_announcement` — 公告
   - `sys_operation_log` — 操作日志

### 种子数据
- **管理员账号** (密码使用BCrypt加密)
  - 超级管理员: `admin` / `admin123`
  - 教练账号: `coach01` / `coach123`

- **运动分类** (7个)
  - 胸部、背部、腿部、肩部、手臂、核心、有氧

- **会员卡种** (5种)
  - 月卡 (¥299/30天)
  - 季卡 (¥799/90天)
  - 年卡 (¥2599/365天)
  - 次卡10次 (¥399)
  - 次卡20次 (¥699)

## 常见问题

### Q1: 导入时提示"Access denied"
- 检查MySQL密码是否正确
- 尝试使用正确的密码：`mysql -u root -p你的密码`

### Q2: 数据库已存在怎么办？
脚本使用 `CREATE DATABASE IF NOT EXISTS`，不会覆盖现有数据。
如果需要重新创建，先手动删除数据库：
```sql
DROP DATABASE IF EXISTS fitpro;
```

### Q3: Navicat执行SQL文件时卡住？
- 检查SQL文件大小（应该不大）
- 尝试分步执行：先执行建表，再执行种子数据
- 或使用命令行导入

### Q4: 表中没有数据？
种子数据在 `07_seed_data.sql` 中，确保完整执行了 `all_tables.sql`

### Q5: MySQL服务无法启动？
- 检查是否已安装MySQL
- 检查端口3306是否被占用
- 查看MySQL错误日志

## 验证导入成功

1. **查询用户表**
   ```sql
   USE fitpro;
   SELECT id, username, nickname, role FROM sys_user;
   ```
   应看到2条记录：admin 和 coach01

2. **查看所有表**
   ```sql
   SHOW TABLES;
   ```
   应显示19张表

3. **检查运动分类**
   ```sql
   SELECT * FROM exercise_category ORDER BY sort_order;
   ```
   应显示7个分类

## 下一步

数据库导入成功后，可以启动应用：

1. **启动后端**
   ```bash
   cd fitness-backend
   mvn spring-boot:run
   ```

2. **启动前端**
   ```bash
   cd fitness-frontend
   npm run dev
   ```

3. **访问应用**
   - 前端: http://localhost:5173
   - 后端API文档: http://localhost:8080/doc.html

## 联系支持

如果遇到问题，请检查：
1. MySQL服务状态
2. 连接参数是否正确
3. SQL文件完整性

或查看项目文档：
- `docs/dev_log.md` — 开发日志
- `docs/engineering_tasks.md` — 任务清单
- `docs/learning_log.md` — 学习日志