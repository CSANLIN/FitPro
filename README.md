# FitPro 健身管理系统

毕业设计 — Spring Boot 3.2 + Vue 3 前后端分离

---

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | x graph TB    subgraph 路由层        R1[/ - 登录页]        R2[/admin - 管理端布局]        R3[/app - 会员端布局]    end​    subgraph 管理端页面        R2 --> P1[Dashboard 仪表盘]        R2 --> P2[会员管理]        R2 --> P3[教练管理]        R2 --> P4[课程管理]        R2 --> P5[运动库管理]        R2 --> P6[系统管理]    end​    subgraph 会员端页面        R3 --> P7[个人中心]        R3 --> P8[课程预约]        R3 --> P9[训练计划]        R3 --> P10[签到打卡]    end​    subgraph 状态管理 Pinia        S1[useAuthStore]        S2[useUserStore]        S3[useCourseStore]    end​    subgraph API 层        A1[authApi]        A2[userApi]        A3[courseApi]        A4[workoutApi]        A5[exerciseApi]        A6[membershipApi]    end​    P1 & P2 & P3 & P4 & P5 & P6 & P7 & P8 & P9 & P10 --> S1 & S2 & S3    S1 & S2 & S3 --> A1 & A2 & A3 & A4 & A5 & A6mermaid#mermaidChart6{font-family:sans-serif;font-size:16px;fill:var(--text-color);}#mermaidChart6 .error-icon{fill:#552222;}#mermaidChart6 .error-text{fill:#552222;stroke:#552222;}#mermaidChart6 .edge-thickness-normal{stroke-width:2px;}#mermaidChart6 .edge-thickness-thick{stroke-width:3.5px;}#mermaidChart6 .edge-pattern-solid{stroke-dasharray:0;}#mermaidChart6 .edge-pattern-dashed{stroke-dasharray:3;}#mermaidChart6 .edge-pattern-dotted{stroke-dasharray:2;}#mermaidChart6 .marker{fill:#333333;stroke:#333333;}#mermaidChart6 .marker.cross{stroke:#333333;}#mermaidChart6 svg{font-family:sans-serif;font-size:16px;}#mermaidChart6 :root{--mermaid-alt-font-family:sans-serif;}Syntax error in textmermaid version 10.9.1ERROR: [Mermaid] Lexical error on line 3. Unrecognized text.
...层        R1[/ - 登录页]        R2[/admin 
----------------------^ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0 |
| Redis | 7.x |

---

## 第一步：初始化数据库

1. 启动 MySQL 服务，确保用户名 `root`、密码 `root` 可登录（或修改 `fitness-backend/src/main/resources/application-dev.yml` 中的连接配置）
2. 按顺序执行 `sql/` 目录下的脚本：

```bash
mysql -u root -p < sql/00_create_database.sql
mysql -u root -p fitness_db < sql/01_user.sql
mysql -u root -p fitness_db < sql/02_membership.sql
mysql -u root -p fitness_db < sql/03_exercise.sql
mysql -u root -p fitness_db < sql/04_workout.sql
mysql -u root -p fitness_db < sql/05_course.sql
mysql -u root -p fitness_db < sql/06_system.sql
mysql -u root -p fitness_db < sql/07_seed_data.sql
```

或者用 Navicat / MySQL Workbench 等图形工具依次执行这 8 个文件。

---

## 第二步：启动后端

```bash
cd fitness-backend
mvn spring-boot:run
```

首次运行会下载依赖（约 200MB），请耐心等待。

启动成功标志：控制台出现 `Started FitnessApplication` 字样。

---

## 第三步：启动前端

```bash
cd fitness-frontend
npm install      # 首次运行需要安装依赖
npm run dev
```

启动成功后访问：**http://localhost:5173**

---

## Phase 1 验收检查点

启动前后端后，逐一验证以下内容：

| # | 验收项 | 地址 | 预期结果 |
|---|--------|------|----------|
| 1 | 后端健康检查 | http://localhost:8080/api/health | 返回 `{"code":200,"message":"success","data":"ok"}` |
| 2 | API 文档 | http://localhost:8080/doc.html | 能看到 Knife4j 接口文档页面 |
| 3 | 前端首页 | http://localhost:5173 | 自动跳转到 `/login`，显示"登录页面" |
| 4 | 404 页面 | http://localhost:5173/not-exist | 显示"404 - 页面不存在" |
| 5 | 403 页面 | http://localhost:5173/403 | 显示"403 - 无权限访问" |
| 6 | 路由守卫 | http://localhost:5173/admin | 未登录时自动跳转回 `/login` |

---

## 目录结构

```
FitPro/
├── fitness-backend/     # Spring Boot 后端
├── fitness-frontend/    # Vue 3 前端
├── sql/                 # 数据库建表脚本
└── docs/                # 开发文档
```

---

## 种子账号（Phase 2 完成后可用）

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | admin123 |
| 教练 | coach01 | coach123 |
