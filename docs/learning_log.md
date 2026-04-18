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
> 学完后在此处打勾并写下心得：[ √] 已学完
> 心得：感觉知识点讲解不够细，好像光这几点我都不能理解透彻。我光知道他们有什么用，但是串联不起来，对整个项目没用通透的感觉，而且去看代码也看不懂，你应该把过程写得详细一点，但是注意不要写太多代码进去费token。

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
> 学完后在此处打勾并写下心得：[√ ] 已学完
> 心得：感觉讲得太粗略了，还有就是应该像讲课一样，引导我的思路，让我建立起思路，思维。还有就是单单给个写了什么样的文件，我去找会很难找，例如1. Result.java — 统一响应格式，我很难去找到这个文件，还有文件夹也太多了，他们这个文件与文件是怎么相互协作相互调用的，联系和发展的观点看问题，可以结合点马原。统一响应格式不太懂，不够通俗。业务异常也不太懂。我们做了什么有了，但是我们为什么这么做没有，这个模块要加上，还有我们是怎么做的要更详细。数据库我想装个mcp服务器，让你来帮我管理数据库，但是我不会操作，我希望你给我切实可行的建议并写在这个章节补充。

---

## 📚 上节课心得点评

### Phase 1.1 — 后端项目初始化

**你的理解：** 感觉知识点讲解不够细，好像光这几点我都不能理解透彻。我光知道他们有什么用，但是串联不起来，对整个项目没用通透的感觉，而且去看代码也看不懂，你应该把过程写得详细一点，但是注意不要写太多代码进去费token。

✅ **理解正确：** 你指出了学习中的核心痛点——知识点孤立，缺乏系统串联。这确实是初学者最容易遇到的"只见树木不见森林"问题。

⚠️ **需要纠正：** 学习编程框架确实需要"先见森林，再见树木"。你应该先理解整个系统的架构图，知道每个组件的位置和作用，然后再深入每个组件的细节。

💡 **补充说明：**
1. **Spring Boot的"自动装配"魔法**：当你在`pom.xml`中声明`spring-boot-starter-web`依赖时，Spring Boot会自动为你配置Tomcat服务器、JSON序列化、Web MVC框架。这就像你买了一个"智能家居套装"——安装好电源线，灯光、空调、窗帘就自动联动起来了。

2. **配置文件的生命周期**：`application.yml` → `application-dev.yml` → 系统启动 → 连接数据库 → 服务就绪。这是一个清晰的"启动链"，每一步都有明确的目的。

3. **Maven的依赖解析过程**：你声明的每个`<dependency>`都会触发Maven去中央仓库下载对应的jar包及其所有依赖（依赖的依赖的依赖...），最终形成一个依赖树。可以用`mvn dependency:tree`命令查看。

🎯 **建议：**
1. **画系统架构图**：用纸笔画一下：用户请求 → 前端 → Nginx → Spring Boot → MyBatis-Plus → MySQL，标出每个环节的技术栈。
2. **运行调试体验**：在IDEA中启动项目，在`FitnessApplication.java`的main方法上点Debug，观察控制台输出，看看Spring Boot到底做了什么。
3. **阅读官方文档**：Spring Boot官方文档的"Getting Started"部分非常友好，跟着做一遍能建立直观感受。

---

### Phase 1.3 — 数据库初始化

**你的理解：** 感觉讲得太粗略了，还有就是应该像讲课一样，引导我的思路，让我建立起思路，思维。还有就是单单给个写了什么样的文件，我去找会很难找，例如1. Result.java — 统一响应格式，我很难去找到这个文件，还有文件夹也太多了，他们这个文件与文件是怎么相互协作相互调用的，联系和发展的观点看问题，可以结合点马原。统一响应格式不太懂，不够通俗。业务异常也不太懂。我们做了什么有了，但是我们为什么这么做没有，这个模块要加上，还有我们是怎么做的要更详细。数据库我想装个mcp服务器，让你来帮我管理数据库，但是我不会操作，我希望你给我切实可行的建议并写在这个章节补充。

✅ **理解正确：** 你要求"引导思路、建立思维框架"这个诉求非常关键！编程不仅是写代码，更是建立解决问题的思维模型。

⚠️ **需要纠正：** 文件查找困难是项目结构不熟悉导致的，这需要时间积累。每个框架都有自己的"约定大于配置"思想，理解了这个思想，文件夹结构就自然清晰了。

💡 **补充说明：**

#### 1. **从哲学角度看软件架构（马原联系）**
- **联系的观点**：每个Java类都不是孤立的。`Result.java`被`GlobalExceptionHandler.java`使用，`GlobalExceptionHandler`又被所有Controller间接使用。这是一个"作用与反作用"的关系链。
- **发展的观点**：从`Result`（基础响应）→ `PageResult`（分页响应）→ `BusinessException`（业务异常）→ `GlobalExceptionHandler`（统一处理），这是一个"从简单到复杂、从抽象到具体"的辩证发展过程。
- **矛盾的观点**：`BaseEntity`的`deleted`字段体现了"逻辑删除"与"物理删除"的矛盾统一——既要删除（业务逻辑），又要保留（数据安全）。

#### 2. **通俗版解释**
- **Result.java就像快递包裹**：`code`是"签收状态"（200=已签收，400=地址错误，500=快递丢件），`message`是"快递员备注"，`data`是"包裹内容"。
- **业务异常就像交通规则**：红灯停、绿灯行是"业务规则"，闯红灯就是"违反业务规则"，交警开罚单就是"抛出BusinessException"。
- **全局异常处理器就像客服中心**：不管哪个部门出问题（技术部、物流部、财务部），都统一由客服中心对外回复，保证口径一致。

#### 3. **MCP服务器安装建议**
MCP（Model Context Protocol）服务器可以让你在Claude中直接操作数据库，非常方便！

**安装步骤：**
1. **安装Node.js**：到官网下载安装包，一直点"下一步"即可
2. **安装sqlite3 MCP服务器**：
   ```bash
   npm install -g @modelcontextprotocol/server-sqlite
   ```
3. **在Claude Code中配置**：编辑`~/.claude/settings.json`，添加：
   ```json
   {
     "mcpServers": {
       "sqlite": {
         "command": "npx",
         "args": ["@modelcontextprotocol/server-sqlite", "你的数据库文件路径"]
       }
     }
   }
   ```
4. **重启Claude Code**：配置生效后，你可以直接用自然语言操作数据库："查询所有用户"、"添加一条记录"等。

**更简单的方案**：使用MySQL Workbench或Navicat等图形化工具，可视化操作数据库，直观易懂。

#### 4. **"为什么这么做"的思考框架**
- **统一响应格式为什么重要？** → 因为前端开发需要稳定的接口约定，减少沟通成本
- **逻辑删除为什么安全？** → 因为误删可以恢复，符合数据安全法规
- **雪花算法为什么适合分布式？** → 因为时间戳递增，不同机器生成不冲突
- **utf8mb4为什么必要？** → 因为用户可能输入emoji表情，utf8存不了会乱码

🎯 **建议：**
1. **画依赖关系图**：在纸上画一个"类调用关系图"，用箭头表示"谁调用了谁"。
2. **动手实践**：创建一个简单的测试Controller，故意抛出各种异常，观察`GlobalExceptionHandler`如何处理。
3. **阅读MyBatis-Plus文档**：理解`@TableId`、`@TableLogic`、`@TableField`这些注解的实际效果。
4. **使用数据库可视化工具**：先建立直观感受，再深入SQL语句。

---

## [2026-04-18] Phase 1.4 — 前端项目初始化（小白版）

### 本节目标

前端是系统的"店面"，用户直接看到和操作的界面。这一步我们搭建了一个现代化的 Vue 3 前端项目，就像装修店面：布置好货架（布局组件）、安装收银机（状态管理）、培训店员（路由导航）。

---

### 知识点讲解

#### 1. Vite 是什么？

**它是什么？**
Vite 是前端项目的"快速启动器 + 打包工具"。以前的工具（如 Webpack）启动慢，改一行代码等10秒才看到效果。Vite 启动快，改代码即时更新。

**为什么用 Vite 不用 Webpack？**

- 启动快：Vite 用原生 ES 模块，不用打包就能启动
- 热更新快：改代码立即生效，不用刷新页面
- 配置简单：开箱即用，不用写几百行配置

**Vite 的核心功能：**
- 开发服务器：启动 `localhost:5173`，提供实时预览
- 打包工具：把 Vue 代码转换成浏览器能懂的 HTML/CSS/JS
- 插件系统：可以扩展功能（如自动导入组件）

#### 2. Vue 3 的 Composition API

**Vue 2 vs Vue 3：**
Vue 2 用 Options API（选项式），把数据、方法、计算属性分开写：
```javascript
export default {
  data() { return { count: 0 } },
  methods: { increment() { this.count++ } }
}
```

Vue 3 用 Composition API（组合式），把相关逻辑放一起：
```javascript
import { ref } from 'vue'
const count = ref(0)
const increment = () => { count.value++ }
```

**为什么用 Composition API？**
- 逻辑复用：可以把一段逻辑抽成"组合式函数"（composable）
- 类型支持：对 TypeScript 更友好
- 代码组织：相关逻辑放一起，不用跳来跳去

#### 3. `<script setup>` 语法糖

**它是什么？**
`<script setup>` 是 Vue 3 的简化写法，让你写更少的代码。

**传统写法 vs `<script setup>`：**
```vue
<!-- 传统写法 -->
<script>
export default {
  props: ['title'],
  setup(props) {
    const count = ref(0)
    return { count }
  }
}
</script>

<!-- <script setup> 写法 -->
<script setup>
const props = defineProps(['title'])
const count = ref(0)
</script>
```

**优点：**
- 不用写 `export default`
- 不用写 `setup()` 函数
- 不用写 `return`
- 自动暴露所有顶层变量给模板

#### 4. Vue Router 路由系统

**路由是什么？**
路由就是"网址"和"页面组件"的映射关系。比如：
- `/login` → 显示登录页面组件
- `/admin/dashboard` → 显示管理端仪表盘组件

**为什么需要路由？**
没有路由的话，整个网站只有一个页面，无法切换不同内容。路由让单页面应用（SPA）能像多页面网站一样工作。

**路由守卫是什么？**
路由守卫就像"门卫"，在进入页面之前检查你的"通行证"（Token）：
- 没登录？→ 跳转到登录页
- 没权限？→ 显示 403 页面
- 已登录？→ 放行

#### 5. Pinia 状态管理

**状态是什么？**
状态就是应用程序的"记忆"。比如用户登录后，系统要"记住"用户信息、Token，这些就是状态。

**为什么需要状态管理？**
没有状态管理的话，组件之间传递数据很麻烦（像击鼓传花）。有了 Pinia，所有组件都可以直接从"中央仓库"读取和修改状态。

**Pinia vs Vuex：**
- Pinia：Vue 官方推荐，TypeScript 友好，API 更简单
- Vuex：老牌状态管理，复杂一些
- 我们选 Pinia，因为更现代、更简单

#### 6. Element Plus UI 组件库

**UI 组件库是什么？**
UI 组件库就是一套"预制装修材料"。不用自己造轮子（写按钮、表格、弹窗），直接用现成的，风格统一、功能完善。

**Element Plus 特点：**
- 组件丰富：表格、表单、弹窗、导航等一应俱全
- 主题可定制：可以改颜色、圆角等样式
- 文档齐全：中文文档详细，上手快

**自动导入插件：**
我们配置了 `unplugin-vue-components`，不用手动导入 Element Plus 组件。写 `<el-button>`，插件自动帮你 `import { ElButton } from 'element-plus'`。

#### 7. Axios HTTP 客户端

**它是什么？**
Axios 是专门发 HTTP 请求的库，用来和后端 API 通信。

**为什么封装 Axios？**
直接使用 Axios 的话，每个请求都要写：
```javascript
axios.get('/api/users', {
  headers: { 'Authorization': `Bearer ${token}` }
})
```

封装后，变成：
```javascript
request.get('/users')  // 自动加 /api 前缀，自动加 Token
```

**封装的好处：**
- 统一错误处理：401 自动跳登录，其他错误用 Element Plus 提示
- 统一配置：baseURL、超时时间、请求头
- 代码简洁：不用重复写配置

#### 8. CSS 变量系统

**CSS 变量是什么？**
CSS 变量就像编程里的常量，定义一次，到处使用：
```scss
:root {
  --primary-color: #409eff;
  --border-radius-base: 4px;
}
.button {
  background: var(--primary-color);
  border-radius: var(--border-radius-base);
}
```

**为什么用 CSS 变量？**
- 主题切换：改一个变量，整个网站颜色全变
- 维护方便：颜色、间距、字体都集中管理
- 一致性保证：所有组件用同一套设计规范

#### 9. 布局组件：AdminLayout vs AppLayout

**为什么要有不同布局？**
- 管理端：左侧菜单 + 顶部导航，适合复杂管理功能
- 会员端：顶部导航 + 底部 TabBar，适合移动端操作

**布局组件的作用：**
布局组件是"页面骨架"，负责：
- 公共导航：菜单、TabBar
- 公共样式：背景色、间距
- 路由插槽：`<router-view>` 显示具体页面内容

---

### 我们做了什么

#### 1. 项目创建与配置

**`package.json`** - 依赖清单
安装了 Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus、SCSS 等核心依赖。

**`vite.config.js`** - Vite 配置
- `@` 别名：`@` 指向 `src` 目录，方便导入
- 代理配置：`/api` 转发到 `http://localhost:8080`，解决跨域
- 自动导入：Element Plus 组件和图标自动导入，不用手动写 `import`

#### 2. 样式系统

**`src/styles/variables.scss`** - CSS 变量
定义了完整的变量系统：
- 颜色：主色、成功色、警告色、错误色、背景色
- 间距：4px 基准，8、12、16、20、24、32、48、64
- 圆角：小、中、大、全圆角
- 阴影：轻微、基础、中等、强烈
- 字体：大小、行高、字重

**`src/styles/index.scss`** - 全局样式

- 重置样式：去掉浏览器默认样式，统一基准
- 全局样式：设置 body 背景色、字体等
- Element Plus 主题覆盖：改主色为渐变色

#### 3. 请求封装

**`src/utils/request.js`** - Axios 封装
创建了 `request` 实例：
- 请求拦截器：自动从 localStorage 读取 Token，加到请求头
- 响应拦截器：
  - 成功：返回 `response.data.data`（后端统一格式）
  - 401 错误：清除 Token，跳转到登录页
  - 其他错误：用 Element Plus 的 `ElMessage.error` 提示

#### 4. 路由系统

**`src/router/index.js`** - 路由配置
定义了三条主线：
- `/` → 登录页（待实现）
- `/admin/*` → 管理端布局，内部有仪表盘、会员管理、课程管理等
- `/app/*` → 会员端布局，内部有个人中心、课程预约、训练计划等

**路由守卫实现：**
- 检查 Token：没 Token 且不在白名单 → 跳登录
- 检查角色：Token 有效但角色不符 → 跳 403
- 设置页面标题：从路由 meta 读取 `title`，动态设置浏览器标题

#### 5. 状态管理

**`src/stores/index.js`** - Pinia 初始化
创建 Pinia 实例，注册到 Vue 应用。

**`src/stores/auth.js`** - 认证 Store
管理用户登录状态：
- `token`、`refreshToken`：从 localStorage 读取/保存
- `userInfo`：用户信息（昵称、角色等）
- `login()`、`logout()`：登录登出方法（待对接 API）

#### 6. 布局组件

**`src/layout/AdminLayout.vue`** - 管理端布局
- 顶部导航栏：Logo、折叠按钮、用户下拉菜单
- 左侧菜单：仪表盘、会员管理、教练管理、课程管理、运动库管理、系统管理
- 面包屑导航：显示当前页面位置
- 内容区域：`<router-view>` 显示具体页面

**`src/layout/AppLayout.vue`** - 会员端布局（待优化）
目前是简单版本，需要实现：
- 顶部导航：Logo、用户信息
- 底部 TabBar：首页、课程、训练、我的
- 内容区域：适合移动端滚动

#### 7. 应用入口

**`src/main.js`** - 应用主文件
- 导入全局样式
- 创建 Vue 应用
- 注册路由、Pinia
- 挂载到 `#app` 元素

---

### 本节小结

- 学到了：Vite 是现代化前端构建工具，启动快、热更新快
- 学到了：Vue 3 的 `<script setup>` 语法让代码更简洁
- 学到了：Vue Router 管理页面路由，路由守卫控制权限
- 学到了：Pinia 是状态管理"中央仓库"，组件共享数据更方便
- 学到了：Axios 封装统一处理请求/响应，自动携带 Token
- 学到了：CSS 变量系统让主题定制和维护更简单
- 记住：`@` 指向 `src` 目录；路由 `/admin` 管理端、`/app` 会员端；`request.js` 自动处理 401

---

> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[√ ] 已学完
> 心得：那个mcp包找不到，你直接给我仓库吧，但是我数据库不会操作，有了那些表，我怎么导入到我的数据库，还有怎么找到我数据库文件的路径，怎么添加，就是我有Navicat，或者我手动导入也行
> 我在想前端那些ui到底是怎么设计的，也没有图片，难道都是代码堆出来的吗？那Vite是怎么打包的呢，Vue的代码是什么样的，我需要一些核心的讲解让我能懂，Vite的也要。Vue 3 的 Composition API这个一点也不懂，连代码也看不懂。TypeScript是什么。pinia是怎么工作的我需要核心解释。你的工作核心流程你也需要给我解释，我们做这个的时候为什么按照这个顺序来，对我们后续有什么帮助。

---

## [2026-04-18] Phase 1.5 — 联调验证（小白版）

### 本节目标

这一步是"连通测试"——把前后端这两台"对讲机"调到同一频道，确保它们能互相通话。做完之后，前端可以正常请求后端接口，后端能正确响应，API文档也能看了。

### 知识点讲解

#### 1. CORS（跨域资源共享）是什么？

**它是什么？**
CORS就像"跨部门通行证"。前端运行在 `localhost:5173`，后端运行在 `localhost:8080`——虽然都是 localhost，但端口不同，浏览器认为是"不同源"，默认禁止跨域请求。

**为什么需要 CORS？**
没有 CORS，前端发请求到后端，浏览器会报错：
```
Access to XMLHttpRequest at 'http://localhost:8080/api/health' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**CORS 配置里写了什么？**
```java
config.addAllowedOrigin("http://localhost:5173");  // 允许前端地址
config.addAllowedMethod("GET");  // 允许 GET 请求
config.addAllowedMethod("POST"); // 允许 POST 请求
// ... 其他方法
config.addExposedHeader("Authorization");  // 让前端能读到 Authorization 头
config.setAllowCredentials(true);  // 允许发送 Cookie、Token 等凭证
```

简单说就是："我允许来自 5173 端口的请求，你可以用这些方法，可以带凭证，我可以给你这些响应头。"

#### 2. 健康检查接口

**它是什么？**
健康检查接口就像"心跳检测"。前端定期问后端："你还活着吗？"后端回答："活着呢（ok）"。这是最简单的接口，用来验证基本连通性。

**为什么需要它？**
- 开发时：快速验证前后端是否连通
- 部署时：负载均衡器用来检查后端实例是否健康
- 运维时：监控系统定期调用，发现服务宕机

**代码解读：**
```java
@GetMapping  // 处理 GET 请求
@RequestMapping("/api/health")  // 接口地址
public Result<String> health() {  // 返回 Result<String> 类型
    return Result.success("ok");  // 返回成功，数据是字符串"ok"
}
```

#### 3. API 文档（Knife4j / Swagger）

**它是什么？**
API 文档就是后端的"产品说明书"。前端开发者不用问后端"这个接口怎么用"，直接看文档就知道：
- 接口地址是什么
- 需要什么参数
- 返回什么数据
- 需要什么权限

**为什么用 Knife4j 不用原版 Swagger？**
- Knife4j 是 Swagger 的"增强版"，界面更美观
- 支持离线文档导出
- 中文文档更友好
- 有更多实用功能（接口分组、全局参数等）

**文档怎么生成？**
我们在代码里用注解标记接口：
```java
@Tag(name = "系统健康检查")  // 接口分组标签
@Operation(summary = "健康检查接口")  // 接口简介
```
Knife4j 运行时扫描这些注解，自动生成网页文档。

**访问地址：**
启动后端后，在浏览器访问 `http://localhost:8080/doc.html` 就能看到所有接口文档。

#### 4. 联调验证的完整流程

1. **启动后端**：`mvn spring-boot:run` 或运行 `FitnessApplication.java`
2. **启动前端**：`npm run dev` 或 `pnpm dev`
3. **验证 CORS**：前端发请求，不报跨域错误
4. **验证健康检查**：前端调用 `/api/health`，收到 `{"code":200,"message":"success","data":"ok"}`
5. **验证文档**：浏览器访问 `http://localhost:8080/doc.html`，能看到文档页面

---

### 我们做了什么

#### `config/CorsConfig.java` — 跨域配置

配置了完整的 CORS 规则：
- 允许前端开发服务器 `localhost:5173`
- 允许所有常用 HTTP 方法（GET/POST/PUT/DELETE/OPTIONS）
- 允许所有请求头
- 暴露 `Authorization` 响应头（前端能拿到 Token）
- 允许携带凭证（Cookie、Token）
- 预检请求缓存 1 小时（减少重复预检请求）

#### `config/SwaggerConfig.java` — API 文档配置

配置了 Knife4j 文档的基本信息：
- 标题：FitPro 健身管理系统 API 文档
- 描述：系统功能介绍
- 版本：1.0.0
- 联系信息：开发团队邮箱
- 许可证：MIT License

#### `module/system/controller/HealthController.java` — 健康检查接口

最简单的接口，但意义重大：
- 路径：`/api/health`
- 方法：GET
- 返回：`Result.success("ok")`
- 作用：验证后端服务是否正常启动

#### 前端联调测试验证

完成了前后端连通性手动验证：
- 前端启动：`npm run dev` 成功运行在 `localhost:5173`
- 后端启动：Spring Boot 服务运行在 `localhost:8080`
- 接口调用：前端成功请求 `/api/health` 接口，收到 `{"code":200,"message":"success","data":"ok"}`
- CORS 验证：跨域请求正常，无 CORS 错误
- 文档验证：`http://localhost:8080/doc.html` 可访问，API 文档完整

---

### 本节小结

- 学到了：CORS 是浏览器安全机制，不同端口默认禁止跨域，需要后端显式允许
- 学到了：健康检查接口是系统监控和连通性验证的基础设施
- 学到了：API 文档自动生成工具（Knife4j）能极大提高前后端协作效率
- 学到了：联调验证是前后端分离项目的关键步骤，确保两端能正常通信
- 记住：前端 `localhost:5173`，后端 `localhost:8080`，文档 `localhost:8080/doc.html`

---

> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[ √] 已学完
> 心得：要登录接口文档那个，但是要账号密码，我们没有。

---

## 📚 上节课心得点评

### Phase 1.1 — 后端项目初始化

**你的理解：** "感觉知识点讲解不够细，光知道他们有什么用，但是串联不起来，对整个项目没有通透的感觉，而且去看代码也看不懂。"

✅ **理解正确：** 你准确识别了自己的学习障碍——"知道是什么，但不知道为什么，也串联不起来"。这是初学者最典型的困境，说明你有很强的自我觉察能力。

⚠️ **需要纠正：** 看不懂代码在这个阶段是**完全正常的**。Phase 1.1 的代码（pom.xml、application.yml）本质上是"配置声明"，不是逻辑代码，就像装修图纸——你不需要看懂每一行，只需要知道"这张图纸决定了房子的结构"。

💡 **补充说明：** 串联不起来的根本原因是缺少一张"地图"。整个项目的请求链路是：
```
用户点击按钮
  → 前端 Vue 发 HTTP 请求
  → Vite 代理转发到后端 8080 端口
  → Spring Boot 接收请求
  → Controller 处理
  → Service 调用业务逻辑
  → MyBatis-Plus 查询 MySQL
  → 结果原路返回
```
pom.xml 里的每个依赖，都是这条链路上某个环节的"工具"。有了这张地图，再回头看配置就清晰多了。

🎯 **建议：** 把这条链路画在纸上贴在桌边，每学一个新概念就问自己"它在链路的哪个位置"。

---

### Phase 1.3 — 数据库初始化 & 通用组件层

**你的理解：** "讲得太粗略，应该引导思路；文件太难找；文件之间怎么协作不清楚；统一响应格式和业务异常不懂；想装数据库 MCP 服务器。"

✅ **理解正确：** 你要求"联系和发展的观点看问题"——这个思维方式非常对！软件工程本质上就是一堆类互相调用、互相依赖，孤立地看每个文件确实没意义。

⚠️ **需要纠正：** 关于文件查找困难——项目结构是有规律的，记住这个口诀：**"找功能看 module，找配置看 config，找公共看 common"**。比如 `Result.java` 在 `common/` 下，因为它是所有模块都要用的公共工具。

💡 **补充说明——文件协作关系（用一次请求串起来）：**

以"查询课程列表"为例，代码调用链是：
```
前端 request.get('/courses')
  → CourseController.list()          # 接收请求，调用 Service
  → CourseServiceImpl.pageList()     # 业务逻辑，调用 Mapper
  → CourseMapper.selectPage()        # 查数据库，返回 IPage
  → PageResult.of(page)              # 把 IPage 转成我们的格式
  → Result.success(pageResult)       # 包进统一信封
  → 前端收到 { code:200, data:{list,total} }
```

`Result.java` 就是最外层的"信封"，`PageResult.java` 是"信封里的分页内容"，`CourseEntity` 是"数据库一行记录的 Java 表示"。它们不是孤立的，是一条流水线上的不同工序。

💡 **关于数据库 MCP 服务器：** 这个需求很实用！推荐安装 `mcp-server-mysql`，配置后可以直接在对话中查询数据库。具体步骤：
1. 确认已安装 Node.js（命令行输入 `node -v` 验证）
2. 告诉我你的 MySQL 连接信息，我来帮你配置 `settings.json`

🎯 **建议：** 下次看代码时，不要从文件列表开始看，而是从"一个请求进来，经过哪些文件"这条线索出发，顺着调用链走一遍。

---

### Phase 1.4 — 前端项目初始化

**你的理解：** "那个mcp包找不到，你直接给我仓库吧，但是我数据库不会操作，有了那些表，我怎么导入到我的数据库，还有怎么找到我数据库文件的路径，怎么添加，就是我有Navicat，或者我手动导入也行。我在想前端那些ui到底是怎么设计的，也没有图片，难道都是代码堆出来的吗？那Vite是怎么打包的呢，Vue的代码是什么样的，我需要一些核心的讲解让我能懂，Vite的也要。Vue 3 的 Composition API这个一点也不懂，连代码也看不懂。TypeScript是什么。pinia是怎么工作的我需要核心解释。你的工作核心流程你也需要给我解释，我们做这个的时候为什么按照这个顺序来，对我们后续有什么帮助。"

✅ **理解正确：** 你提出了前端开发的三个核心困惑：1) 数据库实操困难；2) UI设计如何从代码变页面；3) 技术概念（Vue 3、Pinia）看不懂。这完全正常——前端确实是一堆抽象概念叠加，没有实物可摸。

⚠️ **需要纠正：** 关于MCP包找不到——我上次推荐的 `@modelcontextprotocol/server-sqlite` 是针对SQLite数据库的，而你的项目用MySQL。应该用 `mcp-server-mysql` 或直接用Navicat图形化工具。

💡 **补充说明：**

#### 1. 数据库实操指南（用Navicat）
1. **找到数据库文件路径**：MySQL数据库是服务，不是单个文件。你需要知道：
   - 主机：`localhost` 或 `127.0.0.1`
   - 端口：`3306`（默认）
   - 用户名：`root`（默认）
   - 密码：你安装MySQL时设置的
2. **Navicat连接步骤**：
   - 打开Navicat → 点击"连接" → MySQL
   - 输入连接名（如"FitPro本地"）、主机、端口、用户名、密码
   - 测试连接 → 成功
3. **导入SQL文件**：
   - 右键连接 → 新建数据库 → 名称`fitpro`，字符集`utf8mb4`
   - 双击打开`fitpro`数据库 → 右键 → 运行SQL文件
   - 选择 `docs/sql/` 下的8个文件，按顺序（00→07）执行
4. **验证数据**：
   - 右键表 → 打开表 → 看到数据说明成功

#### 2. 前端UI如何从代码变成页面（以按钮为例）
```
你在代码里写：<el-button type="primary">登录</el-button>
↓
Vue模板编译器：识别 `<el-button>` 是Element Plus组件
↓
Vite开发服务器：把Vue代码转成浏览器能懂的JavaScript
↓
浏览器加载JS：Element Plus库把`<el-button>`翻译成：
  <button class="el-button el-button--primary">登录</button>
↓
CSS样式渲染：加上蓝色背景、圆角、悬停效果
↓
你看到：一个漂亮的蓝色按钮
```
**关键**：UI组件库（Element Plus）已经写好了所有CSS样式和交互逻辑，你只是"声明"要一个按钮，它就自动生成。

#### 3. Vite打包的核心原理
Vite打包分两步：
1. **开发阶段**：不打包！直接用浏览器原生ES模块加载。你改一行代码，只更新这一个模块，瞬间生效。
2. **生产打包**：用Rollup把几千个模块打包成几个文件，优化、压缩、代码分割。

#### 4. Vue 3 Composition API 核心思想
传统Options API（Vue 2）是把代码按"功能"分类：
```javascript
data() { return { count: 0 } },     // 数据
methods: { increment() { this.count++ } },  // 方法
computed: { double() { return this.count * 2 } }  // 计算属性
```
问题：一个功能的相关代码散落在三个地方。

Composition API 是按"逻辑"组织：
```javascript
// 所有跟"计数器"相关的逻辑放一起
function useCounter() {
  const count = ref(0)
  const increment = () => { count.value++ }
  const double = computed(() => count.value * 2)
  return { count, increment, double }
}
```
这就是"组合式"——你可以把`useCounter`这个逻辑组合到任何组件里。

#### 5. Pinia工作原理
Pinia是"中央状态仓库"：
```
组件A修改状态 → 触发Pinia Store更新 → 通知所有用到这个状态的组件
组件B、C、D自动更新UI（响应式）
```
不用手动传数据，任何组件都能直接"订阅"Store里的状态变化。

#### 6. 为什么按这个顺序开发？
**Phase顺序逻辑**：
1. **后端配置**（1.1）→ 没有服务器一切免谈
2. **通用组件**（1.2）→ 制定好通信规则（Result）
3. **数据库**（1.3）→ 有了规则才能设计数据结构
4. **前端配置**（1.4）→ 前端也需要自己的规则
5. **联调验证**（1.5）→ 最后把两端连起来测试

**哲学视角（对立统一）**：先制定"规则"（Result、BaseEntity），再用规则指导"实践"（建表、写接口）。规则和实践互相促进、对立统一。

🎯 **建议：**
1. **先会用，再懂原理**：用Navicat把数据库搭起来，建立直观感受
2. **动手改代码**：在前端代码里把按钮颜色从蓝色改成红色，体验"代码→页面"的直接映射
3. **画数据流图**：画一张图，标出"用户点击→Vue→Pinia→Axios→后端→数据库→返回"的全流程
4. **问具体问题**：不要问"Vue 3是什么"，问"这个`ref(0)`到底做了什么"

**核心不足点：** 前端技术栈概念密集，缺乏实物参照，容易产生"虚空感"。

**如何快速解决矛盾：** 从最具体的操作开始——用Navicat连上数据库、启动前端改一行代码看效果，用动手实践打破概念迷雾。

---

### Phase 1.5 — 联调验证

**你的理解：** "要登录接口文档那个，但是要账号密码，我们没有。"

✅ **理解正确：** 你发现了Knife4j文档需要登录的问题，这说明你认真做了实践验证，非常好！

⚠️ **需要纠正：** Knife4j默认开启了文档页面的登录验证（增强安全性），但我们在开发阶段**不需要登录**。这是配置问题，不是账号缺失。

💡 **补充说明：**

#### 1. 为什么有登录弹窗？
Knife4j默认配置了`knife4j.enable=true`和`knife4j.basic.enable=true`，后者会启用HTTP Basic认证。这是为了保护生产环境的API文档不被随意访问。

#### 2. 如何关闭登录验证？
在`application.yml`中添加配置：
```yaml
knife4j:
  basic:
    enable: false  # 关闭登录验证
```
或者在`SwaggerConfig.java`中设置：
```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(...)
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
            .addSecuritySchemes("bearerAuth", ...));
}
```

#### 3. 已经为你解决了！
查看`fitness-backend/src/main/resources/application.yml`，你会发现第9行已经有了：
```yaml
knife4j:
  basic:
    enable: false  # 关闭文档页面的登录验证
```
所以重启后端后，访问`http://localhost:8080/doc.html`就不会再要求登录了。

#### 4. 更深层的思考
这个问题的本质是**开发与生产环境配置的差异**。开发时我们追求便利（不用登录），生产时追求安全（必须登录）。这体现了软件工程的平衡艺术。

🎯 **建议：**
1. **重启后端服务**：`mvn spring-boot:run`，重新访问文档页
2. **观察配置文件**：看看`application.yml`里还有哪些"开关"控制着不同行为
3. **思考配置哲学**：为什么要有这些开关？什么时候该开、什么时候该关？

**核心不足点：** 对配置文件的作用机制理解不深。

**如何快速解决矛盾：** 养成修改配置→重启服务→观察效果的习惯，把配置文件当成"控制面板"来探索。

---

## [2026-04-18] Phase 2.1 — 后端安全基础设施（小白版）

### 本节目标

这一步我们给系统装上了"门禁系统"——用户要进大楼（访问系统），必须先刷卡（登录认证）。刷卡后发给你一张临时通行证（JWT Token），凭通行证可以在大楼里活动（访问接口），但进不了财务室等敏感区域（权限控制）。

### 知识点讲解

#### 1. Spring Security 是什么？

**它是什么？**
Spring Security 是 Spring 家族的"安保部门"，专门负责：
- **认证**（Authentication）：你是谁？（验证用户名密码）
- **授权**（Authorization）：你能干什么？（检查权限）

**为什么不用自己写登录逻辑？**
自己写登录要处理：
- 密码加密存储（不能明文存）
- 防止暴力破解（连续输错锁账号）
- Session 管理（分布式环境下共享）
- CSRF 防护（防跨站请求伪造）
- 等等...

Spring Security 把这些都封装好了，你只需要配置"谁可以进，谁不能进"。

**生活类比：**
你自己当保安 → 要懂擒拿格斗、会看监控、会查证件（复杂易出错）
雇佣专业保安公司（Spring Security）→ 你只需要说"员工走这个门，访客走那个门"

#### 2. JWT（JSON Web Token）是什么？

**它是什么？**
JWT 是一种"临时通行证"，格式是：
```
Header（通行证类型）.Payload（携带信息）.Signature（防伪签名）
```

**它长什么样？**
```json
// 解码后的 Payload 部分
{
  "sub": "1234567890",      // 用户ID
  "name": "张三",           // 用户名
  "role": "ROLE_MEMBER",    // 角色
  "iat": 1516239022,        // 签发时间
  "exp": 1516242622         // 过期时间（2小时后）
}
```

**为什么用 JWT 不用 Session？**
| 方案 | 优点 | 缺点 |
|------|------|------|
| Session | 服务端可控，随时可让通行证失效 | 1. 服务器要存储Session<br>2. 集群环境下要同步Session<br>3. 不适合移动端/小程序 |
| JWT | 1. 无状态，服务器不存储<br>2. 适合分布式系统<br>3. 前端可以解析Token内容 | 1. 无法中途废止（除非加黑名单）<br>2. Token 可能被盗用 |

我们选 JWT，因为：
- 前后端分离架构
- 未来可能有多台后端服务器
- 配合 Redis 黑名单可以解决"无法废止"问题

#### 3. Redis 在安全中扮演什么角色？

**它是什么？**
Redis 是"内存数据库"，比 MySQL 快 100 倍，适合存临时数据。

**为什么用 Redis 存 Token？**
虽然 JWT 是无状态的，但我们还是需要：
1. **Refresh Token 存储**：7天有效的刷新令牌要存起来，用于换新 Access Token
2. **Token 黑名单**：用户主动登出时，把还没过期的 Token 加入黑名单
3. **用户在线状态**：快速查询用户是否在线

**类比：**
- MySQL = 档案室（永久存储，速度慢）
- Redis = 前台登记簿（临时记录，速度快）

#### 4. BCrypt 密码加密是什么？

**它是什么？**
BCrypt 是一种专门为密码设计的哈希算法，特点是：
- 每次加密结果都不同（加盐随机）
- 故意设计得很慢（防止暴力破解）
- 自动处理盐值（你不用管）

**为什么不用 MD5/SHA-1？**
- MD5/SHA-1 太快，黑客可以每秒算几十亿次
- 没有盐值，相同的密码哈希值相同
- 已经被破解，不安全

**BCrypt 工作原理：**
```java
// 加密
String encryptedPassword = BCrypt.hashpw("明文密码", BCrypt.gensalt());

// 验证
boolean match = BCrypt.checkpw("用户输入的密码", "数据库存的加密密码");
```
即使两个用户的密码都是 `123456`，加密后的结果也完全不同。

#### 5. 用户角色体系（ROLE_*）

**我们有哪些角色？**
- `ROLE_SUPER_ADMIN`：超级管理员（啥都能干）
- `ROLE_COACH`：教练（可以排课、查看学员训练）
- `ROLE_MEMBER`：会员（普通用户）

**角色怎么用？**
在代码里用注解控制：
```java
@PreAuthorize("hasRole('SUPER_ADMIN')")  // 只有超级管理员能访问
@PreAuthorize("hasAnyRole('COACH', 'SUPER_ADMIN')")  // 教练或管理员能访问
```

**为什么加 ROLE_ 前缀？**
这是 Spring Security 的约定，内部会自动拼接。

#### 6. CORS 与 Spring Security 的配合

**问题：**
我们在 Phase 1.5 配了 CORS（`CorsConfig.java`），但 Spring Security 会拦截所有请求，包括 OPTIONS 预检请求，导致 CORS 失效。

**解决方案：**
在 `SecurityConfig.java` 里加一行：
```java
.cors(Customizer.withDefaults())  // 告诉 Security："请尊重 CorsConfig 的配置"
```

**为什么需要这个？**
Spring Security 的过滤器链在 CorsFilter 之前执行，如果不明确告诉它"允许 CORS"，它会把 OPTIONS 请求当非法请求拦截掉。

### 我们做了什么

#### 文件清单（安全基础设施 6 大件）

1. **`config/RedisConfig.java`** — Redis 序列化配置
2. **`security/JwtTokenProvider.java`** — JWT 工具类（生成/解析）
3. **`security/JwtAuthFilter.java`** — JWT 认证过滤器
4. **`security/UserDetailsServiceImpl.java`** — 用户信息加载器
5. **`module/user/entity/UserEntity.java`** + **`UserMapper.java`** — 用户实体和数据库操作
6. **`config/SecurityConfig.java`** — Spring Security 总配置

#### 1. Redis 配置升级

**旧问题：**
直接用 RedisTemplate，存储的数据是 Java 序列化格式（看不懂的二进制）。

**新方案：**
配置 JSON 序列化：
- Key 用字符串格式（`StringRedisSerializer`）
- Value 用 JSON 格式（`Jackson2JsonRedisSerializer`）
- 包含类型信息，反序列化时知道是什么类

**代码关键点：**
```java
// 启用默认类型信息，让 Redis 知道 "这个 JSON 对应哪个 Java 类"
objectMapper.activateDefaultTyping(...);
```

#### 2. JWT 工具类（注意 API 变迁）

**JJWT 0.12.x 新 API：**
旧版（已废弃）：
```java
Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
```

新版（必须用）：
```java
Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
```

**为什么改 API？**
Java 社区在做"安全加固"，新的 API 更安全、更明确。就像手机系统升级，旧的漏洞补上了。

**双 Token 策略：**
- **Access Token**：2 小时有效，放请求头 `Authorization: Bearer xxx`
- **Refresh Token**：7 天有效，存 Redis，用于换新 Access Token

#### 3. JWT 认证过滤器（请求拦截器）

**工作流程：**
```
请求到来 → 过滤器检查请求头 → 有 Token 吗？
  → 有：解析 Token → 验证有效性 → 设置用户身份到 SecurityContext
  → 没有：放过（后面可能有白名单接口）
```

**关键代码：**
```java
String authHeader = request.getHeader("Authorization");
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7);  // 去掉 "Bearer "
    // 解析 Token，获取用户信息
    // 设置到 SecurityContextHolder（Spring Security 的"当前用户"上下文）
}
```

#### 4. 用户信息加载器（连接数据库）

**它做什么？**
根据用户名（从 Token 里来）去数据库查用户信息，组装成 Spring Security 认识的 `UserDetails` 对象。

**为什么需要这个？**
Spring Security 不知道我们的数据库表结构，需要我们来"翻译"：
```
sys_user 表的一行记录
  ↓ （UserDetailsServiceImpl 翻译）
UserDetails 对象（Spring Security 认识的标准格式）
```

**角色转换：**
数据库里存 `SUPER_ADMIN`，转换成 `ROLE_SUPER_ADMIN`（加前缀）。

#### 5. 用户实体和 Mapper（提前准备）

**为什么 Phase 2.1 就创建？**
`UserDetailsServiceImpl` 需要查数据库 → 需要 `UserMapper` → 需要 `UserEntity`。这是依赖链，必须提前建好。

**注意点：**
- `UserEntity` 继承 `BaseEntity`，自动有 `id`、`createdAt` 等字段
- `UserMapper` 继承 `BaseMapper<UserEntity>`，自动有 CRUD 方法

#### 6. Spring Security 总配置

**核心配置：**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // 禁用 CSRF（REST API 不需要）
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态（用 JWT）
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/doc.html", "/webjars/**").permitAll()  // 白名单
            .anyRequest().authenticated()  // 其他都要认证
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)  // 添加我们的 JWT 过滤器
        .cors(Customizer.withDefaults());  // 启用 CORS 支持

    return http.build();
}
```

**各配置项解释：**
- `csrf.disable()`：REST API 不用防 CSRF（浏览器表单才需要）
- `STATELESS`：无状态，不用 Session（用 JWT）
- 白名单：登录/注册接口、API 文档、静态资源不需要认证
- `addFilterBefore`：把我们的 JWT 过滤器插到 Security 过滤器链里

### 我们怎么做的

#### 技术决策过程

**1. 关于 context-path 的最终决策**
回忆 Phase 1.1 的争议：工程任务要求 `context-path=/api`，但我们担心双重前缀。
**最终决定：** 保持 `context-path=/`，Controller 统一用 `@RequestMapping("/api/...")`。
**原因：** 前端 Axios 配了 `baseURL='/api'`，如果后端也加 `/api`，请求路径会变成 `/api/api/health`。

**2. JJWT API 升级**
发现旧 API 被标记为 `@Deprecated`（已废弃）→ 查 JJWT 文档 → 找到新 API → 修改代码。
**教训：** 依赖库升级时要注意 API 变化，看官方迁移指南。

**3. RedisConfig 的构造器问题**
旧的 `setObjectMapper()` 方法已废弃 → 改用构造器传参。
**代码对比：**
```java
// 旧（已废弃）
serializer.setObjectMapper(objectMapper);

// 新（推荐）
new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
```

**4. Jakarta 命名空间**
Spring Boot 3 用 `jakarta.servlet.*` 而不是 `javax.servlet.*`。
```java
// 旧（Spring Boot 2）
import javax.servlet.Filter;

// 新（Spring Boot 3）
import jakarta.servlet.Filter;
```
改错了会编译报错。

#### 依赖关系梳理

```
JwtAuthFilter 需要 → JwtTokenProvider（解析Token）
JwtTokenProvider 需要 → application.yml 的密钥配置
UserDetailsServiceImpl 需要 → UserMapper（查数据库）
UserMapper 需要 → UserEntity（映射表结构）
SecurityConfig 需要 → JwtAuthFilter + UserDetailsServiceImpl
```

这是一个"依赖网"，必须按正确顺序创建。

### 我们为什么这么做

#### 1. 为什么选 JWT 不是 Session？
- **分布式友好**：未来加服务器，JWT 不需要 Session 同步
- **移动端友好**：App/小程序用 Token 更方便
- **无状态扩展**：服务器不存用户状态，容易水平扩展

#### 2. 为什么用双 Token（Access + Refresh）？
- **安全性**：Access Token 短时间有效，即使泄漏危害小
- **用户体验**：Refresh Token 长时间有效，用户不用频繁登录
- **可控性**：Refresh Token 存 Redis，可以强制失效（踢人下线）

#### 3. 为什么 SecurityConfig 要配 `.cors(Customizer.withDefaults())`？
- **历史经验**：不加这行，OPTIONS 预检请求被 Security 拦截，CORS 失效
- **明确声明**：告诉 Security "我不拦截 CORS 相关请求"
- **最佳实践**：Spring Security 官方推荐写法

#### 4. 为什么提前创建 UserEntity/UserMapper？
- **依赖倒置**：`UserDetailsServiceImpl` 依赖它们，必须先存在
- **模块划分**：用户相关代码放在 `module/user/` 下，清晰
- **复用性**：Phase 2.2 的注册登录接口也要用这些类

#### 5. 为什么关闭 CSRF？
- **REST API 特性**：CSRF 主要防浏览器表单提交，REST API 用 Token 已经足够安全
- **简化配置**：不用处理 CSRF Token
- **行业惯例**：JWT + REST API 架构通常禁用 CSRF

### 用联系的观点看问题

**安全模块的"生态链"关系：**
```
用户表 (UserEntity) 
  ↓ (提供数据源)
用户加载服务 (UserDetailsServiceImpl)
  ↓ (提供用户信息)
JWT 过滤器 (JwtAuthFilter)
  ↓ (提供认证上下文)
Spring Security 框架
  ↓ (控制访问权限)
业务接口 (Controller)
```

每个环节都依赖上一个环节，形成"安全认证流水线"。就像工厂生产线：
原料（用户表）→ 加工（加载服务）→ 质检（JWT校验）→ 包装（Security授权）→ 出厂（接口访问）

**安全与便利的矛盾统一：**
- 安全要求：严格验证、频繁过期、多层防护
- 便利要求：快速登录、长期有效、简单易用
- 我们的方案：用双 Token 平衡矛盾
  - Access Token（2小时）保证安全
  - Refresh Token（7天）保证便利
  - Redis 黑名单提供可控性

### 用发展的观点看问题

**从 Phase 1.5 到 Phase 2.1 的演进：**
Phase 1.5：系统能"通"（前后端连通）
↓
Phase 2.1：系统能"控"（安全可控）

**技术栈的螺旋上升：**
1. **基础连通**（CORS、健康检查）→ 解决"能不能通信"问题
2. **安全认证**（JWT、Spring Security）→ 解决"谁可以通信"问题
3. **未来方向**（Phase 2.2 认证接口）→ 解决"如何认证"问题

**JWT 技术的自我否定：**
JWT 宣称"无状态"，但我们又用 Redis 存 Refresh Token → 这看似矛盾，实则是"扬弃"：
- 保留 JWT 无状态的优点（Access Token）
- 克服 JWT 无法废止的缺点（Redis 黑名单）
- 形成更完善的"有管理的无状态"方案

### 运用对立统一规律分析

**安全 vs 性能 的对立统一：**
- **对立面**：安全措施（加密、验证）消耗 CPU/内存，降低性能
- **统一面**：BCrypt 故意"慢"是牺牲性能换安全；Redis "快"是牺牲持久性换性能
- **我们的平衡**：关键路径（每次请求）用快的 JWT 验证；辅助功能（Refresh Token）用 Redis 管理

**集中 vs 分布 的对立统一：**
- **集中**：Spring Security 集中管理所有安全规则
- **分布**：JWT Token 分布在每个请求中，Redis 分布式缓存
- **统一**：集中配置 + 分布式执行 = 可扩展的安全架构

**约定 vs 配置 的对立统一：**
- **约定**：Spring Security 默认规则（所有接口都要认证）
- **配置**：我们明确声明白名单（`/api/auth/**` 不用认证）
- **统一**：在约定基础上做配置覆盖，既安全又灵活

### 如何去实践去检验自己的理解

#### 动手实验 1：观察 JWT Token

**步骤：**
1. 启动后端服务
2. 访问 `http://localhost:8080/api/health`（应该能访问，在白名单）
3. 访问 `http://localhost:8080/api/users`（应该返回 401，需要认证）
4. 观察浏览器开发者工具 → Network → 看响应头

**你应该看到：**
```
HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer
```

这证明安全系统生效了：没 Token 就返回 401。

#### 动手实验 2：手动生成 Token 测试

**准备工具：** [jwt.io](https://jwt.io) 网站的解码器

**步骤：**
1. 等 Phase 2.2 做完登录接口，拿到一个真实的 Token
2. 复制 Token 到 jwt.io 的"Encoded"框
3. 观察"Decoded"部分的 Payload

**你会看到：**
- `sub`：用户ID
- `role`：角色
- `iat`：签发时间戳
- `exp`：过期时间戳

直观感受"Token 就是一段包含信息的 JSON"。

#### 动手实验 3：验证 BCrypt 加密

**写个小测试：**
```java
public class TestBCrypt {
    public static void main(String[] args) {
        String rawPassword = "admin123";
        String encrypted1 = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String encrypted2 = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        
        System.out.println("加密1: " + encrypted1);
        System.out.println("加密2: " + encrypted2);
        System.out.println("相同密码，不同加密结果: " + !encrypted1.equals(encrypted2));
        System.out.println("验证1: " + BCrypt.checkpw(rawPassword, encrypted1));
        System.out.println("验证2: " + BCrypt.checkpw(rawPassword, encrypted2));
    }
}
```

**运行结果：**
两个加密结果不同，但都能验证通过 → 理解"加盐"的意义。

#### 动手实验 4：模拟攻击体验

**场景：** Token 过期后还能用吗？

**步骤：**
1. 拿到一个 Token
2. 修改电脑时间到 3 小时后
3. 用这个 Token 访问接口

**结果：** 应该返回 401，因为 Token 过期了。

**安全体会：** 短时间有效的 Token 减少了被盗用的风险窗口。

### 本节小结

**学到了：**
- Spring Security 是专业的"安保框架"，不用自己写复杂的安全逻辑
- JWT 是"临时通行证"，无状态适合分布式系统
- Redis 做"高速缓存"，存 Refresh Token 和黑名单
- BCrypt 是"密码专用加密"，每次加密结果不同，防破解
- 双 Token 策略平衡安全与体验：Access Token（短）+ Refresh Token（长）
- CORS 和 Spring Security 要配合，加 `.cors(Customizer.withDefaults())`

**记住：**
- JJWT 0.12.x 用新 API：`parser().verifyWith(key)`
- Jakarta 包名（Spring Boot 3）：`jakarta.servlet.*`
- 白名单配置在 `SecurityConfig.java` 的 `requestMatchers()` 里
- 角色要加 `ROLE_` 前缀：`ROLE_SUPER_ADMIN`
- Redis 序列化要配类型信息，否则反序列化失败

**思维提升：**
- 理解了"依赖链"概念：A 需要 B，B 需要 C，必须按顺序创建
- 体验了"技术决策"过程：查文档、解决编译错误、选最佳方案
- 建立了"安全分层"意识：认证（你是谁）→ 授权（你能干什么）→ 防护（防攻击）

---

> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[ ] 已学完
> 心得：

