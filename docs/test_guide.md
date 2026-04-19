# FitPro 测试指南

> 本指南提供从 Phase 1 到 Phase 2.4（当前完成阶段）的所有测试用例，帮助你验证系统功能是否正常工作。

---

## 📋 测试账户信息

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | `admin` | `admin123` | 种子数据中已预置，可访问所有管理端功能 |
| 示例教练 | `coach01` | `coach123` | 种子数据中已预置，角色为 COACH |
| 新注册会员 | 任意 | 任意 | 通过注册页面创建，角色默认为 MEMBER |

**注意**：所有密码在数据库中均使用 BCrypt 加密存储。

---

## 🛠️ 测试环境准备

### 1. 数据库与 Redis
- **MySQL**: 确保本地 MySQL 服务运行在 `localhost:3306`
  - 数据库名: `fitpro`
  - 用户名: `root`
  - 密码: `Nan123123`
  - 可通过 `all_tables.sql` 一键导入（已包含 19 张表 + 种子数据）
- **Redis**: 确保本地 Redis 服务运行在 `localhost:6379`，无需密码

### 2. 后端服务
- 启动命令: `mvn spring-boot:run`（在 `fitness-backend/` 目录下执行）
- 服务地址: `http://localhost:8080`
- API 文档: `http://localhost:8080/doc.html`（Knife4j 接口文档）

### 3. 前端服务
- 启动命令: `npm run dev`（在 `fitness-frontend/` 目录下执行）
- 访问地址: `http://localhost:5173`
- 开发代理: 前端 `/api` 请求自动转发到 `http://localhost:8080`

### 4. 工具准备
- **Knife4j 接口文档**：用于手动测试后端 API
- **浏览器开发者工具**（F12）：查看网络请求、控制台输出
- **Postman/Insomnia**（可选）：用于更复杂的 API 测试

---

## ✅ Phase 1 测试用例

### 1.1 健康检查接口
**测试目的**：验证前后端基本连通性。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 启动后端服务（`mvn spring-boot:run`） | 控制台显示 Spring Boot 启动成功，无报错 |
| 2 | 浏览器访问 `http://localhost:8080/api/health` | 返回 JSON: `{"code":200,"message":"success","data":"ok"}` |
| 3 | 启动前端服务（`npm run dev`） | 控制台显示 Vite 启动成功，访问地址 `http://localhost:5173` |
| 4 | 前端页面打开浏览器开发者工具 → 网络标签 | 能看到对 `/api/health` 的请求，状态 200 |

### 1.2 Knife4j 文档可访问
**测试目的**：验证 API 文档是否正常生成。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 浏览器访问 `http://localhost:8080/doc.html` | 显示 Knife4j 接口文档页面 |
| 2 | 在文档中找到「健康检查」接口 | 可看到 `GET /api/health` 接口描述 |
| 3 | 点击「调试」→「发送请求」 | 返回结果与直接访问相同 |

### 1.3 数据库连接验证
**测试目的**：确保后端能正确连接 MySQL 和 Redis。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 启动后端服务，观察控制台日志 | 看到 `DataSource` 和 `Redis` 连接成功的日志 |
| 2 | 在 MySQL 客户端执行 `SELECT * FROM sys_user WHERE username='admin'` | 返回一行管理员数据，密码为 BCrypt 密文 |
| 3 | 通过 Knife4j 调用任意需要数据库的接口（如注册） | 接口正常响应，无数据库连接错误 |

---

## 🔐 Phase 2 测试用例（认证体系）

### 2.1 用户注册流程
**测试目的**：验证新用户注册功能。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 前端访问 `http://localhost:5173`，点击「注册」链接 | 跳转到注册页面，显示表单 |
| 2 | 填写表单：用户名 `testuser`、密码 `test123`、确认密码 `test123`、昵称 `测试用户`、手机号 `13800138000`，勾选协议 | 表单验证通过，无红色错误提示 |
| 3 | 点击「注册」按钮 | 显示加载状态，成功后自动跳转到对应角色页面（MEMBER → `/app/profile`） |
| 4 | 查看浏览器开发者工具 → 网络标签 | 看到 `POST /api/auth/register` 请求成功（200），响应包含 `accessToken` 和 `refreshToken` |
| 5 | 在 MySQL 中查询 `SELECT * FROM sys_user WHERE username='testuser'` | 新增一条用户记录，密码为 BCrypt 密文，角色为 `MEMBER` |

### 2.2 用户登录流程
**测试目的**：验证用户登录功能。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 前端访问 `http://localhost:5173`（或点击登出后） | 显示登录页面 |
| 2 | 使用超级管理员账号登录：用户名 `admin`、密码 `admin123` | 登录成功，跳转到 `/admin/dashboard` |
| 3 | 使用新注册的账号登录：用户名 `testuser`、密码 `test123` | 登录成功，跳转到 `/app/profile` |
| 4 | 使用错误密码登录：用户名 `admin`、密码 `wrong` | 登录失败，显示「用户名或密码错误」提示 |
| 5 | 查看浏览器 LocalStorage | 看到 `token` 和 `refreshToken` 已保存 |

### 2.3 Token 验证与用户信息获取
**测试目的**：验证 Token 有效性及获取当前用户信息。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 登录成功后，打开浏览器开发者工具 → 控制台 | 无认证相关的错误日志 |
| 2 | 在 Knife4j 中调用 `GET /api/auth/me`，在请求头添加 `Authorization: Bearer {你的accessToken}` | 返回当前用户的完整信息（id、username、nickname、role 等） |
| 3 | 在前端页面刷新（F5） | 页面正常加载，不会跳回登录页（Token 自动恢复） |
| 4 | 手动删除 LocalStorage 中的 `token`，然后刷新页面 | 跳转回登录页面（无 Token 被拦截） |

### 2.4 角色权限验证
**测试目的**：验证不同角色只能访问对应路由。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 使用 `admin`（SUPER_ADMIN）登录 | 成功访问 `/admin/dashboard`，左侧显示完整管理菜单 |
| 2 | 在浏览器地址栏手动输入 `http://localhost:5173/app/profile` | 仍停留在管理端页面（不会跳转，因为角色匹配管理端） |
| 3 | 使用 `testuser`（MEMBER）登录 | 成功访问 `/app/profile`，底部显示会员端 TabBar |
| 4 | 在浏览器地址栏手动输入 `http://localhost:5173/admin/dashboard` | 跳转到 403 无权限页面，显示「您没有权限访问此页面」 |
| 5 | 使用 `coach01`（COACH）登录 | 可根据路由配置测试教练端权限（目前教练与管理员权限类似） |

### 2.5 Token 自动刷新机制
**测试目的**：验证 Access Token 过期后能自动用 Refresh Token 刷新。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 登录后，打开浏览器开发者工具 → 网络标签 | 正常请求 |
| 2 | 手动将 LocalStorage 中的 `token` 改为一个无效字符串（模拟过期） | - |
| 3 | 在前端页面触发一个 API 请求（如刷新页面） | 看到连续两个请求：<br>1. 原请求返回 401<br>2. 自动发送 `POST /api/auth/refresh` 获取新 Token<br>3. 用新 Token 重试原请求成功 |
| 4 | 查看 LocalStorage | `token` 已更新为新值 |
| 5 | 将 `refreshToken` 也改为无效字符串，重复步骤 3 | 自动跳转到登录页面（Refresh Token 也失效） |

### 2.6 用户登出流程
**测试目的**：验证用户登出功能。

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 登录后，在前端找到登出按钮（通常在右上角用户菜单） | 点击登出 |
| 2 | 观察网络请求 | 看到 `POST /api/auth/logout` 请求成功 |
| 3 | 检查 LocalStorage | `token` 和 `refreshToken` 已被清除 |
| 4 | 页面跳转 | 自动跳转到登录页面 |
| 5 | 尝试访问需要认证的页面（如 `/app/profile`） | 被路由守卫拦截，跳转登录页 |

---

## 📝 测试结果记录表

复制以下表格，记录你的测试结果：

### Phase 1 测试结果

| 测试用例 | 测试步骤 | 预期结果 | 实际结果 | 状态（✓/✗） | 备注 |
|----------|----------|----------|----------|-------------|------|
| 健康检查接口 | 1-4 | 前后端连通正常 | 正常连通 | √ | |
| Knife4j 文档可访问 | 1-3 | 文档正常显示，接口可调试 | 正常调试 | √ | |
| 数据库连接验证 | 1-3 | 数据库连接成功，无错误 | 正常连接 | √ | |

### Phase 2 测试结果

| 测试用例 | 测试步骤 | 预期结果 | 实际结果 | 状态（✓/✗） | 备注 |
|----------|----------|----------|----------|-------------|------|
| 用户注册流程 | 1-5 | 新用户注册成功，数据入库 | 可以正常注册 | √ | |
| 用户登录流程 | 1-5 | 登录成功/失败符合预期 | 用户密码正确的话可以正常登录，超级管理员登录不了，显示服务器错误。如果输入密码错误显示的是：服务器错误。教练账号登录不了：服务器错误。 | × | |
| Token 验证与用户信息获取 | 1-4 | Token 有效，能获取用户信息 | 可以获取token信息，但是我用user用户登录，返回的信息里面role是ADMIN,username是admin，好像不正确。 | × | |
| 控制台错误 | 1-5 | 控制台错误 | 错误信息：Uncaught (in promise) fetchError: Request timeout after 30000ms<br/>    at CT (content_script.js:4891:12015)<br/>    at e.sendFrom (content_script.js:4891:2977)<br/>    at async f3.sendWithHealth (content_script.js:4891:16516)<br/>    at async EV (content_script.js:4891:22033)<br/>    at async he (content_script.js:4891:20203)<br/>    at async content_script.js:4891:217463 | × | 这是我自己加的 |
| Token 自动刷新机制 | 1-5 | Token 过期后自动刷新 | 没有进行完整测试，后续再测试 | × | |
| 用户登出流程 | 1-5 | 登出后 Token 清除，跳转登录页 | 前端没有找到登出按钮 | × | |

---

## 🔧 常见问题排查

### 1. 后端启动失败
- **错误**：`java.sql.SQLException: Access denied for user 'root'@'localhost'`
  - **解决**：检查 `application-dev.yml` 中的 MySQL 密码是否正确（默认为 `Nan123123`）
- **错误**：`Failed to configure a DataSource`
  - **解决**：确保 MySQL 服务已启动，数据库 `fitpro` 已创建
- **错误**：`Connection refused: localhost:6379`
  - **解决**：启动 Redis 服务，或检查 Redis 配置

### 2. 前端启动失败
- **错误**：`Cannot find module 'xxx'`
  - **解决**：在 `fitness-frontend/` 目录下执行 `npm install` 安装依赖
- **错误**：`Proxy error: Could not proxy request /api/health`
  - **解决**：确保后端服务已在 `localhost:8080` 启动

### 3. API 请求失败
- **错误**：`401 Unauthorized`
  - **解决**：检查 Token 是否有效，或重新登录获取新 Token
- **错误**：`403 Forbidden`
  - **解决**：当前用户角色无权访问该接口/页面
- **错误**：`404 Not Found`
  - **解决**：检查接口路径是否正确，后端 Controller 是否有对应映射

### 4. 数据库问题
- **错误**：`Duplicate entry 'xxx' for key 'idx_user_username'`
  - **解决**：用户名已存在，换一个用户名注册
- **错误**：数据未保存
  - **解决**：检查 MyBatis-Plus 的 `created_at`、`updated_at` 自动填充是否配置正确

---

## 📈 下一步测试建议

完成 Phase 1-2 测试后，下一步可进行：

1. **Phase 3.1 用户模块测试**：用户 CRUD、个人中心编辑、密码修改
2. **Phase 3.2 身体数据模块测试**：身体数据录入、历史查询
3. **自动化测试**：使用 Postman Collection 或编写 Jest 测试脚本
4. **性能测试**：模拟多用户并发注册/登录，观察系统响应

---

> **提示**：每次代码更新后，建议重新运行相关测试用例，确保新功能不影响现有功能。

---

## 🔧 Bug 修复记录

### [2026-04-19] Phase 2.2 — 修复管理员/教练登录失败及用户信息返回错误

**问题**：用户在测试中发现：
1. 超级管理员和教练账号登录显示"服务器错误"
2. 使用新注册用户登录后，获取的用户信息返回admin信息（role=ADMIN, username=admin）
3. 前端会员端没有找到登出按钮

**修复内容**：
1. **修复管理员/教练登录失败**：
   - 修改 `all_tables.sql` 中管理员和教练账号的 `status` 字段从 `1`（禁用）改为 `0`（正常）
   - 创建数据库更新脚本 `update.sql`，可单独执行更新现有数据库
   
2. **修复用户信息返回错误**：
   - 在 `AuthServiceImpl.getCurrentUserInfo()` 方法中添加详细日志，便于调试用户信息获取问题
   - 日志输出：principal对象、解析的userId、查询到的用户信息
   
3. **添加会员端登出按钮**：
   - 修改 `ProfileView.vue`，在个人中心页面添加退出登录按钮
   - 按钮调用 auth store 的 logout 方法，登出后跳转到登录页

**验证方法**：
1. 重新导入数据库或执行 `update.sql` 更新现有数据库
2. 重新启动后端服务，使用 admin/admin123 登录，观察是否成功
3. 使用新注册用户登录，打开浏览器开发者工具查看网络请求，确认 `/auth/me` 返回正确的用户信息
4. 在会员端个人中心页面查看是否有"退出登录"按钮

**注意事项**：
- 如果问题依然存在，请查看后端日志中 `getCurrentUserInfo` 的调试输出，确认 principal 和 userId 是否正确
- 管理员角色在数据库中是 `SUPER_ADMIN`，前端期望的角色前缀是 `ROLE_SUPER_ADMIN`（已在前次修复中处理）

