# API 接口文档

## 通用规范

### 基础 URL

```
http://localhost:8080/api
```

### 请求/响应格式

- 所有请求 Body 使用 `application/json`（文件上传除外）
- 所有响应统一封装为 `ApiResponse<T>`：

```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... }
}
```

**错误响应**：

```json
{
  "success": false,
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

### 认证方式

需要认证的接口请在请求头中携带：

```
Authorization: Bearer <JWT Token>
```

### 分页响应结构（PageResponse）

```json
{
  "content": [ ... ],
  "totalElements": 100,
  "totalPages": 10,
  "page": 0,
  "size": 10
}
```

---

## 认证接口（/api/auth）

### 用户注册

```
POST /api/auth/register
```

**请求体**：

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "securepassword"
}
```

**响应**（201 Created）：

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "avatarUrl": null,
      "bio": null
    }
  }
}
```

### 用户登录

```
POST /api/auth/login
```

**请求体**：

```json
{
  "username": "alice",
  "password": "securepassword"
}
```

**响应**（200 OK）：同注册响应结构。

### 获取当前用户

```
GET /api/auth/me
Authorization: Bearer <token>
```

**响应**：返回当前登录用户的 `UserDto`。

---

## 用户接口（/api/users）

### 获取用户信息

```
GET /api/users/{id}
```

**响应**：

```json
{
  "id": 1,
  "username": "alice",
  "avatarUrl": "/uploads/avatars/xxx.jpg",
  "bio": "内容创作者",
  "createdAt": "2024-01-01T00:00:00"
}
```

---

## 内容接口（/api/contents）

### 创建内容

```
POST /api/contents
Authorization: Bearer <token>
```

**请求体**：

```json
{
  "title": "文章标题",
  "body": "正文内容...",
  "mediaUrl": "/uploads/images/xxx.jpg",
  "mediaType": "IMAGE",
  "status": "PUBLISHED",
  "tags": ["技术", "Vue"]
}
```

**响应**（201 Created）：返回创建的 `ContentDto`。

### 获取内容列表

```
GET /api/contents?page=0&size=10
```

返回已发布内容的分页列表，按时间降序。

### 获取内容详情

```
GET /api/contents/{id}
```

访问时自动增加浏览量。

### 更新内容

```
PUT /api/contents/{id}
Authorization: Bearer <token>
```

**请求体**：同创建内容，所有字段均可更新。

### 删除内容

```
DELETE /api/contents/{id}
Authorization: Bearer <token>
```

**响应**（200 OK）：

```json
{ "success": true, "message": "Content deleted successfully", "data": null }
```

### 获取指定作者内容

```
GET /api/contents/user/{authorId}?page=0&size=10
```

返回该作者已发布的内容列表。

### 获取我的内容

```
GET /api/contents/my?status=DRAFT&page=0&size=10
Authorization: Bearer <token>
```

`status` 可选：`PUBLISHED`、`DRAFT`，不传则返回全部。

---

## 评论接口（/api/contents/{contentId}/comments）

### 发表评论

```
POST /api/contents/{contentId}/comments
Authorization: Bearer <token>
```

**请求体**：

```json
{
  "body": "这篇文章写得很好！",
  "parentId": null
}
```

回复评论时传入 `parentId`（父评论 ID）。

### 获取评论列表

```
GET /api/contents/{contentId}/comments?page=0&size=20
```

返回顶级评论及其回复。

### 获取单条评论

```
GET /api/contents/{contentId}/comments/{commentId}
```

### 删除评论

```
DELETE /api/contents/{contentId}/comments/{commentId}
Authorization: Bearer <token>
```

---

## 订阅接口（/api/subscriptions）

### 关注用户

```
POST /api/subscriptions/{authorId}
Authorization: Bearer <token>
```

### 取消关注

```
DELETE /api/subscriptions/{authorId}
Authorization: Bearer <token>
```

### 检查是否已关注

```
GET /api/subscriptions/check/{authorId}
Authorization: Bearer <token>
```

**响应**：`{ "data": true }`

### 我的订阅列表

```
GET /api/subscriptions/my?page=0&size=20
Authorization: Bearer <token>
```

### 粉丝列表

```
GET /api/subscriptions/followers/{userId}?page=0&size=20
```

### 订阅/粉丝数量

```
GET /api/subscriptions/count/{userId}
```

**响应**：`{ "data": { "subscriptions": 10, "followers": 50 } }`

### 订阅 Feed 流

```
GET /api/subscriptions/feed?page=0&size=10
Authorization: Bearer <token>
```

---

## 搜索接口（/api/search）

### 搜索内容

```
GET /api/search/contents?q=关键词&tag=标签&page=0&size=10
```

### 搜索用户

```
GET /api/search/users?q=用户名&page=0&size=10
```

### 热门标签

```
GET /api/search/tags/popular?limit=20
```

**响应**：`{ "data": ["技术", "生活", "摄影"] }`

### 标签建议

```
GET /api/search/tags/suggest?q=前缀
```

**响应**：`{ "data": ["前端", "前沿", "前置"] }`

---

## 后台管理接口（/api/dashboard）

### 获取统计数据

```
GET /api/dashboard/stats
Authorization: Bearer <token>
```

**响应**：

```json
{
  "data": {
    "totalContents": 25,
    "totalViews": 1234,
    "totalComments": 89,
    "followerCount": 156
  }
}
```

### 获取我的内容列表

```
GET /api/dashboard/contents?status=PUBLISHED&page=0&size=10
Authorization: Bearer <token>
```

### 更新个人资料

```
PUT /api/dashboard/profile
Authorization: Bearer <token>
```

**请求体**：

```json
{
  "username": "new_username",
  "bio": "新的个人简介"
}
```

### 上传头像

```
POST /api/dashboard/avatar
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <图片文件>
```

---

## 媒体上传接口（/api/upload）

### 上传图片

```
POST /api/upload/image
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <图片文件>
```

**支持格式**：JPEG、PNG、GIF、WebP  
**响应**：

```json
{
  "data": {
    "url": "/uploads/images/uuid.jpg",
    "mediaType": "IMAGE"
  }
}
```

### 上传视频

```
POST /api/upload/video
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <视频文件>
```

**支持格式**：MP4、WebM、MOV  
**响应**：

```json
{
  "data": {
    "url": "/uploads/videos/uuid.mp4",
    "mediaType": "VIDEO"
  }
}
```

---

## 静态资源访问

上传的文件通过以下 URL 直接访问（无需认证）：

```
GET /uploads/{type}/{filename}
```

示例：`GET /uploads/images/abc123.jpg`
