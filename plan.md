# Communication 在线内容发布平台 - 实现计划

## 项目概述

构建一个在线内容发布平台，支持文字/图片/视频发布、用户订阅、评论互动、内容搜索和作者管理后台。

## 技术栈

| 层级 | 技术选型 |
|------|---------|
| 前端 | Vue 3 + TypeScript + Vite |
| UI | Element Plus（暖色调定制） |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| 后端 | Spring Boot 3.2 |
| 认证 | Spring Security + JWT |
| ORM | Spring Data JPA |
| 数据库 | MySQL 8.0 |
| 迁移 | Flyway |
| 测试 | JUnit 5 / Vitest / Playwright |
| 包管理 | pnpm |

## 设计风格

- 主色：`#FF6B35`（橙红）
- 辅色：`#F7C59F`（杏色）
- 背景：`#FFF8F0`（米白）
- 风格：动态、功能性、暖色调社交应用风格

---

## 数据库设计

```
┌─────────────┐       ┌─────────────────┐       ┌──────────────┐
│    users    │       │     contents    │       │   comments   │
├─────────────┤       ├─────────────────┤       ├──────────────┤
│ id (PK)     │──1──┐ │ id (PK)         │──1──┐ │ id (PK)      │
│ username    │     │ │ author_id (FK)  │     │ │ content_id   │
│ password    │     └─│ title           │     └─│ (FK)         │
│ email       │       │ body            │       │ user_id (FK) │
│ avatar_url  │       │ media_url       │       │ body         │
│ bio         │       │ media_type      │       │ created_at   │
│ created_at  │       │ view_count      │       └──────────────┘
│ updated_at  │       │ status          │
└─────────────┘       │ created_at      │
       │              │ updated_at      │
       │              └─────────────────┘
       │
       │  ┌──────────────────┐     ┌──────────────────┐
       └──│  subscriptions   │     │  content_tags    │
          ├──────────────────┤     ├──────────────────┤
          │ id (PK)          │     │ content_id (FK)  │
          │ subscriber_id    │     │ tag              │
          │ author_id        │     └──────────────────┘
          │ created_at       │
          └──────────────────┘
```

---

## 项目结构

### 后端 `communication-backend/`

```
src/main/java/com/communication/
├── CommunicationApplication.java
├── config/           # SecurityConfig, CorsConfig
├── controller/       # Auth, User, Content, Comment, Subscription, Search
├── service/          # 业务逻辑层
├── repository/       # JPA Repository
├── entity/           # 数据库实体
├── dto/              # 请求/响应 DTO
├── exception/        # 全局异常处理
└── util/             # JWT, 文件上传工具
```

### 前端 `communication-frontend/`

```
src/
├── views/            # 页面组件
│   ├── HomeView.vue
│   ├── auth/         # Login, Register
│   ├── content/      # Detail, Create, Edit
│   ├── user/         # Profile, Dashboard
│   └── search/       # SearchView
├── components/       # 复用组件
├── stores/           # Pinia stores
├── api/              # API 请求封装
├── router/           # 路由配置
└── styles/           # 全局样式
```

---

## 迭代计划

### 迭代一：项目骨架与用户认证 ✅ 完成

**目标**：搭建开发基础设施，实现用户注册/登录/JWT认证

- [x] 初始化 Spring Boot 项目，配置 Maven 依赖
- [x] 配置 MySQL 连接 + Flyway 迁移脚本 (V1__init_users.sql)
- [x] 实现 users 表实体、Repository、Service
- [x] 实现注册接口（BCrypt 加密）
- [x] 实现登录接口（返回 JWT）
- [x] 配置 Spring Security + JWT 过滤器
- [x] 配置 CORS 跨域
- [x] 全局异常处理器
- [x] 初始化 Vue 3 + TypeScript + Vite 项目
- [x] 配置 pnpm
- [x] 集成 Element Plus（暖色调主题定制）
- [x] 实现 AppHeader 布局组件
- [x] 实现注册页面
- [x] 实现登录页面
- [x] 配置 Pinia auth store
- [x] 配置 axios 实例 + 拦截器
- [x] 配置 Vue Router + 路由守卫
- [x] 后端：UserService 单元测试
- [x] 前端：auth store 单元测试

**验收**：用户可完成注册并登录，前端保存 Token 并能访问受保护路由

---

### 迭代二：内容发布核心功能 ✅ 完成

**目标**：实现内容的 CRUD，支持文字/图片/视频三种类型

- [x] 创建 contents 表迁移脚本 (V2)
- [x] 实现 Content 实体 + Repository + Service
- [x] 实现内容 CRUD API（含权限验证）
- [x] 实现本地媒体文件上传接口
- [x] 实现内容列表分页查询
- [x] 实现首页 ContentFeed 组件
- [x] 实现 ContentCard 组件
- [x] 实现内容创建页面（富文本 + 媒体上传）
- [x] 实现 MediaUploader 组件
- [x] 实现内容详情页面
- [x] 实现内容编辑/删除功能
- [x] 配置 Pinia content store
- [x] 后端：ContentService 单元测试
- [x] 后端：文件上传集成测试

**验收**：登录用户可发布三种类型内容，所有用户可浏览首页内容流

---

### 迭代三：社交互动功能 ✅ 完成

**目标**：实现评论系统与订阅关系

- [x] 创建 comments、subscriptions 表迁移脚本 (V3)
- [x] 实现评论 CRUD API
- [x] 实现订阅/取消订阅 API
- [x] 实现订阅流接口（聚合已订阅作者的内容）
- [x] 实现用户主页 API
- [x] 在内容详情页集成 CommentList + CommentInput
- [x] 实现评论发布/删除交互
- [x] 实现用户主页 ProfileView
- [x] 实现订阅/取消订阅按钮
- [x] 实现首页 Feed 切换（全部/订阅）
- [x] 后端：SubscriptionService 单元测试
- [x] 后端：CommentService 单元测试

**验收**：用户可评论内容、订阅作者，订阅流正确展示已订阅作者的内容

---

### 迭代四：搜索模块与标签系统 ✅ 完成

**目标**：实现内容搜索和标签分类

- [x] 创建 content_tags 表迁移脚本 (V4)
- [x] 在内容接口中支持标签字段
- [x] 实现搜索接口（标题、正文、标签）
- [x] 实现用户搜索接口
- [x] 在 AppHeader 添加搜索框（防抖）
- [x] 实现搜索结果页（内容/用户 Tab 切换）
- [x] 在内容创建页添加标签输入
- [x] ContentCard 展示标签
- [x] 后端：搜索 Service 单元测试

**验收**：用户可通过关键词/标签搜索到内容和作者

---

### 迭代五：作者管理后台 ✅ 完成

**目标**：实现作者数据看板与账户管理

- [x] 实现 Dashboard 聚合接口（订阅数、浏览量、趋势）
- [x] 实现个人资料编辑接口
- [x] 实现头像上传接口
- [x] 实现 DashboardView（数据概览卡片）
- [x] 实现内容管理表格
- [x] 实现个人资料编辑页
- [x] 后端：Dashboard 统计接口单元测试

**验收**：作者可在后台查看订阅者数量、内容数据，可编辑个人信息

---

### 迭代六：UI 精化与质量收尾 ✅ 完成

**目标**：UI 设计精化、性能优化、测试补全

- [x] 全局暖色调设计系统落地
- [x] 卡片悬停动画、按钮交互反馈
- [x] 移动端响应式适配
- [x] 空状态页设计
- [x] 加载骨架屏动画
- [x] 图片懒加载组件
- [x] 前端：路由懒加载（已默认配置）
- [x] 配置 Docker Compose 部署
- [x] 编写启动文档

**验收**：UI 在移动端/桌面端均表现良好，支持 Docker 一键部署

---

## API 端点概览

```
认证模块 /api/auth
  POST /register, /login, /logout
  GET  /me

内容模块 /api/contents
  GET / POST / PUT /{id} / DELETE /{id}
  GET /feed (订阅流)

评论模块 /api/contents/{id}/comments
  GET / POST / DELETE /{commentId}

订阅模块 /api/subscriptions
  GET /my-subscriptions
  POST / DELETE /{authorId}

用户模块 /api/users
  GET /{id}, PUT /profile, GET /dashboard

搜索模块 /api/search
  GET /contents?q=, /users?q=

媒体上传 /api/upload
  POST /image, /video
```

---

## 核心 E2E 测试用例

| 旅程 | 步骤 |
|------|------|
| 社交订阅 | 注册 → 搜索作者 → 订阅 → 在 Feed 看到作者内容 |
| 内容搜索 | 搜索关键词 → 查看内容详情 → 登录 → 评论 |
| 内容创作 | 登录 → 发布内容 → 在后台查看浏览量 |
| 完整闭环 | 注册 → 发内容 → 被他人订阅 → 看到订阅数增长 |

---

## 验证方式

1. **后端验证**：`./mvnw test` 运行单元测试
2. **前端验证**：`pnpm test:unit` 运行 Vitest 单元测试
3. **E2E 验证**：`pnpm test:e2e` 运行 Playwright 测试
4. **手动验证**：启动前后端，完成核心用户旅程

---

## 关键文件清单

- `communication-backend/src/main/resources/db/migration/` - 数据库迁移脚本
- `communication-backend/src/main/java/com/communication/config/SecurityConfig.java` - 认证配置
- `communication-frontend/src/stores/auth.ts` - 认证状态管理
- `communication-frontend/src/api/http.ts` - API 请求封装
- `communication-frontend/src/router/index.ts` - 路由配置

---

## 待确认问题

*(已全部确认)*

- [x] 媒体存储方式：本地文件存储
- [x] 用户角色：统一角色，所有用户可发布内容
- [x] UI 组件库：Element Plus
