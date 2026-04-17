# CLAUDE.md — FitPro 代码生成规范

## 项目概述

FitPro 健身管理系统，Spring Boot 3.2 + Vue 3 前后端分离架构。
- 后端: `fitness-backend/` — Java 17, Spring Boot 3.2, MyBatis-Plus 3.5, MySQL 8.0, Redis 7.x
- 前端: `fitness-frontend/` — Vue 3.4, Vite 5, Element Plus 2, Pinia 2, Axios

## 通用规范

- 所有代码文件使用 UTF-8 编码
- 缩进: Java 用 4 空格, Vue/JS/CSS 用 2 空格
- 文件末尾保留一个空行
- 不要生成无用的注释或 TODO，只在逻辑复杂处加注释
- 变量/方法命名要自解释，不需要额外注释
- **所有回答必须使用中文**

---

## Java 后端规范

### 包结构

```
com.fitness
├── config/          # 配置类
├── common/          # 通用组件 (Result, BaseEntity, 异常)
├── security/        # 安全模块 (JWT, Filter)
├── module/          # 业务模块
│   └── {模块名}/
│       ├── entity/  # 实体类
│       ├── dto/     # 请求 DTO
│       ├── vo/      # 响应 VO
│       ├── mapper/  # MyBatis Mapper
│       ├── service/ # Service 接口 + impl/
│       └── controller/ # Controller
└── util/            # 工具类
```

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | UpperCamelCase | `CourseScheduleService` |
| 方法名 | lowerCamelCase | `findByUserId()` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 数据库字段 | lower_snake_case | `created_at` |
| URL 路径 | kebab-case | `/api/course-schedules` |
| 包名 | 全小写 | `com.fitness.module.course` |

### Entity 规范

```java
@Data
@TableName("表名")
public class XxxEntity extends BaseEntity {
    // 字段使用 MyBatis-Plus 注解
    // @TableId(type = IdType.ASSIGN_ID) 在 BaseEntity 中已定义
    // 枚举字段使用 String 类型，不用 Java enum
    // 金额字段使用 BigDecimal
    // 时间字段使用 LocalDateTime / LocalDate
}
```

BaseEntity 包含: id, createdAt, updatedAt, deleted (逻辑删除)

### DTO / VO 规范

- DTO: 接收前端请求参数，加 @Valid 校验注解
- VO: 返回给前端的数据，脱敏后的视图对象
- 命名: `XxxCreateDTO`, `XxxUpdateDTO`, `XxxQueryDTO`, `XxxVO`
- 不要直接把 Entity 返回给前端

```java
@Data
public class CourseCreateDTO {
    @NotBlank(message = "课程名称不能为空")
    private String name;

    @NotNull(message = "课程时长不能为空")
    @Min(value = 15, message = "课程时长最少15分钟")
    private Integer durationMinutes;
}
```

### Controller 规范

```java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "分页查询课程")
    public Result<PageResult<CourseVO>> list(CourseQueryDTO query) {
        return Result.success(courseService.pageList(query));
    }

    @PostMapping
    @Operation(summary = "创建课程")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> create(@RequestBody @Valid CourseCreateDTO dto) {
        courseService.create(dto);
        return Result.success();
    }
}
```

规则:
- 统一使用 `Result<T>` 包装返回值
- 使用 `@RequiredArgsConstructor` 构造器注入，不用 `@Autowired`
- 使用 Knife4j 注解 `@Tag` `@Operation` 标注接口
- 权限控制使用 `@PreAuthorize`
- 路径使用复数名词: `/api/courses`, `/api/users`

### Service 规范

```java
public interface CourseService extends IService<CourseEntity> {
    PageResult<CourseVO> pageList(CourseQueryDTO query);
    void create(CourseCreateDTO dto);
}

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, CourseEntity>
        implements CourseService {

    // 使用 LambdaQueryWrapper 构建查询
    // 事务方法加 @Transactional
    // 业务异常抛 BusinessException
}
```

### Mapper 规范

```java
@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {
    // 简单查询用 MyBatis-Plus 内置方法
    // 复杂联表查询写在 XML 中
}
```

XML 文件放在 `resources/mapper/` 下，命名: `XxxMapper.xml`

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

错误码:
- 200: 成功
- 400: 参数错误
- 401: 未认证
- 403: 无权限
- 404: 资源不存在
- 500: 服务器错误
- 1001-1999: 业务错误码

### 异常处理

- 业务异常统一抛 `BusinessException(code, message)`
- GlobalExceptionHandler 统一捕获并返回 Result
- 不要在 Controller 中 try-catch

---

## Vue 前端规范

### 目录结构

```
src/
├── api/           # 按模块拆分 API 文件
│   ├── auth.js
│   ├── user.js
│   └── course.js
├── components/    # 公共组件 (PascalCase 命名)
├── composables/   # 组合式函数 (useXxx.js)
├── layout/        # 布局组件
├── router/        # 路由配置
├── stores/        # Pinia Store (useXxxStore.js)
├── styles/        # 全局样式
├── utils/         # 工具函数
└── views/         # 页面组件 (按模块分文件夹)
```

### 组件规范

- 使用 `<script setup>` 语法
- 组件文件名 PascalCase: `CourseCard.vue`
- 页面文件名 PascalCase + View 后缀: `CourseListView.vue`

```vue
<template>
  <div class="course-list">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCourseStore } from '@/stores/course'

// props / emits
const props = defineProps({
  courseId: { type: Number, required: true }
})
const emit = defineEmits(['update'])

// 响应式数据
const loading = ref(false)

// 生命周期
onMounted(() => {
  fetchData()
})

// 方法
const fetchData = async () => {
  loading.value = true
  try {
    // ...
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.course-list {
  /* 使用 scoped 样式 */
}
</style>
```

### API 封装规范

```javascript
// src/api/course.js
import request from '@/utils/request'

export const courseApi = {
  list: (params) => request.get('/courses', { params }),
  getById: (id) => request.get(`/courses/${id}`),
  create: (data) => request.post('/courses', data),
  update: (id, data) => request.put(`/courses/${id}`, data),
  delete: (id) => request.delete(`/courses/${id}`)
}
```

### Axios 封装要求

```javascript
// src/utils/request.js
// baseURL: '/api'
// 请求拦截器: 自动注入 Authorization: Bearer {token}
// 响应拦截器:
//   - 200: 返回 response.data.data
//   - 401: 清除 Token, 跳转登录页
//   - 其他: ElMessage.error(message)
```

### Store 规范

```javascript
// src/stores/auth.js
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  // 使用 setup 语法
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const login = async (form) => { /* ... */ }
  const logout = () => { /* ... */ }

  return { token, userInfo, login, logout }
})
```

### 路由规范

- 管理端路由前缀: `/admin`
- 会员端路由前缀: `/app`
- 路由 meta 中声明 `title`, `roles`, `requiresAuth`
- 使用路由懒加载: `() => import('@/views/xxx/XxxView.vue')`

---

## 数据库规范

- 表名: lower_snake_case，业务表不加 `t_` 前缀，系统表加 `sys_` 前缀
- 主键: `id BIGINT` 使用雪花算法
- 每张表必须有: `created_at`, `updated_at`
- 需要软删除的表加: `deleted TINYINT DEFAULT 0`
- 索引命名: `idx_表名_字段名`
- 外键只在 ER 图中体现关系，数据库中不建外键约束
- 字符集: utf8mb4

---

## 开发工作流规范

### 每次开发任务完成后必须执行

1. **同步 `docs/task_tree.md`**
   - 将已完成的子任务 `[ ]` 改为 `[x]`
   - 整个小节完成时更新父节点状态

2. **同步 `docs/engineering_tasks.md`**
   - 将对应行状态列从 `[ ]` 改为 `[x]`（或 `[~]` 进行中 / `[!]` 阻塞）
   - 若有新发现的子任务，追加到对应 Phase 末尾

3. **写入开发日志 `docs/dev_log.md`**
   - 格式见下方"开发日志规范"
   - 每个任务节点（1.1 / 1.2 / 2.1 …）各写一条记录

4. **写入学习日志 `docs/learning_log.md`**
   - 以**资深老师**视角，面向**完全不懂编程的小白**讲解
   - 内容与开发日志同步，但要把每个技术点讲透彻：是什么、为什么、怎么用
   - 多用生活类比，不出现不解释的缩写，不跳步骤
   - 格式见下方"学习日志规范"

> ⚠️ 以上四步是硬性要求，代码写完不同步文档视为任务未完成。

### 开发日志规范

文件路径: `docs/dev_log.md`

每条记录格式：

```markdown
## [日期] Phase X.X — 任务标题

**完成内容**
- 简短列举创建/修改的文件及核心功能

**关键决策**
- 记录有取舍的技术选型或实现方式（没有则省略）

**遗留问题**
- 记录待确认的问题或下一步注意事项（没有则省略）
```

### 学习日志规范

文件路径: `docs/learning_log.md`

- 角色：资深全栈工程师老师，耐心、细致、善用类比
- 受众：对编程一无所知的初学者
- 要求：
  - 每个文件/配置/概念都要解释"它是什么、为什么需要它、不加会怎样"
  - 专业术语首次出现必须括号解释，如：`Maven（项目构建工具，帮你自动下载和管理依赖库）`
  - 多用生活场景类比，例如把 pom.xml 比作"购物清单"
  - 代码片段必须逐行注释说明
  - 结尾给出"本节学到了什么"小结

每条记录格式：

```markdown
## [日期] Phase X.X — 任务标题（小白版）

### 本节目标
用一两句话说清楚这一步要做什么、做完之后系统变成什么状态。

### 知识点讲解
（逐一展开每个涉及的技术概念）

#### 概念名称
...

### 我们做了什么
（对照开发日志，逐文件用白话解释）

#### 文件名
...

### 本节小结
- 学到了 ...
- 记住 ...

---
> 📝 **学习心得区**（由学员填写）
> 学完后在此处打勾并写下心得：[ ] 已学完
> 心得：
```

### 学员心得反馈机制

> ⚠️ 这是硬性要求，每轮开发开始前必须执行。

**每轮新开发任务开始时，必须先执行以下步骤：**

1. **读取 `docs/learning_log.md`**，找到所有带有"学习心得区"且已打勾（`[x]`）的条目
2. **逐条审阅学员心得**，针对每条心得：
   - 肯定理解正确的部分
   - 纠正理解偏差或错误
   - 补充遗漏的重要知识点
   - 给出进一步学习建议
3. **将反馈写入对话回复**，格式如下：

```markdown
## 📚 上节课心得点评

### Phase X.X — [任务标题]
**你的理解：** [引用学员原话]

✅ 理解正确：...
⚠️ 需要纠正：...
💡 补充说明：...
🎯 建议：...
```

4. 点评完成后，再开始本轮开发任务。

**如果没有新的已打勾心得，跳过此步骤，直接开始开发。**

---

## Git 提交规范

```
<type>(<scope>): <subject>

type: feat | fix | refactor | style | docs | test | chore
scope: 模块名 (auth, user, course, workout, exercise, membership, checkin, system)
subject: 简短描述，不超过 50 字符
```

示例:
- `feat(auth): 实现 JWT 登录认证`
- `fix(course): 修复预约人数超限问题`
- `refactor(common): 统一响应格式重构`
