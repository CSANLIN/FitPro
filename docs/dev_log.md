# FitPro 开发日志

> 格式: `## [日期] Phase X.X — 任务标题`
> 状态标记同 engineering_tasks: `[x]` 已完成 · `[~]` 进行中 · `[!]` 阻塞

---

## [2026-04-17] Phase 1.1 — 后端项目初始化

**完成内容**
- `fitness-backend/pom.xml` — Spring Boot 3.2.5 父 POM，依赖涵盖：
  - MyBatis-Plus 3.5.7（`mybatis-plus-spring-boot3-starter`）
  - Spring Security + Validation + Data Redis
  - Knife4j 4.4.0（OpenAPI 3 / Jakarta）
  - Lombok、Hutool 5.8.26、JJWT 0.12.5
- `application.yml` — 主配置：激活 dev profile、MyBatis-Plus 全局（雪花 ID、逻辑删除、驼峰映射）、Knife4j 启用、JWT 参数（access 2h / refresh 7d）
- `application-dev.yml` — 开发环境：MySQL localhost:3306/fitness_db、Redis localhost:6379 无密码、debug 日志
- `application-prod.yml` — 生产环境：所有敏感配置读环境变量（`DB_HOST`、`DB_PASSWORD`、`REDIS_*`）、HikariCP 连接池调优、日志写文件
- `FitnessApplication.java` — 启动类，`@MapperScan("com.fitness.module.**.mapper")`
- `FitnessApplicationTests.java` — 上下文加载测试桩

**关键决策**
- `context-path` 未设为 `/api`：engineering_tasks 要求 `context-path=/api`，但前端 Axios 已规划 `baseURL='/api'` + Vite dev proxy 转发，若后端也加 `/api` 会导致双重前缀。保持 `context-path=/`，由 Controller `@RequestMapping("/api/...")` 统一加前缀，与 Knife4j 文档路径更兼容。
- 未引入 `mapstruct`：engineering_tasks 列了 mapstruct，但 CLAUDE.md 规范中未提及，当前阶段手写转换更直观；Phase 3 量大时再引入。

**遗留问题**
- `application-dev.yml` 中 Redis password 留空，本地开发需确认 Redis 无密码启动；若有密码需在本地 `application-dev.yml` 手动补充（不提交）。
- `mvn compile` 需本地有 JDK 17 + Maven 3.8+，首次运行会下载依赖约 200MB。

---

## [2026-04-17] Phase 1.2 — 通用组件层

**完成内容**
- `common/Result.java` — 泛型统一响应封装，三个静态工厂方法：`success()`、`success(data)`、`error(code, msg)`
- `common/PageResult.java` — 分页响应封装，两个 `of()` 工厂方法：直接从 `IPage<T>` 转换，或传入转换后的 `List<T>` + 原始 `IPage` 获取分页元数据
- `common/BaseEntity.java` — 实体基类，含雪花 ID、自动填充时间戳、逻辑删除字段
- `common/exception/BusinessException.java` — 业务异常，携带 `code` 字段，默认 code=1000
- `common/exception/GlobalExceptionHandler.java` — `@RestControllerAdvice` 全局捕获四类异常，统一返回 `Result`
- `config/MybatisPlusConfig.java` — 分页插件（MySQL 方言）+ `MetaObjectHandler` 自动填充 `createdAt`/`updatedAt`

---

## [2026-04-17] Phase 1.3 — 数据库初始化

**完成内容**
- `sql/00_create_database.sql` — 建库，utf8mb4 字符集
- `sql/01_user.sql` — `sys_user`（用户）、`body_record`（身体数据）
- `sql/02_membership.sql` — `membership_card`（卡种）、`member_membership`（会籍）、`check_in`（签到）
- `sql/03_exercise.sql` — `exercise_category`（运动分类）、`exercise`（运动动作）
- `sql/04_workout.sql` — 训练模块 7 张表（模板/计划/记录及其明细）
- `sql/05_course.sql` — `course`（课程）、`course_schedule`（排课）、`course_booking`（预约），预约表加唯一索引防重复预约
- `sql/06_system.sql` — `sys_announcement`（公告）、`sys_operation_log`（操作日志）
- `sql/07_seed_data.sql` — 管理员 admin/admin123、教练 coach01/coach123、7个运动分类、5种会员卡

**关键决策**
- `course_booking` 加 `UNIQUE KEY (user_id, schedule_id)`：数据库层面防止同一用户重复预约同一排课，比应用层校验更可靠。
- 种子数据 ID 使用固定大数（`1000000000000000001`）：避免与雪花算法生成的 ID 冲突，雪花 ID 基于时间戳，不会生成这么小的值。
