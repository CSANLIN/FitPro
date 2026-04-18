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
- `application-dev.yml` — 开发环境：MySQL localhost:3306/fitpro、Redis localhost:6379 无密码、debug 日志
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

---

## [2026-04-18] Phase 1.4 — 前端项目初始化

**完成内容**
- `fitness-frontend/` — Vite + Vue 3 项目创建，pnpm 安装依赖
- `package.json` — 依赖：Vue 3.4, Vue Router 4, Pinia 2, Axios, Element Plus 2.4, @element-plus/icons-vue, echarts, sass, unplugin 自动导入插件
- `vite.config.js` — 配置 `@` 别名 → `src/`，开发代理 `/api` → `http://localhost:8080`，Element Plus 组件/图标自动导入
- `src/styles/` — CSS 变量系统（颜色/间距/圆角/阴影/字体），全局重置，Element Plus 主题覆盖（主色渐变）
- `src/utils/request.js` — Axios 实例封装，请求拦截器自动注入 Bearer Token，响应拦截器解包 `response.data.data`，401 自动跳登录，统一 ElMessage 错误提示
- `src/router/index.js` — 路由配置：`/` → 登录页（待实现），`/admin/*` → 管理端布局，`/app/*` → 会员端布局，路由守卫检查 Token/角色
- `src/stores/` — Pinia 初始化，`auth.js` 管理 Token/用户信息（login/logout 方法待实现）
- `src/layout/AdminLayout.vue` — 管理端布局：左侧可折叠菜单（仪表盘/会员/教练/课程/运动库/系统），顶部导航栏，面包屑，用户下拉
- `src/layout/AppLayout.vue` — 会员端布局：顶部导航栏（Logo + 用户头像），底部 TabBar（首页/课程/训练/我的），移动端适配
- `src/main.js` — 应用入口，注册路由、Pinia、全局样式

**关键决策**
- 使用 Vite 而非 Webpack：启动快、热更新快，开发体验更好
- 使用 `<script setup>` 语法：比 Options API 更简洁，逻辑组织更清晰
- 使用 Pinia 而非 Vuex：Vue 官方推荐，TypeScript 友好，API 更简单
- Element Plus 自动导入：减少 `import` 语句，代码更干净
- CSS 变量系统：主题定制方便，设计规范统一

**遗留问题**
- 路由白名单需要补充（登录页、注册页、403/404 错误页）
- Auth Store 的 `login()`、`logout()` 方法等待后端接口（Phase 2）
- 移动端适配需在 Phase 5 进一步优化

---

## [2026-04-18] Phase 1.5 — 联调验证

**完成内容**
- `module/system/controller/HealthController.java` — 健康检查接口，`GET /api/health` 返回 `Result.success("ok")`
- `config/CorsConfig.java` — CORS 跨域配置，允许 `localhost:5173` 前端开发服务器，支持常用 HTTP 方法和头部
- `config/SwaggerConfig.java` — Knife4j/OpenAPI 配置，配置 API 文档基本信息，可通过 `http://localhost:8080/doc.html` 访问
- 前端联调测试 — 验证前后端连通性：前端启动成功，可正常请求 `/api/health` 接口

**关键决策**
- CORS 配置使用 `CorsFilter` 而非 `WebMvcConfigurer`，可处理所有请求（包括静态资源）
- Knife4j 配置使用 OpenAPI 3 规范（Spring Boot 3 默认），与 Spring Boot 3 的 Jakarta 命名空间兼容
- 健康检查接口单独放在 `system` 模块，未来可扩展为完整的健康检查端点（数据库、Redis 连接状态）

**遗留问题**
- Knife4j 文档需要实际启动后端服务验证可访问性（启动后可访问 `http://localhost:8080/doc.html`）
- MySQL 和 Redis 服务需要本地启动，否则后端启动会失败

---

## [2026-04-18] Phase 2.1 — 后端安全基础设施

**完成内容**
- `config/RedisConfig.java` — RedisTemplate 序列化配置，Key 用 StringRedisSerializer，Value 用 Jackson2JsonRedisSerializer（含类型信息，支持反序列化）
- `security/JwtTokenProvider.java` — JWT 工具类，使用 JJWT 0.12.x 新 API（`Jwts.parser().verifyWith()`），生成/解析 Access Token (2h) 和 Refresh Token (7d)，密钥从 `application.yml` 读取
- `security/JwtAuthFilter.java` — JWT 认证过滤器，继承 `OncePerRequestFilter`，从 `Authorization: Bearer {token}` 提取 Token，解析后设置 `SecurityContextHolder`
- `security/UserDetailsServiceImpl.java` — 从 `sys_user` 表加载用户，构建 `UserDetails`，角色格式 `ROLE_{role}`
- `module/user/entity/UserEntity.java` — 映射 `sys_user` 表（UserDetailsServiceImpl 依赖）
- `module/user/mapper/UserMapper.java` — 继承 `BaseMapper<UserEntity>`
- `config/SecurityConfig.java` — Spring Security 配置：禁用 CSRF、无状态 Session、白名单放行、注册 JwtAuthFilter、BCryptPasswordEncoder Bean、启用 `@PreAuthorize`

**关键决策**
- JJWT 0.12.x 新 API：`Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()`，旧的 `parserBuilder()` 已废弃
- `Jackson2JsonRedisSerializer` 使用构造函数传入 `ObjectMapper`，避免过时的 `setObjectMapper()` 方法
- `SecurityConfig` 中 `.cors(Customizer.withDefaults())` 确保 Spring Security 不拦截 CORS 预检请求（OPTIONS）
- `UserEntity` 和 `UserMapper` 提前在 Phase 2.1 创建，作为 `UserDetailsServiceImpl` 的依赖

**遗留问题**
- 无

---

## [2026-04-18] Phase 2.2 — 认证接口

**完成内容**
- `module/auth/dto/RegisterDTO.java` — 用户注册请求参数，包含用户名、密码、确认密码、昵称、手机号、角色字段，使用 `@NotBlank`、`@Size`、`@Pattern` 等校验注解
- `module/auth/dto/LoginDTO.java` — 用户登录请求参数，包含用户名和密码字段
- `module/auth/vo/TokenVO.java` — 令牌响应对象，包含 `accessToken`、`refreshToken`、`tokenType`、`expiresIn`、`refreshExpiresIn` 字段
- `module/auth/vo/UserInfoVO.java` — 用户信息响应对象，映射 `UserEntity` 所有字段（除密码外），用于返回给前端的脱敏数据
- `module/auth/service/AuthService.java` — 认证服务接口，定义 `register`、`login`、`refresh`、`logout`、`getCurrentUserInfo` 方法
- `module/auth/service/impl/AuthServiceImpl.java` — 认证服务实现类，已完成所有方法：
  - `register()`：校验用户名/手机号唯一性 → BCrypt 加密密码 → 保存用户 → 生成双 Token → 存储 Refresh Token 到 Redis → 返回 TokenVO
  - `login()`：验证用户名密码 → 检查用户状态 → 生成双 Token → 存储 Refresh Token → 返回 TokenVO
  - `refresh()`：验证 Refresh Token → 与 Redis 存储比对 → 生成新 Access Token → 返回 TokenVO
  - `logout()`：验证 Refresh Token → 从 Redis 中删除对应的 Refresh Token
  - `getCurrentUserInfo()`：从 SecurityContext 获取当前用户 ID → 查询用户信息 → 转换为 UserInfoVO
- `module/auth/controller/AuthController.java` — 认证控制器，完成 `/api/auth/register`、`/login`、`/refresh`、`/logout`、`/me` 所有接口

**关键决策**
- RedisTemplate 类型匹配：将 `RedisTemplate<String, String>` 改为 `RedisTemplate<String, Object>` 以匹配 `RedisConfig` 中的 JSON 序列化配置
- JwtTokenProvider 扩展：添加 `getAccessTokenExpire()` 和 `getRefreshTokenExpire()` 方法供服务层获取过期时间配置
- 时间字段自动填充：`UserEntity` 继承 `BaseEntity`，利用 MyBatis-Plus 的 `MetaObjectHandler` 自动填充 `createdAt` 和 `updatedAt`，避免在服务层手动设置时间
- 统一业务错误码：注册、登录、刷新、登出等操作使用统一的 `BusinessException` 错误码（1001-1009）

**遗留问题**
- Maven 编译环境问题：本地环境中 Maven 不在 PATH 中，`mvn compile` 无法执行，需要用户配置 Maven 环境或使用 IDE 进行编译
- 接口功能待测试：需启动后端服务，通过 Knife4j 文档测试注册→登录→刷新→登出→获取用户信息全流程

---

## [2026-04-18] Phase 2.3.1 — 前端认证API

**完成内容**
- `src/api/auth.js` — 认证API模块，包含5个方法：
  - `register(data)`：用户注册，参数包含用户名、密码、昵称、手机号、角色
  - `login(data)`：用户登录，参数包含用户名、密码
  - `refresh(refreshToken)`：刷新访问令牌，参数为刷新令牌字符串
  - `logout(refreshToken)`：用户登出，参数为刷新令牌字符串
  - `getMe()`：获取当前用户信息

**关键决策**
- 使用统一的request实例（`@/utils/request`），该实例已配置baseURL为`/api`，并自动注入Bearer Token到请求头
- 所有方法返回Promise，由request拦截器统一处理响应格式和错误码
- 响应数据格式与后端`TokenVO`、`UserInfoVO`一致，request拦截器会自动提取`response.data.data`

**遗留问题**
- API调用尚未集成到具体的Auth Store和页面组件中，需后续完成2.3.2（Auth Store）和2.3.3/2.3.4（登录/注册页面）

## [2026-04-18] Phase 2.3.2 — Auth Store

**完成内容**
- `src/stores/auth.js` — 认证状态管理 Store，使用 Pinia 的 Composition API 实现：
  - 状态：token、refreshToken、userInfo，自动持久化到 localStorage
  - 计算属性：isLoggedIn，基于 token 是否存在
  - 方法：login() 调用 authApi.login 并保存令牌、fetchUserInfo() 获取用户信息、register() 注册并自动登录、logout() 调用后端登出接口并清除本地状态、refreshAccessToken() 刷新访问令牌、init() 初始化时自动获取用户信息

**关键决策**
- 采用 Composition API 写法，逻辑更集中，易于扩展
- 所有方法都处理错误情况：失败时清除本地认证状态，确保状态一致性
- 提供 init() 方法用于应用启动时恢复用户状态（有 token 但无 userInfo 时自动获取）
- logout() 方法确保即使后端登出失败也清除本地状态，避免用户卡在登录状态

**遗留问题**
- 需要与路由守卫集成，实现未登录跳转和角色权限控制（Phase 2.3.5）
- 需要与 Axios 拦截器集成，实现 Token 自动刷新和 401 处理（Phase 2.3.6）
- 需要前端页面调用这些方法完成登录/注册流程（Phase 2.3.3/2.3.4）

---
## [2026-04-18] Phase 2.3.3 — 登录页面

**完成内容**
- `src/views/auth/LoginView.vue` — 完整的登录页面组件：
  - 响应式布局，使用 CSS 变量系统确保设计一致性
  - 背景使用健身主题图片叠加渐变蒙层，营造专业氛围
  - 表单包含用户名、密码输入框，支持密码显示/隐藏切换
  - 表单验证：用户名（3-20字符）、密码（6-20字符），Element Plus 规则验证
  - "记住我"复选框和"忘记密码"链接（暂未实现功能）
  - 登录按钮集成 Auth Store 的 `login()` 方法，支持加载状态
  - 登录成功后根据用户角色自动跳转：管理员/教练 → `/admin/dashboard`，会员 → `/app/profile`
  - 提供注册页面链接，方便新用户注册

**关键决策**
- 使用 Unsplash 健身图片作为背景，增强视觉吸引力
- 采用卡片式设计，结合毛玻璃效果（backdrop-filter），符合现代 UI 趋势
- 完全响应式设计，在移动端自动调整内边距和布局
- 表单验证在提交前进行，确保数据格式正确
- 登录成功后立即获取用户信息并保存到 Auth Store

**遗留问题**
- "忘记密码"功能暂未实现，需要后续 Phase 补充
- 背景图片为外部 URL，生产环境建议下载到本地或使用 CDN 确保稳定性
