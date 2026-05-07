# FitPro 健身管理系统

毕业设计项目 — 基于 Spring Boot 3.2 + Vue 3 的前后端分离健身管理系统。

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 |
| JDK | Java | 17 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| ORM | MyBatis-Plus | 3.5.7 |
| 安全 | Spring Security + JWT | — |
| API 文档 | Knife4j / Swagger | — |
| AI | DeepSeek API | — |
| 前端框架 | Vue 3 (Composition API) | 3.4 |
| 构建工具 | Vite | 5.x |
| UI 库 | Element Plus | 2.x |
| 状态管理 | Pinia | — |
| 图表 | ECharts / vue-echarts | — |
| 测试 | Vitest + happy-dom | — |

## 功能模块

### 会员端
- **课程预约** — 浏览课程分类，按日期筛选排课，在线预约/取消
- **训练计划** — 查看训练计划，按日执行训练（记录组数/次数/重量）
- **运动库** — 浏览动作分类，查看动作详情（图片/视频/要领）
- **身体数据** — 录入体重/体脂等指标，历史趋势图
- **AI 健身助手** — 基于用户数据的流式 AI 对话
- **签到打卡** — 每日签到，连续签到统计
- **会籍信息** — 查看会籍有效期/剩余次数
- **个人中心** — 编辑资料、修改密码、头像上传

### 管理端
- **仪表盘** — 会员数/签到数/课程数/收入统计（ECharts 图表）
- **会员管理** — 会员列表/详情/会籍操作（办卡/续费/冻结/退卡）
- **教练管理** — 教练列表/详情/课程排行
- **课程管理** — 课程 CRUD / 上下架 / 排课管理
- **运动库管理** — 动作分类 / 动作 CRUD（含图片视频上传）
- **系统管理** — 公告发布、操作日志、系统配置

### 通用功能
- JWT 登录/注册/刷新 Token
- 角色权限控制（MEMBER / COACH / SUPER_ADMIN）
- 会籍权限校验（无会籍会员仅可浏览）
- Token 过期自动刷新

## 快速启动

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0 |
| Redis | 7.x |

### 1. 初始化数据库

```bash
mysql -u root -p < sql/00_create_database.sql
mysql -u root -p fitpro < sql/01_user.sql
mysql -u root -p fitpro < sql/02_membership.sql
mysql -u root -p fitpro < sql/03_exercise.sql
mysql -u root -p fitpro < sql/04_workout.sql
mysql -u root -p fitpro < sql/05_course.sql
mysql -u root -p fitpro < sql/06_system.sql
mysql -u root -p fitpro < sql/07_seed_data.sql
```

或者使用图形工具（Navicat / MySQL Workbench）依次执行上述 SQL 文件。

### 2. 修改数据库配置

编辑 `fitness-backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    username: root       # 改为你的 MySQL 用户名
    password: yourpass   # 改为你的 MySQL 密码
```

### 3. 启动后端

```bash
cd fitness-backend
mvn spring-boot:run
```

首次运行会自动下载依赖（约 200MB），启动成功标志：控制台出现 `Started FitnessApplication`。

后端 API 文档：http://localhost:8080/doc.html

### 4. 启动前端

```bash
cd fitness-frontend
npm install        # 首次运行需要安装依赖
npm run dev
```

启动后访问：**http://localhost:5173**

### 5. 生产构建

```bash
cd fitness-frontend
npm run build      # 输出到 dist/ 目录，自动 gzip 压缩
```

## 种子账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | admin | admin123 | 可访问管理端全部功能 |
| 教练 | coach01 | coach123 | 可管理排课和学员 |
| 会员（有会籍） | testabc | 123456 | 已分配月卡会籍，可使用 AI/训练/预约等功能 |
| 会员（无会籍） | member | 123456 | 仅可浏览 |

## 项目结构

```
FITPRO/
├── fitness-backend/              # Spring Boot 后端
│   ├── src/main/java/com/fitness/
│   │   ├── config/               # 配置 (Security, CORS, Jackson, MyBatis-Plus)
│   │   ├── common/               # 通用组件 (Result, BaseEntity, 异常处理)
│   │   ├── security/             # JWT 认证过滤器
│   │   ├── module/
│   │   │   ├── auth/             # 认证模块 (登录/注册/Token刷新)
│   │   │   ├── user/             # 用户/身体数据模块
│   │   │   ├── course/           # 课程/排课/预约模块
│   │   │   ├── workout/          # 训练计划/记录模块
│   │   │   ├── exercise/         # 运动库模块
│   │   │   ├── membership/       # 会籍/签到模块
│   │   │   ├── payment/          # 支付模块
│   │   │   ├── ai/               # AI 健身助手模块
│   │   │   ├── admin/            # 管理端仪表盘
│   │   │   ├── coach/            # 教练端模块
│   │   │   ├── file/             # 文件上传模块
│   │   │   └── system/           # 系统管理模块
│   │   └── util/
│   ├── src/main/resources/
│   │   ├── mapper/               # MyBatis XML 映射
│   │   └── application*.yml      # 多环境配置
│   └── pom.xml
│
├── fitness-frontend/             # Vue 3 前端
│   ├── src/
│   │   ├── api/                  # API 封装 (按模块)
│   │   ├── views/                # 页面组件
│   │   ├── router/               # 路由配置
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── layout/               # 布局组件
│   │   ├── styles/               # 全局样式/CSS变量
│   │   └── utils/                # 工具函数 (Axios 封装)
│   ├── index.html
│   └── vite.config.js
│
├── sql/                          # 数据库建表脚本 (8个文件)
├── docs/                         # 开发文档
│   ├── task_tree.md              # 分阶段任务树
│   ├── engineering_tasks.md      # 工程执行清单
│   └── dev_log.md                # 开发日志
└── README.md
```

## API 概览

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | `/api/auth/login` | 用户名密码登录 |
| | POST | `/api/auth/register` | 注册新用户 |
| | POST | `/api/auth/refresh` | 刷新 Token |
| 用户 | GET | `/api/users/profile` | 获取个人信息 |
| | PUT | `/api/users/profile` | 更新个人信息 |
| 身体数据 | POST | `/api/body-records` | 录入身体数据 |
| | GET | `/api/body-records` | 查询历史记录 |
| 课程 | GET | `/api/courses` | 课程列表（支持按类型筛选） |
| 排课 | GET | `/api/course-schedules` | 按日期范围查排课 |
| 预约 | POST | `/api/course-bookings` | 预约课程 |
| | PUT | `/api/course-bookings/{id}/cancel` | 取消预约 |
| 训练计划 | GET | `/api/workout-plans` | 计划列表 |
| | GET | `/api/workout-plans/{id}` | 计划详情（含训练日/动作） |
| | POST | `/api/workout-plans` | 创建计划 |
| 训练记录 | POST | `/api/workout-records` | 提交训练记录 |
| 会籍 | GET | `/api/memberships/my` | 我的会籍 |
| 签到 | POST | `/api/check-ins` | 签到打卡 |
| AI | POST | `/api/ai/chat` | AI 流式对话（SSE） |
| 文件 | POST | `/api/files/upload` | 上传文件（图片/视频） |

完整 API 文档启动后端后访问：http://localhost:8080/doc.html

## 开发

### 代码规范

参见 [CLAUDE.md](./CLAUDE.md) — 包含 Java/Vue 命名规范、包结构、提交规范等。

### 开发日志

参见 [docs/dev_log.md](./docs/dev_log.md) — 按日期记录每次开发的内容、决策和遗留问题。

### 测试

```bash
# 后端测试
cd fitness-backend
mvn test

# 前端测试
cd fitness-frontend
npm run test
```

共 90+ 个测试用例，覆盖 Service 层、Controller 层、前端 Store 和页面组件。

## 部署

### Docker 部署（推荐）

```bash
# 1. 构建前端
cd fitness-frontend
npm install && npm run build

# 2. 构建后端
cd ../fitness-backend
mvn package -DskipTests

# 3. Docker Compose 启动
cd ..
docker-compose up -d
```

### 手动部署

```bash
# 后端
cd fitness-backend
mvn package -DskipTests
java -jar target/fitness-backend-*.jar

# 前端（使用 nginx 托管 dist 目录）
cd fitness-frontend
npm install && npm run build
# 将 dist/ 目录复制到 nginx html 目录
```
