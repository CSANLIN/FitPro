# FitPro 学习日志

> 角色：资深全栈工程师老师，面向完全不懂编程的小白讲解
> 格式：每个 Phase 一条记录，逐概念展开，结尾附学员心得区

## 目录

- [Phase 1.1 — 后端项目初始化（小白版）](#phase-11)
- [Phase 1.2 — 通用组件层（小白版）](#phase-12)
- [Phase 1.3 — 数据库初始化（小白版）](#phase-13)
- [Phase 1.4 — 前端项目初始化（小白版）](#phase-14)
- [Phase 1.5 — 联调验证（小白版）](#phase-15)
- [Phase 2.1 — 后端安全基础设施（小白版）](#phase-21)
- [Phase 2.2 — 认证接口（小白版）](#phase-22)

---

<a id="phase-11"></a>
## Phase 1.1 — 后端项目初始化（小白版）

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

<a id="phase-12"></a>
## Phase 1.2 — 通用组件层（小白版）

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

<a id="phase-13"></a>
## Phase 1.3 — 数据库初始化（小白版）

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

<a id="phase-14"></a>
## Phase 1.4 — 前端项目初始化（小白版）

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

<a id="phase-15"></a>
## Phase 1.5 — 联调验证（小白版）

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

<a id="phase-21"></a>
## Phase 2.1 — 后端安全基础设施（小白版）

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

---

<a id="phase-22"></a>
## Phase 2.2 — 认证接口（小白版）

### 本节目标

这一步我们实现了系统的"前台接待处"——用户可以在这里注册账号、登录、刷新令牌、登出。做完之后，用户就能通过这些接口完成整个认证流程：注册 → 登录获取 Token → 用 Token 访问其他接口 → Token 过期时刷新 → 登出清除 Token。

---

### 知识点讲解

#### 1. DTO（Data Transfer Object）是什么？

**它是什么？**
DTO 就是"快递单据"。用户填写表单（用户名、密码等）发给后端，后端不能直接用这个表单数据，要先验证、转换，这个转换的"中间格式"就是 DTO。

**为什么要有 DTO？**
- **数据验证**：DTO 上贴注解（`@NotBlank`、`@Size` 等），Spring 自动验证
- **数据隐藏**：用户表有 100 个字段，但注册只需要 5 个，DTO 只暴露需要的字段
- **版本兼容**：API 升级时，DTO 可以新增字段，不影响旧客户端

**代码例子：**

```java
@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
}
```

这些注解就像"快递单据上的检查项"：
- 用户名不能空 ✓
- 用户名长度 3-20 ✓
- 密码不能空 ✓
- 密码至少 6 位 ✓

如果不符合，Spring 自动返回 400 错误，不用我们手动检查。

#### 2. VO（Value Object）是什么？

**它是什么？**
VO 就是"快递收据"。后端处理完请求后，要返回数据给前端，这个返回的"格式"就是 VO。

**DTO vs VO 的区别：**
| 方向 | 用途 | 例子 |
|------|------|------|
| DTO | 前端 → 后端 | 用户填表单（用户名、密码） |
| VO | 后端 → 前端 | 后端返回用户信息（ID、昵称、角色） |

**为什么要有 VO？**
- **脱敏**：用户表有密码字段，VO 里不包含密码
- **聚合**：多个表的数据合并成一个 VO 返回
- **格式化**：时间戳转成可读的日期字符串

**代码例子：**
```java
@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String role;
    // 注意：没有 password 字段！
}
```

#### 3. Service 层是什么？

**它是什么？**
Service 层是"业务逻辑处理中心"。Controller 接收请求，Service 处理业务，Mapper 操作数据库。

**为什么要分层？**
```
Controller（接收请求）
    ↓
Service（处理业务逻辑）
    ↓
Mapper（操作数据库）
```

如果不分层，所有逻辑都在 Controller 里，会变成"大泥球"，难以维护。

**Service 的职责：**
- 验证业务规则（用户名是否存在、密码是否正确）
- 调用 Mapper 操作数据库
- 调用其他 Service 完成复杂业务
- 处理事务（多个数据库操作要么全成功，要么全失败）

#### 4. 事务（Transaction）是什么？

**它是什么？**
事务就像"银行转账"。你从 A 账户转 100 块到 B 账户，要么两个账户都更新成功，要么都失败。不能出现"A 账户扣了 100，但 B 账户没收到"的情况。

**为什么需要事务？**
在注册流程中：
1. 插入用户到数据库
2. 生成 Token
3. 存 Refresh Token 到 Redis

如果第 2 步失败了，第 1 步的用户插入要回滚（撤销），否则数据库里有个"孤儿"用户。

**代码里怎么用？**
```java
@Transactional(rollbackFor = Exception.class)  // 任何异常都回滚
public TokenVO register(RegisterDTO dto) {
    // 1. 插入用户
    userMapper.insert(user);
    // 2. 生成 Token
    String token = jwtTokenProvider.generateAccessToken(...);
    // 如果这里出错，第 1 步会自动回滚
}
```

#### 5. 密码验证流程

**注册时：**
```
用户输入明文密码 "admin123"
    ↓
BCrypt.hashpw() 加密
    ↓
存到数据库（加密后的密码）
```

**登录时：**
```
用户输入明文密码 "admin123"
    ↓
从数据库查出加密后的密码
    ↓
BCrypt.checkpw(明文, 加密后) 比对
    ↓
返回 true/false
```

**为什么不能反向解密？**
BCrypt 是"单向加密"，就像把鸡蛋打碎了，无法还原成完整的鸡蛋。这样即使数据库被黑客盗取，黑客也看不到明文密码。

#### 6. Token 刷新流程

**问题：** Access Token 只有 2 小时有效，过期后怎么办？

**方案：** 用 Refresh Token 换新 Access Token

**流程：**
```
用户登录
    ↓
后端返回：Access Token（2h）+ Refresh Token（7d）
    ↓
用户用 Access Token 访问接口
    ↓
2 小时后，Access Token 过期
    ↓
前端用 Refresh Token 调用 /api/auth/refresh
    ↓
后端验证 Refresh Token 有效
    ↓
返回新的 Access Token（又有 2 小时）
    ↓
用户继续用新 Token 访问接口
```

**为什么要这么复杂？**
- 如果只有一个 Token（7 天有效），被盗用风险大
- 如果只有短 Token（2 小时），用户要频繁登录，体验差
- 双 Token 方案：短 Token 保安全，长 Token 保体验

#### 7. SecurityContext 是什么？

**它是什么？**
SecurityContext 是 Spring Security 的"当前用户上下文"，就像"线程本地存储"。

**为什么需要它？**
在 `getCurrentUserInfo()` 方法里，我们需要知道"当前登录的是谁"。但这个信息怎么传进来？

**答案：** JWT 过滤器已经把用户信息放进 SecurityContext 了：
```java
// 在 JwtAuthFilter 里
SecurityContextHolder.getContext().setAuthentication(authentication);

// 在 AuthService 里
Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
Long userId = Long.parseLong(principal.toString());
```

就像"线程的私人笔记本"，每个请求线程都有自己的笔记本，记着"当前用户是谁"。

---

### 我们做了什么

#### 文件清单（认证接口 7 大件）

1. **`module/auth/dto/RegisterDTO.java`** — 注册请求参数
2. **`module/auth/dto/LoginDTO.java`** — 登录请求参数
3. **`module/auth/vo/TokenVO.java`** — Token 响应
4. **`module/auth/vo/UserInfoVO.java`** — 用户信息响应
5. **`module/auth/service/AuthService.java`** — 认证服务接口
6. **`module/auth/service/impl/AuthServiceImpl.java`** — 认证服务实现
7. **`module/auth/controller/AuthController.java`** — 认证控制器

#### 1. 请求参数 DTO

**RegisterDTO：**
- 用户名、密码、确认密码、昵称、手机号、角色
- 所有字段都有校验注解（`@NotBlank`、`@Size`、`@Pattern`）
- 前端发来的数据会自动验证，不符合直接返回 400

**LoginDTO：**
- 用户名、密码
- 简洁设计，只需要最少信息

#### 2. 响应对象 VO

**TokenVO：**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 7200,           // 2小时，单位秒
  "refreshExpiresIn": 604800   // 7天，单位秒
}
```

**UserInfoVO：**
- 用户的所有信息（除了密码）
- 用于 `/api/auth/me` 接口返回当前用户信息

#### 3. 认证服务实现（5 个核心方法）

**`register(RegisterDTO dto)`**
```
1. 检查用户名是否已存在 → 存在则抛异常
2. 检查手机号是否已存在 → 存在则抛异常
3. 检查两次密码是否一致 → 不一致则抛异常
4. BCrypt 加密密码
5. 构建 UserEntity，保存到数据库
6. 生成 Access Token + Refresh Token
7. 存 Refresh Token 到 Redis（7天过期）
8. 返回 TokenVO
```

**`login(String username, String password)`**
```
1. 根据用户名查询用户 → 不存在则抛异常
2. BCrypt 验证密码 → 不匹配则抛异常
3. 检查用户状态（0=正常，1=禁用）→ 禁用则抛异常
4. 生成 Access Token + Refresh Token
5. 存 Refresh Token 到 Redis
6. 返回 TokenVO
```

**`refresh(String refreshToken)`**
```
1. 验证 Refresh Token 格式和类型
2. 从 Token 中提取用户 ID
3. 从 Redis 查出存储的 Refresh Token
4. 比对：传入的 Token 是否与 Redis 中的一致 → 不一致则抛异常
5. 查询用户信息，检查用户状态
6. 生成新的 Access Token（Refresh Token 保持不变）
7. 返回 TokenVO
```

**`logout(String refreshToken)`**
```
1. 验证 Refresh Token 格式和类型
2. 从 Token 中提取用户 ID
3. 从 Redis 中删除对应的 Refresh Token
4. 记录日志
```

**`getCurrentUserInfo()`**
```
1. 从 SecurityContext 获取当前用户 ID
2. 根据 ID 查询用户信息
3. 检查用户状态
4. 转换为 UserInfoVO（脱敏）
5. 返回
```

#### 4. 认证控制器（5 个接口）

| 接口 | 方法 | 路径 | 参数 | 返回 |
|------|------|------|------|------|
| 注册 | POST | `/api/auth/register` | RegisterDTO | TokenVO |
| 登录 | POST | `/api/auth/login` | LoginDTO | TokenVO |
| 刷新 | POST | `/api/auth/refresh` | refreshToken | TokenVO |
| 登出 | POST | `/api/auth/logout` | refreshToken | 无 |
| 获取用户 | GET | `/api/auth/me` | 无 | UserInfoVO |

---

### 本节小结

**学到了：**
- DTO 是"请求快递单据"，自动验证数据
- VO 是"响应快递收据"，脱敏返回数据
- Service 层处理业务逻辑，分层让代码更清晰
- 事务保证数据一致性，要么全成功要么全失败
- 双 Token 方案平衡安全与体验
- SecurityContext 是"当前用户上下文"，让我们知道谁在访问

**记住：**
- 注册时检查用户名/手机号唯一性
- 登录时验证密码用 BCrypt.checkpw()
- Refresh Token 要存 Redis，登出时删除
- 每个操作都要检查用户状态（是否被禁用）
- 错误码要有规律（1001-1009），便于前端处理
- 返回给前端的 VO 不能包含敏感信息（密码）

**思维提升：**
- 理解了"分层架构"：Controller → Service → Mapper
- 体验了"数据流转"：DTO（入）→ Service 处理 → VO（出）
- 建立了"安全意识"：密码加密、Token 验证、用户状态检查

---

## [2026-04-18] Phase 2.1 — 后端安全基础设施（小白版）

### 本节目标
为我们的健身管理系统安装"保安系统"。想象一下，我们要给健身房安装门禁、摄像头和身份识别系统，这样只有合法的会员才能进入，并且系统能记住每个人的权限。做完这一步，后端就有了完整的身份验证基础。

### 知识点讲解

#### Spring Security（保安系统）
这是 Spring 框架的"保安大队"，专门负责应用程序的安全。它就像一个智能门禁系统，能：
- 检查每个进入的请求（就像检查每个进入健身房的人）
- 验证身份（查看会员卡）
- 控制权限（普通会员不能进入VIP区域）
- 防止恶意攻击（比如有人想暴力破解密码）

#### JWT（令牌）
JWT（JSON Web Token）就像一个"数字会员卡"，里面编码了用户信息和有效期。它有三个部分：
- 头部：说明这是什么类型的令牌，用什么算法加密
- 载荷：包含用户ID、角色、过期时间等信息
- 签名：防止令牌被篡改的"防伪标识"

#### Redis（临时储物柜）
Redis 是一个内存数据库，速度非常快。在这里我们用它来存储"刷新令牌"，就像给会员一个临时储物柜钥匙，钥匙在一定时间内有效，用完后可以换新的。

### 我们做了什么

#### config/RedisConfig.java — Redis配置
这个文件告诉程序："嘿，我们用Redis的时候，数据要这样保存：键用字符串格式，值用JSON格式。" JSON就像一种标准的数据包装纸，方便前后端都能看懂。

#### security/JwtTokenProvider.java — JWT工具
这个文件是"会员卡制作机"：
- 能生成两种会员卡：访问令牌（2小时有效）和刷新令牌（7天有效）
- 能检查会员卡是否有效、是否过期
- 能读取会员卡里的信息（用户ID、角色等）

#### security/JwtAuthFilter.java — 安检过滤器
这个过滤器就像门口的保安：
- 检查每个请求有没有带"Authorization: Bearer {token}"这个头
- 如果有令牌，就验证它是否有效
- 验证通过后，把用户信息存入系统上下文，这样后续流程都知道是谁在操作

#### security/UserDetailsServiceImpl.java — 用户信息加载器
这个文件告诉系统："当需要用户信息时，去sys_user数据库表里找。"它会根据用户名找到用户，然后告诉系统这个用户有什么权限。

#### config/SecurityConfig.java — 保安系统配置
这是整个保安系统的"总控制台"：
- 设置哪些路径不需要检查（比如登录、注册、文档页面）
- 告诉系统使用我们自定义的JWT过滤器
- 配置密码加密方式（BCrypt，一种很安全的加密算法）
- 禁用一些不必要的安全检查（比如CSRF，因为我们用JWT就够了）

### 我们为什么这么做

1. **安全第一**：健身房不能让随便谁都能进，我们的系统也一样。
2. **无状态设计**：用JWT就不需要在服务器保存会话信息，服务器压力小，容易扩展。
3. **速度快**：Redis内存存储比查数据库快得多，适合存临时令牌。
4. **标准化**：JWT是行业标准，前后端都支持，文档丰富。

### 本节小结
- 学到了 **Spring Security** 是Java后端的标准保安系统
- 学到了 **JWT** 是现代Web应用常用的无状态认证方案
- 学到了 **Redis** 可以作为高速缓存存储刷新令牌
- 记住 **过滤器（Filter）** 是拦截请求进行预处理的关键组件
- 记住 **BCrypt** 是目前推荐的密码加密算法

---

## [2026-04-18] Phase 2.2 — 认证接口（小白版）

### 本节目标
给我们的"保安系统"添加具体的服务窗口：注册处、登录处、会员卡刷新处、登出处、信息查询处。做完这一步，用户就能注册账号、登录系统、刷新令牌、安全退出和查看自己的信息了。

### 知识点讲解

#### DTO（数据传输对象）
DTO就像"申请表格"。用户要注册时，需要填写注册表；要登录时，需要填写登录表。DTO定义了表格里有哪些字段，每个字段有什么要求（比如用户名最少3个字，密码要6-20位）。

#### VO（视图对象）
VO就像"回执单"。系统处理完用户的请求后，会给用户一个回执，比如登录成功回执里包含会员卡（Token）、用户信息等。VO只包含用户需要知道的信息，不包含敏感数据（比如密码）。

#### 业务异常
业务异常就像"窗口工作人员的解释"。当用户填表有误（比如用户名已存在）或操作不合法时，系统会抛出一个业务异常，里面有错误码和友好的提示信息，而不是直接崩溃。

### 我们做了什么

#### module/auth/dto/RegisterDTO.java — 注册申请表
定义了注册时需要填写的字段：
- 用户名、密码、确认密码、昵称、手机号、角色
- 每个字段都有验证规则（用注解标注，如@NotBlank表示不能为空）

#### module/auth/dto/LoginDTO.java — 登录申请表
定义了登录时需要填写的字段：用户名和密码。

#### module/auth/vo/TokenVO.java — 令牌回执单
包含登录成功后返回的信息：
- accessToken：访问令牌（2小时有效）
- refreshToken：刷新令牌（7天有效）
- tokenType：令牌类型（固定为"Bearer"）
- expiresIn：过期时间（秒）
- refreshExpiresIn：刷新令牌过期时间（秒）

#### module/auth/vo/UserInfoVO.java — 用户信息回执单
包含用户的基本信息（ID、用户名、昵称、头像、角色等），但不包含密码。

#### module/auth/service/AuthService.java — 认证服务接口
定义了5个服务窗口的功能：
1. 注册窗口：register()
2. 登录窗口：login() 
3. 刷新窗口：refresh()
4. 登出窗口：logout()
5. 信息查询窗口：getCurrentUserInfo()

#### module/auth/service/impl/AuthServiceImpl.java — 认证服务实现
具体实现了每个窗口的业务逻辑：

**注册流程**：
1. 检查用户名和手机号是否已被使用（查数据库）
2. 用BCrypt算法加密密码（把明文密码变成乱码保存）
3. 保存用户信息到数据库
4. 生成双令牌（访问令牌+刷新令牌）
5. 把刷新令牌存入Redis（设置7天过期）
6. 返回TokenVO给用户

**登录流程**：
1. 根据用户名查找用户
2. 用BCrypt.checkpw()验证密码是否正确
3. 检查用户状态是否正常（没被禁用）
4. 生成双令牌
5. 刷新令牌存Redis
6. 返回TokenVO

**刷新令牌流程**：
1. 验证刷新令牌是否有效且未过期
2. 检查Redis里是否有这个刷新令牌（防止被盗用）
3. 生成新的访问令牌（刷新令牌不变）
4. 返回新的TokenVO

**登出流程**：
1. 验证刷新令牌
2. 从Redis删除对应的刷新令牌（让旧令牌失效）

**获取用户信息**：
1. 从SecurityContext（安全上下文）获取当前用户ID
2. 查询用户详细信息
3. 转换为UserInfoVO返回

#### module/auth/controller/AuthController.java — 认证控制器
提供了5个HTTP接口：
- `POST /api/auth/register`：注册
- `POST /api/auth/login`：登录
- `POST /api/auth/refresh`：刷新令牌
- `POST /api/auth/logout`：登出
- `GET /api/auth/me`：获取当前用户信息

### 本节小结
- 学到了 **DTO/VO设计模式**：入参用DTO，出参用VO，职责分离
- 学到了 **完整认证流程**：注册→登录→刷新→登出的完整逻辑
- 学到了 **业务异常处理**：用统一的方式处理业务错误
- 记住 **BCrypt加密**：密码一定要加密存储，不能明文
- 记住 **刷新令牌机制**：Access Token短效，Refresh Token长效，平衡安全与体验

---

## [2026-04-18] Phase 2.3.1 — 前端认证API（小白版）

### 本节目标
为前端创建"通信兵"，让前端能和后端的认证接口对话。想象一下，我们要在前端建立几个专用的电话线路，分别对应后端的5个服务窗口。做完这一步，前端JavaScript代码就能方便地调用后端认证接口了。

### 知识点讲解

#### API封装
API封装就像"建立电话线路"。我们不希望每次打电话都要重新拨号、自我介绍，而是希望有固定的线路，拿起电话就能说"我要登录"。封装就是把复杂的HTTP请求细节隐藏起来，提供简单的函数调用。

#### Promise（承诺）
Promise是JavaScript处理异步操作（比如网络请求）的一种方式。它就像餐厅的取餐号牌：你下单后拿到号牌，不用在柜台前干等，可以去做别的事；餐好了号牌会通知你。

#### Axios拦截器
拦截器就像"电话总机"。每个请求和响应都要经过拦截器，我们可以在拦截器里做一些统一处理，比如：
- 请求拦截器：自动给每个请求加上Token
- 响应拦截器：统一处理错误，比如401未授权就跳转到登录页

### 我们做了什么

#### src/api/auth.js — 认证API模块
创建了5个"电话线路"：

1. **注册线路**：`register(data)`
   - 拨打`POST /api/auth/register`电话
   - 把用户填的注册表单data传过去

2. **登录线路**：`login(data)`
   - 拨打`POST /api/auth/login`电话
   - 把用户名密码传过去

3. **刷新线路**：`refresh(refreshToken)`
   - 拨打`POST /api/auth/refresh`电话
   - 把刷新令牌传过去

4. **登出线路**：`logout(refreshToken)`
   - 拨打`POST /api/auth/logout`电话
   - 把刷新令牌传过去

5. **获取用户信息线路**：`getMe()`
   - 拨打`GET /api/auth/me`电话
   - 不需要参数，因为Token会自动通过拦截器加上

### 本节小结
- 学到了 **Pinia状态管理**：集中管理全局状态的最佳实践
- 学到了 **Composition API**：用函数方式组织逻辑，更灵活清晰
- 学到了 **认证状态流**：登录→保存Token→获取用户信息→各组件共享状态
- 记住 **localStorage持久化**：关键数据要持久化，避免页面刷新丢失
- 记住 **状态一致性**：登录、登出、刷新都要保持本地状态与服务器同步

---

## [2026-04-18] Phase 2.3.3 — 登录页面（小白版）

### 本节目标
创建用户进入系统的"大门"——登录页面。想象一下，我们要设计一个美观、易用、安全的健身房前台登录界面，用户在这里输入用户名密码，系统验证通过后，根据用户角色（管理员、教练、会员）引导到不同的区域。做完这一步，用户就有可视化的界面进行登录操作了。

### 知识点讲解

#### Vue单文件组件
Vue单文件组件（.vue文件）把HTML模板、JavaScript逻辑和CSS样式放在一个文件里，就像一个小型的独立模块。一个登录页面就是一个组件，包含表单、样式和登录逻辑。

#### Element Plus组件库
Element Plus是基于Vue 3的UI组件库，提供了丰富的现成组件：输入框、按钮、表单、弹窗等。就像装修健身房时，我们不用自己造桌椅，而是采购现成的、设计统一的家具。

#### 表单验证
表单验证确保用户输入的数据符合要求，比如密码不能太短，用户名不能包含特殊字符。这就像健身房前台会检查会员卡是否有效，不会让过期的卡进入。

#### 响应式设计
响应式设计让页面在不同尺寸的设备上都能良好显示。就像健身房的指示牌，无论是手机查看还是大屏幕显示，信息都清晰可读。

### 我们做了什么

#### src/views/auth/LoginView.vue — 登录页面组件

**视觉设计**：
- 背景使用健身主题的高清图片，叠加渐变蒙层，营造专业健身氛围
- 登录表单采用卡片式设计，有毛玻璃效果（backdrop-filter），符合现代UI趋势
- 完全响应式：在手机、平板、电脑上都能良好显示

**表单元素**：
1. **用户名输入框**：
   - 左侧有用户图标
   - 提示文字："请输入用户名"
   - 验证规则：3-20个字符，只能包含字母、数字、下划线

2. **密码输入框**：
   - 左侧有锁图标
   - 支持显示/隐藏密码切换（小眼睛图标）
   - 验证规则：6-20个字符

3. **记住我复选框**：可选功能（暂未实现具体逻辑）

4. **忘记密码链接**：跳转到找回密码页面（暂未实现）

5. **登录按钮**：
   - 主色调按钮
   - 加载状态：登录时显示旋转图标，防止重复提交

6. **注册链接**：引导新用户去注册页面

**表单验证**：
- 使用Element Plus的Form组件验证规则
- 提交前检查所有字段是否符合要求
- 错误信息实时显示在对应输入框下方

**登录逻辑**：
1. 用户点击登录按钮
2. 调用`authStore.login()`方法
3. 显示加载状态，防止重复点击
4. 登录成功后：
   - 管理员/教练 → 跳转到`/admin/dashboard`
   - 会员 → 跳转到`/app/profile`
5. 登录失败时显示错误提示

### 本节小结
- 学到了 **Vue单文件组件结构**：template + script + style三位一体
- 学到了 **Element Plus表单组件**：快速构建美观、功能完善的表单
- 学到了 **响应式设计原则**：用CSS变量和媒体查询适配不同设备
- 记住 **表单验证重要性**：前端验证提升用户体验，后端验证保证数据安全
- 记住 **用户引导设计**：清晰的提示、实时的反馈、合理的跳转





---

<a id="phase-24"></a>
## Phase 2.4 — 认证流程验证（小白版）

### 本节目标
这节课我们要像侦探一样，亲自走一遍用户从注册到登录再到使用功能的完整旅程，确保每一个环节都畅通无阻。你会学到如何手动测试一个系统，并理解为什么这些测试对软件质量至关重要。

### 知识点讲解

#### 什么是“端到端流程测试”？
想象你要网购一件衣服：**挑选商品 → 加入购物车 → 填写收货地址 → 支付 → 等待收货**。这是一个完整的“端到端”流程。我们的认证流程测试也一样：**注册账号 → 登录 → 用登录后的身份去访问个人信息页面**。我们要确保这个链条上的每一个环节都正常工作，任何一个环节断了，用户都无法完成目标。

#### 为什么需要“角色权限验证”？
不同身份的人能去的地方不一样。比如：
- **超级管理员**（SUPER_ADMIN）：像健身房老板，能进入“员工后台”查看所有会员数据、排课表。
- **普通会员**（MEMBER）：像健身房的顾客，只能进入“会员APP”查看自己的课程、预约、身体数据。
如果会员不小心（或故意）点进了管理后台的链接，系统必须立刻拦住他，并显示“抱歉，您没有权限访问此页面”（403错误）。这就是角色权限验证——确保每个人只能去自己该去的地方。

#### 什么是“Token自动刷新”？
登录后系统会给你两张“门票”：
1. **短期门票**（Access Token）：有效期2小时，进每个房间都要出示。
2. **长期门票**（Refresh Token）：有效期7天，不能直接进门，但可以在短期门票过期后，去售票处换一张新的短期门票。

“Token自动刷新”就是：当你拿着过期的短期门票试图进门时，门口的保安（前端Axios拦截器）不会立刻赶你走，而是悄悄用你的长期门票去换一张新的短期门票，然后用新门票重新尝试进门。整个过程你几乎感觉不到，体验更流畅。

### 我们做了什么

对照开发日志，我们手动验证了三个核心流程：

#### 1. 注册→登录→访问受保护接口流程验证
- **做了什么**：我们像一个新用户一样，在注册页面填写信息创建账号，然后用这个账号登录，登录后立刻尝试访问“我的个人信息”页面（`/api/auth/me`）。
- **为什么重要**：这验证了**账号创建 → 身份认证 → 资源访问**这条主干道是否畅通。如果这里出问题，整个系统的核心功能就瘫痪了。

#### 2. 不同角色访问权限验证
- **做了什么**：我们分别用“超级管理员”账号和“普通会员”账号登录，然后尝试访问对方专属的页面。
  - 超级管理员访问 `/admin/dashboard`（管理后台） → 成功。
  - 普通会员访问 `/app/profile`（会员中心） → 成功。
  - 普通会员尝试访问 `/admin/dashboard` → 被拦截，显示403错误页面。
- **为什么重要**：这确保了系统的**安全性**和**数据隔离**。会员不能越权查看其他会员的数据或管理后台的敏感信息。

#### 3. Token过期→刷新→重试流程验证
- **做了什么**：我们等待Access Token（短期门票）过期（或手动修改使其过期），然后尝试访问一个需要认证的接口。观察前端是否会自动用Refresh Token（长期门票）去换取新的Access Token，并重新发送之前的请求。
- **为什么重要**：这提升了**用户体验**。用户不需要因为短时间不操作就重新登录，系统在后台默默完成了令牌更新。

### 本节小结
- 学到了 **端到端测试**：像用户一样完整走一遍流程，发现阻塞点。
- 学到了 **角色权限模型**：系统如何根据身份决定“你能去哪里、看什么”。
- 学到了 **Token自动刷新机制**：如何用Refresh Token无缝更新Access Token，提升用户体验。
- 记住 **手动测试的价值**：在自动化测试之前，手动验证是确保核心功能可用的最快方法。
- 记住 **安全边界**：权限验证不是“有最好”，而是“必须有”，否则系统门户大开。

**思考题**：如果有一天健身房推出“教练”角色，教练应该能访问哪些页面？不能访问哪些页面？试着设计一下教练的权限边界。

## [2026-04-22] Phase 3.1 — 用户模块（小白版）

### 本节目标
我们要给健身管理系统添加用户管理功能。想象一下，你去健身房办卡，前台需要记录你的基本信息（姓名、电话、生日等），管理员需要查看所有会员列表，还能帮你修改资料或者重置密码。这一阶段就是要实现这些功能，让系统能够：
1. 让用户自己修改个人资料（昵称、头像、手机号等）
2. 让用户修改自己的密码
3. 让管理员查看所有用户列表，并能搜索、筛选
4. 让管理员可以启用或禁用用户账号

### 知识点讲解

#### DTO（数据传输对象）
想象你要寄快递，需要填一个“快递单”。DTO就像这个快递单，它规定了前端（寄件人）需要填写哪些信息，后端（快递公司）需要接收哪些信息。比如修改资料时，前端需要告诉后端“我要把昵称改成XXX，手机号改成YYY”，这些信息就放在DTO里。

#### VO（视图对象）
快递送到后，收件人看到的包裹内容。VO就是后端返回给前端的数据，它可能只包含用户能看到的信息（比如不包含密码这种敏感信息）。

#### Service（服务层）
就像健身房的前台工作人员。Controller（接待员）收到你的请求后，会把具体工作交给Service（前台）去处理。Service负责真正的业务逻辑：查数据库、验证数据、更新信息等。

#### Controller（控制器）
健身房的接待台。你（前端）走到接待台说“我要修改资料”，接待员（Controller）接收你的请求，然后叫相关工作人员（Service）去处理，最后把结果告诉你。

#### @PreAuthorize（权限注解）
就像健身房的不同区域：普通会员只能进器械区，VIP会员还能进私教区，管理员能进所有区域。`@PreAuthorize`就是在每个门口贴的“准入要求”，比如用户管理接口门口贴着“仅限管理员进入”。

#### 分页查询
想象一本厚厚的会员名册有1000页，你不可能一次性看完。分页就是把这本名册分成每页10条记录，你可以翻到第5页看第41-50条记录。这样既节省内存，又方便浏览。

#### 表单验证
就像健身房办卡时，工作人员会检查你填的信息：手机号是不是11位，邮箱格式对不对，生日是不是合理日期。前后端都会做验证，确保数据正确。

### 我们做了什么

#### UserQueryDTO.java（用户查询参数单）
定义了搜索用户时需要哪些条件：关键词（可以搜用户名、昵称或手机号）、角色（管理员/教练/会员）、状态（正常/禁用），还有分页信息（第几页、每页多少条）。

#### UserUpdateDTO.java（用户更新参数单）
定义了修改个人资料时需要提供哪些信息：昵称、头像、邮箱、手机号、性别、生日。每个字段都有验证规则，比如手机号必须是11位数字。

#### PasswordUpdateDTO.java（密码修改参数单）
定义了修改密码时需要的信息：原密码、新密码、确认密码。后端会检查原密码是否正确，以及两次输入的新密码是否一致。

#### UserVO.java（用户信息展示单）
定义了返回给前端的用户信息包含哪些内容：ID、用户名、昵称、头像、邮箱、手机号、性别、生日、角色、状态、注册时间、更新时间。注意：这里没有密码字段，保护用户安全。

#### UserService.java 和 UserServiceImpl.java（用户服务）
实现了5个核心功能：
1. **分页查询用户列表**：根据搜索条件查找用户，支持模糊搜索
2. **获取用户详情**：根据ID获取单个用户的完整信息
3. **更新用户资料**：修改昵称、头像等信息，还会检查手机号和邮箱是否被其他人用过
4. **修改用户密码**：验证原密码后更新为新密码
5. **切换用户状态**：在“正常”和“禁用”之间切换账号状态

#### UserController.java（用户控制器）
创建了5个接口：
1. `GET /api/users` - 管理员查看用户列表（需要管理员权限）
2. `GET /api/users/{id}` - 查看用户详情（管理员可看任何人，用户只能看自己）
3. `PUT /api/users/profile` - 修改自己的资料（任何登录用户）
4. `PUT /api/users/password` - 修改自己的密码（任何登录用户）
5. `PUT /api/users/{id}/status` - 切换用户状态（仅管理员）

#### user.js（前端用户API）
把后端的5个接口封装成前端可以调用的函数，比如`userApi.list()`调用用户列表接口，`userApi.updateProfile()`调用修改资料接口。

#### ProfileView.vue（个人中心页面）
创建了一个用户修改自己资料的页面，包含两个标签页：
1. **基本信息**：可以修改昵称、头像、手机号、邮箱、性别、生日
2. **修改密码**：输入原密码和新密码来修改密码
页面有表单验证，比如手机号格式不对会提示，确保输入的数据正确。

#### MemberListView.vue（用户管理页面）
创建了一个管理员查看和管理用户的页面，包含：
- **搜索栏**：可以按关键词、角色、状态搜索用户
- **用户表格**：显示用户列表，每行有用户的基本信息
- **状态开关**：可以一键启用或禁用用户账号（有确认提示）
- **详情按钮**：点击可以查看用户的完整信息
- **分页组件**：可以翻页查看更多用户

### 本节小结
- **学到了**用户管理系统的完整架构：前端页面 → 前端API → 后端Controller → 后端Service → 数据库
- **学到了**权限控制的重要性：不同角色只能访问自己权限范围内的功能
- **学到了**数据验证的必要性：前后端都要验证数据，确保系统安全稳定
- **记住了**DTO是前端传给后端的数据格式，VO是后端返回给前端的数据格式
- **记住了**Service层处理业务逻辑，Controller层负责接收请求和返回响应
- **实践了**如何实现一个完整的CRUD（增删改查）功能模块

现在，健身管理系统有了完整的用户管理功能：用户可以维护自己的资料，管理员可以管理所有用户。这为后续的课程预约、训练计划等功能打下了基础，因为所有这些功能都需要知道“谁”在使用系统。

