# Communication Platform

一个现代化的内容发布平台，支持用户注册、内容发布（文本/图片/视频）、订阅、评论、搜索等功能。

## 技术栈

### 后端
- Java 21 + Spring Boot 3.2
- Spring Security + JWT 认证
- Spring Data JPA + MySQL
- Flyway 数据库迁移

### 前端
- Vue 3 + TypeScript
- Pinia 状态管理
- Vue Router
- Element Plus UI
- Vite 构建工具

## 项目结构

```
communication/
├── communication-backend/     # Spring Boot 后端
│   ├── src/main/java/        # Java 源码
│   ├── src/main/resources/   # 配置文件和数据库迁移
│   └── src/test/             # 单元测试
├── communication-frontend/    # Vue 3 前端
│   ├── src/api/              # API 接口封装
│   ├── src/components/       # 可复用组件
│   ├── src/views/            # 页面视图
│   ├── src/stores/           # Pinia 状态管理
│   └── src/router/           # 路由配置
├── docker-compose.yml        # Docker 编排文件
└── plan.md                   # 开发计划文档
```

## 快速开始

### 方式一：Docker 部署（推荐）

1. 确保已安装 Docker 和 Docker Compose

2. 在项目根目录运行：
```bash
docker-compose up -d
```

3. 访问应用：
   - 前端：http://localhost
   - 后端 API：http://localhost:8080/api

### 方式二：本地开发

#### 前置条件
- JDK 21+
- Node.js 20+
- pnpm
- MySQL 8.0+

#### 后端启动

1. 创建 MySQL 数据库：
```sql
CREATE DATABASE communication CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 配置数据库连接（修改 `application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/communication
    username: your_username
    password: your_password
```

3. 启动后端：
```bash
cd communication-backend
./mvnw spring-boot:run
```

后端将在 http://localhost:8080 启动

#### 前端启动

1. 安装依赖：
```bash
cd communication-frontend
pnpm install
```

2. 启动开发服务器：
```bash
pnpm dev
```

前端将在 http://localhost:5173 启动

## 功能特性

### 用户系统
- 用户注册/登录
- JWT Token 认证
- 个人资料编辑
- 头像上传

### 内容管理
- 发布文本/图片/视频内容
- 内容编辑和删除
- 草稿保存
- 标签系统

### 社交互动
- 关注/取消关注用户
- 订阅动态 Feed
- 评论和回复
- 嵌套评论支持

### 搜索功能
- 按关键词搜索内容
- 按标签搜索
- 搜索用户
- 热门标签展示

### 作者后台
- 数据概览（浏览量、评论数、粉丝数）
- 内容管理表格
- 个人资料编辑

## API 接口

### 认证
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户

### 内容
- `GET /api/contents` - 获取内容列表
- `POST /api/contents` - 创建内容
- `GET /api/contents/{id}` - 获取内容详情
- `PUT /api/contents/{id}` - 更新内容
- `DELETE /api/contents/{id}` - 删除内容

### 评论
- `GET /api/contents/{id}/comments` - 获取评论列表
- `POST /api/contents/{id}/comments` - 发表评论
- `DELETE /api/contents/{id}/comments/{commentId}` - 删除评论

### 订阅
- `POST /api/subscriptions/{authorId}` - 关注用户
- `DELETE /api/subscriptions/{authorId}` - 取消关注
- `GET /api/subscriptions/feed` - 获取订阅动态

### 搜索
- `GET /api/search/contents` - 搜索内容
- `GET /api/search/users` - 搜索用户
- `GET /api/search/tags/popular` - 热门标签

### 后台
- `GET /api/dashboard/stats` - 获取统计数据
- `PUT /api/dashboard/profile` - 更新资料
- `POST /api/dashboard/avatar` - 上传头像

## 运行测试

### 后端测试
```bash
cd communication-backend
./mvnw test
```

### 前端测试
```bash
cd communication-frontend
pnpm test:unit
```

## 环境变量

### 后端
| 变量 | 说明 | 默认值 |
|------|------|--------|
| `SPRING_DATASOURCE_URL` | 数据库连接 URL | `jdbc:mysql://localhost:3306/communication` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `root` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | - |
| `JWT_SECRET` | JWT 密钥 | - |
| `UPLOAD_PATH` | 文件上传路径 | `./uploads` |

## 开发计划

详见 [plan.md](./plan.md)
