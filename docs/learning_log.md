# FitPro 学习日志

> 角色：资深全栈工程师老师，面向完全不懂编程的小白讲解
> 格式：每个 Phase 一条记录，逐概念展开，结尾附学员心得区

# FitPro 学习日志

> 角色：资深全栈工程师老师，面向完全不懂编程的小白讲解
> 格式：每个 Phase 一条记录，逐概念展开，结尾附学员心得区

---

## [2026-04-17] Phase 1.1 — 后端项目初始化（小白版）

### 本节目标

这一步我们用 Spring Boot 框架搭建了一个"空房子"——虽然还没有家具（业务代码），但水电气网都接好了（依赖库都装好了）。

---

### 知识点讲解

#### 1. Spring Boot 是什么？

**它是什么？**
Spring Boot 是一个 Java 框架，就像盖房子的"脚手架"。没有它，从零开始写 Web 服务器要写几千行代码；有了它，只需要几十行。

**为什么用 Spring Boot？**
- 自动配置：不用手动配置数据库连接、日志等
- 内置 Tomcat：不用单独安装 Web 服务器
- 生态丰富：有成千上万的插件库可用

#### 2. Maven 与 pom.xml

**Maven 是什么？**
Maven 是 Java 的"包管理工具"，就像 npm（JavaScript）或 pip（Python）。

**pom.xml 是什么？**
`pom.xml` 是一个配置文件，列出项目需要的所有依赖库。就像购物清单：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```
意思是："我需要 MyBatis-Plus 3.5.7 这个库"。Maven 会自动下载并安装。

**为什么要用 pom.xml？**
- 集中管理依赖版本
- 避免手动下载 jar 包
- 团队成员只需要 `mvn install` 就能装好所有依赖

#### 3. 依赖库介绍

**MyBatis-Plus**
- 作用：操作数据库的框架
- 为什么用它：比原生 SQL 更简洁，自动生成 CRUD 代码

**Spring Security**
- 作用：处理用户认证和权限
- 为什么用它：安全性有保障，不用自己写登录逻辑

**Redis**
- 作用：内存数据库，速度极快
- 为什么用它：存储 Token、缓存数据，减轻 MySQL 压力

**Knife4j**
- 作用：自动生成 API 文档
- 为什么用它：前端开发者可以直接看接口文档，不用问后端

**Lombok**
- 作用：减少重复代码
- 为什么用它：自动生成 getter/setter，代码更简洁

**JJWT**
- 作用：生成和验证 JWT Token
- 为什么用它：实现无状态认证，适合分布式系统

#### 4. application.yml 配置文件

**它是什么？**
`application.yml` 是项目的"配置说明书"，告诉 Spring Boot：
- 数据库在哪里
- Redis 在哪里
- 日志怎么输出
- 等等

**为什么要分 dev 和 prod？**
- `application-dev.yml`：开发环境，数据库用本地 localhost，日志输出到控制台
- `application-prod.yml`：生产环境，数据库用远程服务器，日志写到文件

这样做的好处：
- 开发时不会误操作生产数据库
- 生产环境配置更安全（密码从环境变量读取）

#### 5. 启动类（FitnessApplication.java）

**它是什么？**
启动类是程序的"入口"，就像 main 函数。

**@SpringBootApplication 是什么？**
这个注解告诉 Spring Boot："这是一个 Spring Boot 应用，帮我自动配置"。

**@MapperScan 是什么？**
这个注解告诉 MyBatis："去 `com.fitness.module.**.mapper` 这个路径下找所有 Mapper 接口，自动注册"。

#### 6. 多环境配置的好处

**场景：**
- 本地开发：连接本地 MySQL 和 Redis
- 测试环境：连接测试服务器
- 生产环境：连接生产服务器

**不用多环境配置的后果：**
- 开发完了要手动改配置才能上线，容易出错
- 生产环境的密码可能被提交到 Git，安全隐患

**用多环境配置的好处：**
- 一套代码，三套配置
- 启动时指定 `--spring.profiles.active=prod`，自动用生产配置
- 密码存在环境变量，不提交到 Git

---

### 我们做了什么

#### 项目结构

```
fitness-backend/
├── pom.xml                    # 依赖配置
├── src/main/java/com/fitness/
│   ├── FitnessApplication.java    # 启动类
│   ├── common/                    # 通用组件（后续补充）
│   ├── config/                    # 配置类（后续补充）
│   ├── module/                    # 业务模块（后续补充）
│   ├── security/                  # 安全模块（后续补充）
│   └── util/                      # 工具类（后续补充）
└── src/main/resources/
    ├── application.yml            # 主配置
    ├── application-dev.yml        # 开发环境
    └── application-prod.yml       # 生产环境
```

#### 关键配置

- **Spring Boot 3.2.5**：最新稳定版
- **Java 17**：长期支持版本
- **Maven 3.8+**：支持 Java 17
- **MyBatis-Plus 3.5.7**：最新版本
- **Knife4j 4.4.0**：支持 Jakarta（Spring Boot 3 用 Jakarta 而不是 javax）

#### 为什么用 Jakarta？

Spring Boot 3 之前用 `javax` 包名，Spring Boot 3 改成 `jakarta` 包名。这是 Java 社区的一次大迁移。

---

### 本节小结

- 学到了：Spring Boot 是 Java Web 开发的脚手架，大大简化开发
- 学到了：pom.xml 是依赖清单，Maven 自动下载和管理
- 学到了：多环境配置（dev/prod）是最佳实践，避免误操作
- 学到了：启动类用 `@SpringBootApplication` 标注，是程序入口
- 记住：`@MapperScan` 自动扫描 Mapper；`application-dev.yml` 用本地配置；生产密码不提交 Git

---
> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[ ] 已学完
> 心得：

---

## [2026-04-17] Phase 1.2 — 通用组件层（小白版）

### 本节目标

这一步我们搭建了后端的"公共基础设施"——就像盖楼之前先铺好水电管道。
做完之后，后续所有业务代码都能直接用这些工具，不用重复造轮子。

---

### 知识点讲解

#### 1. Result.java — 统一响应格式

**它是什么？**
每次前端向后端发请求，后端都要回一个"答复"。如果每个接口的答复格式都不一样，前端就会很混乱。`Result` 就是一个标准信封，所有答复都装进这个信封再寄出去。

**信封长什么样？**
```json
{
  "code": 200,       // 状态码，200=成功，400=参数错误，500=服务器出错
  "message": "success",  // 描述信息
  "data": { ... }    // 真正的数据内容
}
```

**为什么需要它？**
没有它的话，有的接口返回 `{ "user": {...} }`，有的返回 `{ "result": [...] }`，前端每次都要猜数据在哪个字段里，非常痛苦。

**代码解读：**
```java
// <T> 是"泛型"，意思是"数据可以是任意类型"
// 就像信封可以装信、装照片、装文件——都是同一种信封
public class Result<T> {
    private int code;       // 状态码
    private String message; // 消息
    private T data;         // 数据（T 代表任意类型）

    // 成功但不需要返回数据（比如删除操作）
    public static <T> Result<T> success() { ... }

    // 成功并返回数据（比如查询操作）
    public static <T> Result<T> success(T data) { ... }

    // 失败，返回错误码和错误信息
    public static <T> Result<T> error(int code, String message) { ... }
}
```

---

#### 2. PageResult.java — 分页响应

**它是什么？**
当数据很多时（比如1000个用户），不能一次全部返回，要分页显示（每页20条）。`PageResult` 就是专门装分页数据的信封。

**为什么需要分页？**
想象一下图书馆有10万本书，如果一次全部搬出来，你根本看不完，还会把桌子压垮。分页就是"每次只拿第1页的20本"。

**它包含什么？**
```json
{
  "list": [...],    // 当前页的数据列表
  "total": 1000,    // 总共有多少条数据
  "pageNum": 1,     // 当前是第几页
  "pageSize": 20    // 每页多少条
}
```

**`IPage` 是什么？**
MyBatis-Plus（数据库查询框架）查询分页数据后，会把结果包装成 `IPage` 对象。我们的 `PageResult.of(page)` 方法就是把 `IPage` 转换成我们自己的格式。

---

#### 3. BaseEntity.java — 实体基类

**它是什么？**
"实体"（Entity）就是数据库里一张表对应的 Java 类。比如用户表对应 `UserEntity`，课程表对应 `CourseEntity`。

**基类是什么？**
基类就像所有员工都要签的"通用合同条款"——不管你是前台还是工程师，都有工号、入职日期、离职日期这些公共字段。`BaseEntity` 就是这份通用条款，所有实体类都继承它。

**它包含哪些字段？**
```java
@TableId(type = IdType.ASSIGN_ID)  // 主键，用"雪花算法"自动生成唯一ID
private Long id;

@TableField(fill = FieldFill.INSERT)  // 插入时自动填入当前时间
private LocalDateTime createdAt;      // 创建时间

@TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时都自动填入
private LocalDateTime updatedAt;             // 更新时间

@TableLogic  // 逻辑删除标记
private Integer deleted;  // 0=正常，1=已删除
```

**什么是雪花算法（Snowflake）？**
生成全球唯一ID的算法，就像每个人的身份证号码——不管在哪台服务器上生成，都不会重复。

**什么是逻辑删除？**
真正的删除是把数据从数据库里抹掉（找不回来了）。逻辑删除是把 `deleted` 字段从 `0` 改成 `1`，数据还在，只是查询时自动过滤掉。就像把文件放进回收站，而不是彻底删除。

---

#### 4. BusinessException.java — 业务异常

**它是什么？**
程序运行时会出现两种错误：
- **系统错误**：服务器崩了、数据库连不上（这是意外）
- **业务错误**：用户名已存在、余额不足、课程已满（这是预期内的情况）

`BusinessException` 专门用来表示"业务错误"，让我们能优雅地告诉用户"你的操作不符合规则"。

**为什么要继承 `RuntimeException`？**
Java 里异常分两种：
- 受检异常（Checked Exception）：必须在代码里显式处理，很麻烦
- 非受检异常（RuntimeException）：可以不处理，由全局异常处理器统一捕获

我们选 `RuntimeException`，这样业务代码里直接 `throw new BusinessException(...)` 就行，不用到处写 `try-catch`。

---

#### 5. GlobalExceptionHandler.java — 全局异常处理器

**它是什么？**
想象一个"客服中心"——不管哪个部门出了问题，都统一由客服中心对外回复，保证回复格式一致、措辞得体。

`GlobalExceptionHandler` 就是这个客服中心，它拦截所有未处理的异常，统一转换成 `Result` 格式返回给前端。

**它处理哪些异常？**
```java
// 业务异常 → 返回业务错误码和消息
@ExceptionHandler(BusinessException.class)
// 例：用户名已存在 → { "code": 1001, "message": "用户名已存在" }

// 参数校验失败 → 返回 400
@ExceptionHandler(MethodArgumentNotValidException.class)
// 例：密码太短 → { "code": 400, "message": "密码长度不能少于6位" }

// 没有权限 → 返回 403
@ExceptionHandler(AccessDeniedException.class)
// 例：普通用户访问管理员接口 → { "code": 403, "message": "无权限访问" }

// 其他所有未知错误 → 返回 500
@ExceptionHandler(Exception.class)
// 同时在服务器日志里记录详细错误信息，方便排查
```

**`@RestControllerAdvice` 是什么？**
这个注解告诉 Spring："这个类是全局的异常处理器，所有 Controller 抛出的异常都交给它处理"。

---

#### 6. MybatisPlusConfig.java — MyBatis-Plus 配置

**它是什么？**
MyBatis-Plus（简称 MP）是帮我们操作数据库的框架。这个配置类给 MP 装了两个"插件"：

**插件一：分页插件（PaginationInnerInterceptor）**
没有这个插件，MP 的分页查询不会自动生成 `LIMIT` 语句，查出来的是全部数据。装上之后，传入"第几页、每页多少条"，MP 自动帮你加 `LIMIT 0, 20` 这样的 SQL。

**插件二：自动填充（MetaObjectHandler）**
还记得 `BaseEntity` 里的 `createdAt` 和 `updatedAt` 吗？每次插入/更新数据时，我们不想手动写 `entity.setCreatedAt(LocalDateTime.now())`，太麻烦了。

`MetaObjectHandler` 就是自动帮你填这些字段的"小助手"：
- 插入数据时：自动填 `createdAt` 和 `updatedAt` 为当前时间
- 更新数据时：自动更新 `updatedAt` 为当前时间

---

### 我们做了什么

#### `common/Result.java`
后端所有接口的"标准信封"，前端收到的每个响应都长一个样子，方便统一处理。

#### `common/PageResult.java`
分页数据的专用信封，告诉前端"总共多少条、当前第几页、这页的数据是什么"。

#### `common/BaseEntity.java`
所有数据库实体的"公共基因"，自动带上 ID、创建时间、更新时间、逻辑删除标记。

#### `common/exception/BusinessException.java`
业务规则被违反时抛出的异常，携带错误码，让前端知道具体是什么业务问题。

#### `common/exception/GlobalExceptionHandler.java`
统一的异常"翻译官"，把各种异常翻译成标准的 `Result` 格式返回给前端。

#### `config/MybatisPlusConfig.java`
给数据库框架装上分页插件和自动填充插件，省去大量重复代码。

---

### 本节小结

- 学到了：用泛型 `Result<T>` 统一所有接口的响应格式，前端只需要处理一种格式
- 学到了：`BaseEntity` 基类让所有实体自动拥有公共字段，避免重复定义
- 学到了：逻辑删除比物理删除更安全，数据可以恢复
- 学到了：全局异常处理器让业务代码更干净，不用到处写 `try-catch`
- 记住：`@RestControllerAdvice` = 全局异常处理；`@TableLogic` = 逻辑删除；`MetaObjectHandler` = 自动填充时间

---

## [2026-04-17] Phase 1.3 — 数据库初始化（小白版）

### 本节目标

数据库是系统的"仓库"，所有数据都存在这里。这一步我们建好了 19 张表，就像在仓库里划分了 19 个货架，每个货架放不同类型的东西。

---

### 知识点讲解

#### 1. 数据库与表的概念

**数据库是什么？**
数据库就是一个"超级 Excel"。Excel 有多个工作表，数据库也有多个表。每个表都有行和列：
- 行 = 一条记录（比如一个用户）
- 列 = 一个字段（比如用户名、密码、邮箱）

**为什么要建表？**
没有表的话，数据就乱成一团。有了表，数据就有了结构，程序就能快速查找和修改。

#### 2. 主键（Primary Key）

**它是什么？**
主键就是每条记录的"身份证号"，全球唯一，不能重复。

**为什么需要主键？**
想象一个班级有 30 个学生，都叫"小明"。老师要找某个小明，就找不到了。但如果每个学生有学号（主键），就能唯一确定是谁。

**雪花算法（Snowflake）是什么？**
一种生成全球唯一 ID 的算法。它根据时间戳 + 机器 ID + 序列号组合，保证即使在分布式系统中也不会重复。就像每个人的身份证号一样，全国唯一。

#### 3. 外键与关系

**外键是什么？**
外键是表与表之间的"连接线"。比如"会籍表"里有 `user_id`，它指向"用户表"的 `id`，这就是外键。

**为什么要有外键？**
外键保证数据的一致性。比如，不能给一个不存在的用户办卡。

**注意：** 我们的设计中，数据库里不建外键约束（只在 ER 图中体现关系），因为：
- 外键约束会降低插入/删除速度
- 应用层代码可以更灵活地处理关系
- 这是互联网公司的常见做法

#### 4. 索引（Index）

**它是什么？**
索引就像书的"目录"。没有目录，要找某个知识点，得从第一页翻到最后。有了目录，直接跳到对应页码。

**常见索引类型：**
- `PRIMARY KEY`：主键索引，最快
- `UNIQUE KEY`：唯一索引，保证字段值不重复（比如用户名）
- `KEY`：普通索引，加快查询速度

**例子：**
```sql
KEY idx_user_username (username)  -- 按用户名查询会很快
KEY idx_body_record_user_id (user_id)  -- 按用户 ID 查询会很快
```

#### 5. 逻辑删除 vs 物理删除

**物理删除：**
```sql
DELETE FROM sys_user WHERE id = 123;  -- 数据彻底消失
```
问题：数据找不回来了，如果误删就完蛋了。

**逻辑删除：**
```sql
UPDATE sys_user SET deleted = 1 WHERE id = 123;  -- 只是标记为已删除
SELECT * FROM sys_user WHERE deleted = 0;  -- 查询时自动过滤
```
优点：数据还在，可以恢复；符合法律要求（有些行业要求保留数据）。

#### 6. 字符集与排序规则

**utf8mb4 是什么？**
- `utf8`：最多 3 字节，无法存储某些特殊符号（比如 emoji）
- `utf8mb4`：最多 4 字节，可以存储全世界所有字符，包括 emoji

**为什么选 utf8mb4？**
现在用户可能输入 emoji（😀），如果用 `utf8` 会报错。`utf8mb4` 虽然占空间多一点，但更安全。

#### 7. 种子数据

**它是什么？**
种子数据是系统初始化时预先插入的数据。比如：
- 管理员账号（这样系统才能启动）
- 运动分类（这样用户才能选择）
- 会员卡种（这样才能办卡）

**为什么要用固定 ID？**
种子数据用固定大数（`1000000000000000001`），而程序生成的 ID 用雪花算法（基于时间戳，很大但不会这么小）。这样就不会冲突。

---

### 我们做了什么

#### 8 个 SQL 文件

1. **00_create_database.sql** — 建库，指定字符集为 utf8mb4
2. **01_user.sql** — 用户表 + 身体数据表
3. **02_membership.sql** — 会籍卡种 + 会员会籍 + 签到记录
4. **03_exercise.sql** — 运动分类 + 运动动作
5. **04_workout.sql** — 训练模板 + 计划 + 记录（7 张表）
6. **05_course.sql** — 课程 + 排课 + 预约
7. **06_system.sql** — 公告 + 操作日志
8. **07_seed_data.sql** — 初始数据（管理员、教练、分类、卡种）

#### 关键设计

- **预约表的唯一索引**：`UNIQUE KEY (user_id, schedule_id)` 防止同一用户重复预约
- **逻辑删除**：所有业务表都有 `deleted` 字段，删除时只改这个字段
- **自动时间戳**：所有表都有 `created_at` 和 `updated_at`，由应用层自动填充

---

### 本节小结

- 学到了：数据库是"超级 Excel"，表是行列结构，主键是唯一身份证
- 学到了：雪花算法生成全球唯一 ID，避免分布式系统中的 ID 冲突
- 学到了：逻辑删除比物理删除更安全，数据可以恢复
- 学到了：索引就像书的目录，加快查询速度
- 记住：`utf8mb4` 支持 emoji；外键约束可选；种子数据用固定大 ID

---
> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[ ] 已学完
> 心得：

