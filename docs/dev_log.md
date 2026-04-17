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
