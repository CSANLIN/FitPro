# FitPro 系统架构设计

## 1. 整体架构图

```mermaid
graph TB
    subgraph 客户端
        A[PC 浏览器 - 管理端]
        B[移动端浏览器 - 会员端]
    end

    subgraph 前端层 - Vue 3
        C[Nginx / Vite Dev Server]
        subgraph Vue 应用
            D[Vue Router 路由层]
            E[Pinia 状态管理]
            F[Axios HTTP 层]
            G[ECharts 图表]
        end
    end

    subgraph 后端层 - Spring Boot 3.2
        subgraph 接入层
            H[Spring Security + JWT Filter]
            I[CORS Filter]
            J[Global Exception Handler]
        end
        subgraph 业务层
            K[Auth Service]
            L[User Service]
            M[Course Service]
            N[Workout Service]
            O[Exercise Service]
            P[Membership Service]
            Q[CheckIn Service]
            R[System Service]
        end
        subgraph 数据层
            S[MyBatis-Plus ORM]
            T[Redis Template]
        end
    end

    subgraph 存储层
        U[(MySQL 8.0)]
        V[(Redis 7.x)]
    end

    A --> C
    B --> C
    C --> D
    D --> E
    E --> F
    F -->|HTTP /api/**| H
    H --> I
    I --> J
    J --> K & L & M & N & O & P & Q & R
    K & L & M & N & O & P & Q & R --> S & T
    S --> U
    T --> V
```

## 2. 请求处理流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant N as Nginx
    participant F as JWT Filter
    participant CT as Controller
    participant SV as Service
    participant MP as MyBatis-Plus
    participant DB as MySQL
    participant RD as Redis

    C->>N: HTTP Request
    N->>F: /api/** 转发
    F->>F: 解析 JWT Token
    alt Token 有效
        F->>CT: 放行请求
        CT->>CT: 参数校验
        CT->>SV: 调用业务逻辑
        SV->>RD: 查询缓存
        alt 缓存命中
            RD-->>SV: 返回缓存数据
        else 缓存未命中
            SV->>MP: 数据库查询
            MP->>DB: SQL 执行
            DB-->>MP: 返回结果
            MP-->>SV: 返回实体
            SV->>RD: 写入缓存
        end
        SV-->>CT: 返回业务结果
        CT-->>C: Result.success(data)
    else Token 无效/过期
        F-->>C: 401 Unauthorized
    end
```

## 3. 模块依赖关系

```mermaid
graph TD
    AUTH[auth 认证模块] --> USER[user 用户模块]
    COURSE[course 课程模块] --> USER
    COURSE --> COACH[教练管理]
    WORKOUT[workout 训练模块] --> USER
    WORKOUT --> EXERCISE[exercise 运动库模块]
    MEMBERSHIP[membership 会籍模块] --> USER
    CHECKIN[checkin 签到模块] --> USER
    CHECKIN --> MEMBERSHIP
    SYSTEM[system 系统模块]
    DASHBOARD[仪表盘] --> USER & COURSE & MEMBERSHIP & CHECKIN

    style AUTH fill:#e1f5fe
    style USER fill:#fff3e0
    style COURSE fill:#e8f5e9
    style WORKOUT fill:#fce4ec
    style EXERCISE fill:#f3e5f5
    style MEMBERSHIP fill:#fff8e1
    style CHECKIN fill:#e0f2f1
    style SYSTEM fill:#f5f5f5
```

## 4. 安全架构

```mermaid
graph LR
    subgraph 认证流程
        A[登录请求] --> B[验证账号密码]
        B --> C[生成 Access Token + Refresh Token]
        C --> D[Access Token 存客户端]
        C --> E[Refresh Token 存 Redis]
    end

    subgraph 鉴权流程
        F[API 请求 + Bearer Token] --> G{JWT Filter}
        G -->|解析成功| H[从 Token 提取角色]
        H --> I{权限注解检查}
        I -->|通过| J[执行业务]
        I -->|拒绝| K[403 Forbidden]
        G -->|Token 过期| L[用 Refresh Token 刷新]
        L -->|刷新成功| M[返回新 Token]
        L -->|刷新失败| N[401 重新登录]
    end
```

## 5. 前端架构

```mermaid
graph TB
    subgraph 路由层
        R1[/ - 登录页]
        R2[/admin - 管理端布局]
        R3[/app - 会员端布局]
    end

    subgraph 管理端页面
        R2 --> P1[Dashboard 仪表盘]
        R2 --> P2[会员管理]
        R2 --> P3[教练管理]
        R2 --> P4[课程管理]
        R2 --> P5[运动库管理]
        R2 --> P6[系统管理]
    end

    subgraph 会员端页面
        R3 --> P7[个人中心]
        R3 --> P8[课程预约]
        R3 --> P9[训练计划]
        R3 --> P10[签到打卡]
    end

    subgraph 状态管理 Pinia
        S1[useAuthStore]
        S2[useUserStore]
        S3[useCourseStore]
    end

    subgraph API 层
        A1[authApi]
        A2[userApi]
        A3[courseApi]
        A4[workoutApi]
        A5[exerciseApi]
        A6[membershipApi]
    end

    P1 & P2 & P3 & P4 & P5 & P6 & P7 & P8 & P9 & P10 --> S1 & S2 & S3
    S1 & S2 & S3 --> A1 & A2 & A3 & A4 & A5 & A6
```
