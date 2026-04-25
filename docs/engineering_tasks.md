# FitPro 工程执行任务清单

> 状态标记: `[ ]` 待做 · `[~]` 进行中 · `[x]` 已完成 · `[!]` 阻塞

---

## Phase 1 — 项目骨架搭建

### 1.1 后端项目初始化

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 1.1.1 | Spring Initializr 创建项目 (Java 17, Spring Boot 3.2, Maven) | `fitness-backend/` 整个目录 | `mvn compile` 通过 | `[x]` |
| 1.1.2 | pom.xml 补充依赖 | `pom.xml` | 依赖: mybatis-plus-spring-boot3-starter 3.5.x, spring-boot-starter-data-redis, knife4j-openapi3-jakarta-spring-boot-starter 4.x, lombok, hutool-all, mapstruct, jjwt 0.12.x, spring-boot-starter-security, spring-boot-starter-validation | `[x]` |
| 1.1.3 | 主配置文件 | `src/main/resources/application.yml` | 激活 dev profile, 设置 server.port=8080, context-path=/api | `[x]` |
| 1.1.4 | 开发环境配置 | `src/main/resources/application-dev.yml` | MySQL 连接 (fitpro, 3306), Redis 连接 (localhost:6379), MyBatis-Plus 配置 (mapper-locations, logic-delete, id-type), 日志级别 DEBUG | `[x]` |
| 1.1.5 | 启动类 | `src/.../FitnessApplication.java` | `@SpringBootApplication` `@MapperScan("com.fitness.module.*.mapper")` | `[x]` |

### 1.2 通用组件层

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 1.2.1 | 统一响应封装 | `common/Result.java` | 泛型 `Result<T>`, 静态方法 `success()` `success(data)` `error(code, msg)`, 字段: code/message/data | `[x]` |
| 1.2.2 | 分页响应封装 | `common/PageResult.java` | 泛型 `PageResult<T>`, 字段: list/total/pageNum/pageSize, 提供从 IPage 转换的静态工厂方法 | `[x]` |
| 1.2.3 | 实体基类 | `common/BaseEntity.java` | 字段: id(Long, @TableId ASSIGN_ID), createdAt(LocalDateTime), updatedAt(LocalDateTime), deleted(Integer, @TableLogic) | `[x]` |
| 1.2.4 | 业务异常类 | `common/exception/BusinessException.java` | 字段: code(int), message(String), 继承 RuntimeException | `[x]` |
| 1.2.5 | 全局异常处理器 | `common/exception/GlobalExceptionHandler.java` | @RestControllerAdvice, 捕获: BusinessException→对应code, MethodArgumentNotValidException→400, AccessDeniedException→403, Exception→500 | `[x]` |
| 1.2.6 | MyBatis-Plus 配置 | `config/MybatisPlusConfig.java` | 分页插件 PaginationInnerInterceptor, MetaObjectHandler 自动填充 createdAt/updatedAt | `[x]` |

### 1.3 数据库初始化

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 1.3.1 | 建库脚本 | `sql/00_create_database.sql` | `CREATE DATABASE fitpro CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;` | `[x]` |
| 1.3.2 | 用户与身体数据表 | `sql/01_user.sql` | 建表: sys_user, body_record; 索引: idx_user_username, idx_user_phone, idx_body_record_user_id | `[x]` |
| 1.3.3 | 会籍与签到表 | `sql/02_membership.sql` | 建表: membership_card, member_membership, check_in; 索引: idx_membership_user_id, idx_checkin_user_id | `[x]` |
| 1.3.4 | 运动库表 | `sql/03_exercise.sql` | 建表: exercise_category, exercise; 索引: idx_exercise_category_id | `[x]` |
| 1.3.5 | 训练模块表 | `sql/04_workout.sql` | 建表: workout_template, workout_template_item, workout_plan, workout_plan_day, workout_plan_day_item, workout_record, workout_record_item | `[x]` |
| 1.3.6 | 课程模块表 | `sql/05_course.sql` | 建表: course, course_schedule, course_booking; 索引: idx_schedule_date, idx_booking_user_id | `[x]` |
| 1.3.7 | 系统模块表 | `sql/06_system.sql` | 建表: sys_announcement, sys_operation_log | `[x]` |
| 1.3.8 | 种子数据 | `sql/07_seed_data.sql` | 插入: 超级管理员账号 (admin/admin123, BCrypt加密), 运动分类 (胸部/背部/腿部/肩部/手臂/核心/有氧), 示例会员卡种 | `[x]` |

### 1.4 前端项目初始化

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 1.4.1 | Vite 创建 Vue 3 项目 | `fitness-frontend/` | `npm create vite@latest fitness-frontend -- --template vue`, `npm install` 成功 | `[x]` |
| 1.4.2 | 安装核心依赖 | `package.json` | dependencies: vue-router@4, pinia, axios, element-plus, @element-plus/icons-vue, echarts; devDependencies: sass, unplugin-auto-import, unplugin-vue-components | `[x]` |
| 1.4.3 | Vite 配置 | `vite.config.js` | 配置: @别名→src, Element Plus 自动导入, 开发代理 /api→http://localhost:8080 | `[x]` |
| 1.4.4 | 全局样式 | `src/styles/index.scss` `src/styles/variables.scss` | CSS 变量: 主色/背景色/文字色/间距, Element Plus 主题覆盖, reset 样式 | `[x]` |
| 1.4.5 | Axios 封装 | `src/utils/request.js` | 创建实例 baseURL='/api', 请求拦截器注入 Bearer Token, 响应拦截器: 解包 data / 401 跳登录 / ElMessage 报错 | `[x]` |
| 1.4.6 | 路由基础配置 | `src/router/index.js` | 路由: / → 登录, /admin → 管理端布局, /app → 会员端布局; 导航守卫: 未登录拦截 | `[x]` |
| 1.4.7 | Pinia 初始化 | `src/stores/index.js` | createPinia() 注册到 app | `[x]` |
| 1.4.8 | 管理端布局 | `src/layout/AdminLayout.vue` | 左侧菜单 + 顶部导航栏 + 右侧内容区 (el-container), 菜单折叠, 面包屑 | `[x]` |
| 1.4.9 | 会员端布局 | `src/layout/AppLayout.vue` | 顶部导航 + 底部 TabBar + 内容区, 移动端友好 | `[x]` |
| 1.4.10 | main.js 入口 | `src/main.js` | 注册: App, Router, Pinia, Element Plus, 全局样式 | `[x]` |

### 1.5 联调验证

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 1.5.1 | 健康检查接口 | `module/system/controller/HealthController.java` | `GET /api/health` 返回 `Result.success("ok")` | `[x]` |
| 1.5.2 | CORS 配置 | `config/CorsConfig.java` | 允许 localhost:5173, 允许 GET/POST/PUT/DELETE, 允许 Authorization 头 | `[x]` |
| 1.5.3 | Knife4j 配置 | `config/SwaggerConfig.java` | 访问 http://localhost:8080/doc.html 能看到接口文档 | `[x]` |
| 1.5.4 | 前端联调测试 | 手动验证 | 前端 `npm run dev` 启动, 请求 /api/health 返回成功 | `[x]` |

**M1 里程碑验收**: 后端启动无报错 + Knife4j 文档可访问 + 前端请求后端成功 + 19张表建好

---

## Phase 2 — 认证与授权

### 2.1 后端安全基础设施

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 2.1.1 | Redis 配置类 | `config/RedisConfig.java` | RedisTemplate 序列化配置 (Key: String, Value: JSON) | `[x]` |
| 2.1.2 | JWT 工具类 | `security/JwtTokenProvider.java` | 方法: generateAccessToken(userId, role)→String, generateRefreshToken(userId)→String, parseToken(token)→Claims, isTokenExpired(token)→boolean; Access Token 有效期 2h, Refresh Token 7d; 密钥从配置读取 | `[x]` |
| 2.1.3 | JWT 认证过滤器 | `security/JwtAuthFilter.java` | 继承 OncePerRequestFilter, 从 Authorization 头提取 Token, 解析后设置 SecurityContext, 白名单路径直接放行 | `[x]` |
| 2.1.4 | UserDetailsService 实现 | `security/UserDetailsServiceImpl.java` | 从 sys_user 表加载用户, 构建 UserDetails (含角色 ROLE_SUPER_ADMIN / ROLE_COACH / ROLE_MEMBER) | `[x]` |
| 2.1.5 | Security 配置 | `config/SecurityConfig.java` | SecurityFilterChain: 禁用 csrf, 无状态 session, 白名单 (/api/auth/**, /doc.html, /webjars/**), 注册 JwtAuthFilter, BCryptPasswordEncoder Bean | `[x]` |

### 2.2 认证接口 (后端)

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 2.2.1 | Auth DTO | `module/auth/dto/LoginDTO.java` `RegisterDTO.java` | LoginDTO: username + password; RegisterDTO: username + password + nickname + phone + role(默认MEMBER) | `[x]` |
| 2.2.2 | Auth VO | `module/auth/vo/TokenVO.java` `UserInfoVO.java` | TokenVO: accessToken + refreshToken + expiresIn; UserInfoVO: id + username + nickname + avatar + role | `[x]` |
| 2.2.3 | User Entity + Mapper | `module/user/entity/UserEntity.java` `module/user/mapper/UserMapper.java` | UserEntity 映射 sys_user 全字段, UserMapper 继承 BaseMapper | `[x]` |
| 2.2.4 | AuthService | `module/auth/service/AuthService.java` + `impl/` | register(): 校验用户名唯一→BCrypt加密→入库→返回TokenVO; login(): 查用户→校验密码→生成双Token→Refresh存Redis→返回TokenVO; refresh(): 校验RefreshToken→生成新AccessToken; logout(): 删除Redis中RefreshToken | `[x]` |
| 2.2.5 | AuthController | `module/auth/controller/AuthController.java` | POST /api/auth/register, POST /api/auth/login, POST /api/auth/refresh, POST /api/auth/logout, GET /api/auth/me | `[x]` |
| 2.2.6 | Knife4j 接口测试 | 手动验证 | 在 Swagger 文档中测试注册→登录→携带Token访问/me | `[x]` |

### 2.3 前端认证

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 2.3.1 | Auth API | `src/api/auth.js` | authApi: login, register, refresh, logout, getMe | `[x]` |
| 2.3.2 | Auth Store | `src/stores/auth.js` | useAuthStore: token, refreshToken, userInfo, isLoggedIn(computed), login(), logout(), refreshAccessToken(), fetchUserInfo() | `[x]` |
| 2.3.3 | 登录页 | `src/views/auth/LoginView.vue` | 表单: 用户名+密码+记住我, Element Plus 表单校验, 登录成功→按角色跳转 (ADMIN→/admin, MEMBER→/app) | `[x]` |
| 2.3.4 | 注册页 | `src/views/auth/RegisterView.vue` | 表单: 用户名+密码+确认密码+昵称+手机号, 注册成功→自动登录→跳转 | `[x]` |
| 2.3.5 | 路由守卫完善 | `src/router/index.js` | beforeEach: 无Token→跳登录 (白名单除外), 有Token无userInfo→fetchUserInfo, 角色不匹配→403页面 | `[x]` |
| 2.3.6 | Axios Token 刷新 | `src/utils/request.js` | 响应拦截器: 401时用refreshToken换新token→重试原请求, refreshToken也失效→跳登录 | `[x]` |
| 2.3.7 | 403/404 页面 | `src/views/error/403View.vue` `404View.vue` | 简洁的错误提示 + 返回按钮 | `[x]` |

### 2.4 认证流程验证

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 2.4.1 | 注册→登录→访问受保护接口流程验证 | 手动验证 | 新用户注册→登录获取Token→携带Token访问/me接口成功 | `[x]` |
| 2.4.2 | 不同角色访问权限验证 | 手动验证 | SUPER_ADMIN能访问/admin路径，MEMBER只能访问/app路径，角色不匹配显示403 | `[x]` |
| 2.4.3 | Token过期→刷新→重试流程验证 | 手动验证 | Access Token过期后，自动用Refresh Token刷新并重试请求，Refresh Token也过期时跳转登录页 | `[x]` |

**M2 里程碑验收**: 注册新用户 → 登录获取Token → 访问受保护接口 → Token过期自动刷新 → 不同角色路由隔离

---

## Phase 3 — 核心业务模块

### 3.1 用户模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.1.1 | User DTO/VO | `module/user/dto/UserQueryDTO.java` `UserUpdateDTO.java` `module/user/vo/UserVO.java` | QueryDTO: keyword + role + status + 分页参数; UpdateDTO: nickname + avatar + email + phone + gender + birthday; VO: 脱敏 (无password) | `[x]` |
| 3.1.2 | UserService | `module/user/service/UserService.java` + `impl/` | pageList(query): 分页+模糊搜索+角色筛选; getDetail(id); updateProfile(id, dto); updatePassword(id, oldPwd, newPwd); toggleStatus(id) | `[x]` |
| 3.1.3 | UserController | `module/user/controller/UserController.java` | GET /api/users (管理端列表), GET /api/users/{id}, PUT /api/users/profile (用户端修改), PUT /api/users/password, PUT /api/users/{id}/status (管理端) | `[x]` |
| 3.1.4 | 前端 - 个人中心 | `src/views/profile/ProfileView.vue` | 头像上传, 基本信息编辑表单, 修改密码弹窗 | `[x]` |
| 3.1.5 | 前端 - 用户管理 (管理端) | `src/views/member/MemberListView.vue` | el-table 列表 + 搜索栏 + 分页, 状态切换开关, 查看详情 | `[x]` |

### 3.2 身体数据模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.2.1 | BodyRecord Entity/DTO/VO | `module/user/entity/BodyRecordEntity.java` `dto/BodyRecordCreateDTO.java` `vo/BodyRecordVO.java` | CreateDTO: weight + height + bodyFat + chest + waist + hip + recordDate + remark, 带校验 | `[ ]` |
| 3.2.2 | BodyRecordService | `module/user/service/BodyRecordService.java` + `impl/` | create(userId, dto); listByUser(userId, startDate, endDate): 按日期范围查询; getLatest(userId) | `[ ]` |
| 3.2.3 | BodyRecordController | `module/user/controller/BodyRecordController.java` | POST /api/body-records, GET /api/body-records (当前用户), GET /api/body-records/latest | `[ ]` |
| 3.2.4 | 前端 - 身体数据页 | `src/views/profile/BodyDataView.vue` | 录入表单 + 历史记录列表 + 最新数据卡片 (图表在 Phase 5) | `[ ]` |

### 3.3 运动库模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.3.1 | Entity 层 | `module/exercise/entity/ExerciseCategoryEntity.java` `ExerciseEntity.java` | 映射 exercise_category, exercise 表 | `[x]` |
| 3.3.2 | DTO/VO | `module/exercise/dto/ExerciseCreateDTO.java` `ExerciseQueryDTO.java` `vo/ExerciseVO.java` `ExerciseCategoryVO.java` | QueryDTO: categoryId + muscleGroup + equipment + difficulty + keyword | `[x]` |
| 3.3.3 | Mapper | `module/exercise/mapper/ExerciseCategoryMapper.java` `ExerciseMapper.java` | BaseMapper 继承, ExerciseMapper 加联查分类名的 XML | `[x]` |
| 3.3.4 | Service | `module/exercise/service/ExerciseCategoryService.java` `ExerciseService.java` + `impl/` | 分类: listAll, create, update, delete; 动作: pageList(query), create, update, delete, getDetail | `[x]` |
| 3.3.5 | Controller | `module/exercise/controller/ExerciseCategoryController.java` `ExerciseController.java` | 分类: GET/POST/PUT/DELETE /api/exercise-categories; 动作: GET/POST/PUT/DELETE /api/exercises | `[x]` |
| 3.3.6 | 前端 - 运动库浏览 (会员端) | `src/views/exercise/ExerciseListView.vue` | 分类 Tab 切换 + 动作卡片列表 + 筛选 (肌群/器械/难度) + 动作详情弹窗 | `[x]` |
| 3.3.7 | 前端 - 运动库管理 (管理端) | `src/views/exercise/ExerciseManageView.vue` | 分类管理 + 动作 CRUD 表格 + 新增/编辑弹窗 | `[x]` |

### 3.4 训练模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.4.1 | Entity 层 (6个) | `module/workout/entity/` 下 WorkoutTemplateEntity, WorkoutTemplateItemEntity, WorkoutPlanEntity, WorkoutPlanDayEntity, WorkoutPlanDayItemEntity, WorkoutRecordEntity, WorkoutRecordItemEntity | 映射对应 7 张表 | `[ ]` |
| 3.4.2 | DTO/VO | `module/workout/dto/` `vo/` | PlanCreateDTO (含嵌套 days→items), RecordCreateDTO (含嵌套 items), PlanVO, PlanDetailVO, RecordVO, RecordDetailVO, TemplateVO | `[ ]` |
| 3.4.3 | Mapper + XML | `module/workout/mapper/` + `resources/mapper/` | 7 个 Mapper, WorkoutPlanMapper.xml: 联查计划+天+动作的嵌套结果映射, WorkoutRecordMapper.xml: 联查记录+组 | `[ ]` |
| 3.4.4 | WorkoutTemplateService | `module/workout/service/WorkoutTemplateService.java` + `impl/` | CRUD + 复制模板 + 按教练查询 | `[ ]` |
| 3.4.5 | WorkoutPlanService | `module/workout/service/WorkoutPlanService.java` + `impl/` | create(含嵌套保存), getDetail(联查), listByUser, listByCoach, updateStatus | `[ ]` |
| 3.4.6 | WorkoutRecordService | `module/workout/service/WorkoutRecordService.java` + `impl/` | create(含嵌套保存), getDetail, listByUser(分页), 统计: weeklyCount, totalVolume | `[ ]` |
| 3.4.7 | Controller | `module/workout/controller/` 3个 Controller | 模板: /api/workout-templates; 计划: /api/workout-plans; 记录: /api/workout-records | `[ ]` |
| 3.4.8 | 前端 - 训练计划 (会员端) | `src/views/workout/WorkoutPlanView.vue` | 按周展示训练日, 每天显示动作列表, 点击进入训练记录 | `[ ]` |
| 3.4.9 | 前端 - 训练记录 (会员端) | `src/views/workout/WorkoutRecordView.vue` | 实时记录: 选动作→填组数/重量/次数→完成, 历史记录列表 | `[ ]` |
| 3.4.10 | 前端 - 训练模板管理 (管理端) | `src/views/workout/WorkoutTemplateManageView.vue` | 模板 CRUD + 动作拖拽排序 | `[ ]` |

### 3.5 课程与预约模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.5.1 | Entity 层 | `module/course/entity/` CourseEntity, CourseScheduleEntity, CourseBookingEntity | 映射 3 张表 | `[ ]` |
| 3.5.2 | DTO/VO | `module/course/dto/` `vo/` | CourseCreateDTO, ScheduleCreateDTO, BookingCreateDTO, CourseVO, ScheduleVO (含教练名+课程名+已约人数), BookingVO | `[ ]` |
| 3.5.3 | Mapper + XML | `module/course/mapper/` | CourseScheduleMapper.xml: 联查课程+教练信息 | `[ ]` |
| 3.5.4 | CourseService | `module/course/service/CourseService.java` + `impl/` | CRUD + 上下架 | `[ ]` |
| 3.5.5 | CourseScheduleService | `module/course/service/CourseScheduleService.java` + `impl/` | create, listByDateRange(日历查询), listByCoach, cancel | `[ ]` |
| 3.5.6 | CourseBookingService | `module/course/service/CourseBookingService.java` + `impl/` | book(userId, scheduleId): 校验容量→乐观锁更新currentCount→创建预约; cancel(bookingId); listByUser; listBySchedule | `[ ]` |
| 3.5.7 | Controller | `module/course/controller/` 3个 Controller | 课程: /api/courses; 排课: /api/course-schedules; 预约: /api/course-bookings | `[ ]` |
| 3.5.8 | 前端 - 课程列表 (会员端) | `src/views/course/CourseListView.vue` | 课程卡片列表 + 类型筛选 + 点击查看排课 | `[ ]` |
| 3.5.9 | 前端 - 课程预约 (会员端) | `src/views/course/CourseBookingView.vue` | 排课日历 + 点击预约/取消 + 我的预约列表 | `[ ]` |
| 3.5.10 | 前端 - 排课管理 (管理端) | `src/views/course/ScheduleManageView.vue` | 日历视图排课 + 新增排课弹窗 (选课程+教练+时间+地点) | `[ ]` |

### 3.6 会籍与签到模块

| # | 任务 | 产出文件 | 验收标准 | 状态 |
|---|------|----------|----------|------|
| 3.6.1 | Entity 层 | `module/membership/entity/` MembershipCardEntity, MemberMembershipEntity; `module/checkin/entity/` CheckInEntity | 映射 3 张表 | `[ ]` |
| 3.6.2 | DTO/VO | 各模块 dto/ vo/ | CardCreateDTO, MembershipCreateDTO (办卡: userId+cardId), MembershipVO (含卡种名+剩余天数/次数), CheckInVO | `[ ]` |
| 3.6.3 | MembershipService | `module/membership/service/` + `impl/` | createCard(管理端), listCards; assignMembership(办卡): 计算到期日; renew(续费); freeze/unfreeze(冻结/解冻); cancel(退卡) | `[ ]` |
| 3.6.4 | CheckInService | `module/checkin/service/` + `impl/` | checkIn(userId): 校验有效会籍→次卡扣次数→创建记录; listByUser(分页); todayCount(统计) | `[ ]` |
| 3.6.5 | Controller | 2个 Controller | 会籍: /api/memberships, /api/membership-cards; 签到: /api/check-ins | `[ ]` |
| 3.6.6 | 前端 - 会籍信息 (会员端) | `src/views/membership/MembershipView.vue` | 当前会籍卡片 (类型+到期日+剩余次数) + 历史会籍 | `[ ]` |
| 3.6.7 | 前端 - 签到打卡 (会员端) | `src/views/checkin/CheckInView.vue` | 签到按钮 + 本月签到日历 + 签到记录列表 | `[ ]` |
| 3.6.8 | 前端 - 会籍管理 (管理端) | `src/views/membership/MembershipManageView.vue` | 卡种管理 + 会员办卡/续费/冻结/退卡操作弹窗 | `[ ]` |

**M3 里程碑验收**: 所有业务模块 CRUD 接口可用 + 前端页面可操作 + Knife4j 文档完整

---

<!-- PLACEHOLDER_PHASE4 -->
