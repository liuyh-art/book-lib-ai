# 图书管理系统 - API 接口文档

**基础地址**: `http://localhost:8081/api`  
**鉴权方式**: JWT Token (请求头 `Authorization: Bearer <token>`)  
**统一返回格式**: `Result<T>` = `{ code: 200, message: "操作成功", data: T }`

---

## 目录

| 模块 | 前缀 | 说明 |
|------|------|------|
| 1. 认证模块 | `/api/auth` | 登录、注册、获取当前用户 |
| 2. 用户管理 | `/api/user` | 用户CRUD、修改密码 |
| 3. 分类管理 | `/api/category` | 分类CRUD |
| 4. 图书管理 | `/api/book` | 图书CRUD、分页查询 |
| 5. 借阅管理 | `/api/borrow` | 借书、还书、借阅记录 |
| 6. 数据统计 | `/api/dashboard` | 首页数据看板 |

---

## 1. 认证模块 (`/api/auth`)

### 1.1 POST `/api/auth/login` — 用户登录

**请求体** (`LoginDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "email": "admin@book.com",
      "phone": "13800000000",
      "role": 1,
      "avatar": null,
      "status": 1,
      "createTime": "2026-06-30 00:00:00"
    }
  }
}
```

**错误码**: `1001-用户不存在`, `1002-账号已禁用`, `1004-密码错误`

### 1.2 POST `/api/auth/register` — 用户注册

**请求体** (`RegisterDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名，3-50字符 |
| `password` | String | 是 | 密码，6-50字符 |
| `realName` | String | 否 | 真实姓名 |
| `email` | String | 否 | 邮箱 |
| `phone` | String | 否 | 手机号 |

### 1.3 GET `/api/auth/me` — 获取当前登录用户

**请求头**: `Authorization: Bearer <token>`

**响应**: `UserInfoVO`（同登录返回的 userInfo 结构）

---

## 2. 用户管理 (`/api/user`) — 管理员权限

### 2.1 GET `/api/user/page` — 分页查询用户列表

**请求参数**:

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 10 | 每页条数 |
| `username` | String | 否 | — | 用户名模糊搜索 |
| `realName` | String | 否 | — | 真实姓名模糊搜索 |
| `role` | int | 否 | — | 角色筛选: 1=管理员, 2=读者 |
| `status` | int | 否 | — | 状态筛选: 1=启用, 0=禁用 |

**响应**: `PageVO<UserInfoVO>`

### 2.2 POST `/api/user` — 新增用户

**请求体** (`UserDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `username` | String | 是 | 用户名 |
| `password` | String | 否 | 密码(不传默认123456) |
| `realName` | String | 否 | 真实姓名 |
| `email` | String | 否 | 邮箱 |
| `phone` | String | 否 | 手机号 |
| `role` | int | 否 | 角色 |
| `status` | int | 否 | 状态 |

### 2.3 PUT `/api/user` — 编辑用户

**请求体**: 同 `UserDTO`，需传入 `id`

### 2.4 DELETE `/api/user/{id}` — 删除用户

**路径参数**: `id` — 用户ID

### 2.5 PUT `/api/user/password` — 修改密码

**请求体** (JSON):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `oldPassword` | String | 是 | 原密码 |
| `newPassword` | String | 是 | 新密码 |

---

## 3. 分类管理 (`/api/category`)

### 3.1 GET `/api/category/list` — 获取全部分类

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `status` | int | 否 | 状态筛选: 1=启用, 0=禁用 |

### 3.2 GET `/api/category/page` — 分页查询分类

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 10 | 每页条数 |
| `categoryName` | String | 否 | — | 名称模糊搜索 |
| `status` | int | 否 | — | 状态筛选 |

### 3.3 GET `/api/category/{id}` — 获取分类详情

### 3.4 POST `/api/category` — 新增分类

**请求体** (`CategoryDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `categoryName` | String | 是 | 分类名称 |
| `sort` | int | 否 | 排序号 |
| `status` | int | 否 | 状态 |

### 3.5 PUT `/api/category` — 编辑分类

### 3.6 DELETE `/api/category/{id}` — 删除分类

> ⚠️ 分类下有图书时无法删除，返回错误码 `2102`

---

## 4. 图书管理 (`/api/book`)

### 4.1 GET `/api/book/page` — 多条件分页查询图书

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 10 | 每页条数 |
| `bookName` | String | 否 | — | 书名模糊搜索 |
| `author` | String | 否 | — | 作者模糊搜索 |
| `categoryId` | Long | 否 | — | 分类筛选 |
| `status` | int | 否 | — | 状态筛选: 1=上架, 0=下架 |

**响应**: `PageVO<BookVO>`

| BookVO字段 | 类型 | 说明 |
|-----------|------|------|
| `id` | Long | 主键 |
| `bookName` | String | 书名 |
| `author` | String | 作者 |
| `publisher` | String | 出版社 |
| `isbn` | String | ISBN编号 |
| `categoryId` | Long | 分类ID |
| `categoryName` | String | 分类名称 |
| `stock` | int | 当前库存 |
| `totalStock` | int | 总库存 |
| `description` | String | 图书简介 |
| `status` | int | 状态 |
| `createTime` | String | 创建时间 |
| `updateTime` | String | 更新时间 |

### 4.2 GET `/api/book/list` — 获取全部上架图书

### 4.3 GET `/api/book/{id}` — 获取图书详情

### 4.4 POST `/api/book` — 新增图书

**请求体** (`BookDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `bookName` | String | 是 | 书名 |
| `author` | String | 否 | 作者 |
| `publisher` | String | 否 | 出版社 |
| `isbn` | String | 否 | ISBN |
| `categoryId` | Long | 是 | 分类ID |
| `stock` | int | 是 | 库存 |
| `totalStock` | int | 是 | 总库存 |
| `description` | String | 否 | 简介 |
| `status` | int | 否 | 状态 |

### 4.5 PUT `/api/book` — 编辑图书

### 4.6 DELETE `/api/book/{id}` — 删除图书

---

## 5. 借阅管理 (`/api/borrow`)

### 5.1 POST `/api/borrow/borrow` — 借书

**请求体** (`BorrowDTO`):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `bookId` | Long | 是 | 图书ID |

**业务规则**:
- 每人最多同时借5本
- 同一本书不能重复借出
- 库存不足时无法借出
- 已下架图书无法借出

**错误码**: `2001-图书不存在`, `2002-库存不足`, `2003-图书已下架`, `3002-已借阅`, `3004-超过上限`

### 5.2 POST `/api/borrow/return` — 还书

**请求体**: 同 `BorrowDTO`

### 5.3 GET `/api/borrow/page` — 查询全部借阅记录 (管理员)

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 10 | 每页条数 |
| `userId` | Long | 否 | — | 用户筛选 |
| `bookId` | Long | 否 | — | 图书筛选 |
| `status` | int | 否 | — | 状态: 0=借出中, 1=已归还 |

**响应**: `PageVO<BorrowRecordVO>`

| BorrowRecordVO字段 | 类型 | 说明 |
|-------------------|------|------|
| `id` | Long | 记录ID |
| `userId` | Long | 用户ID |
| `userName` | String | 用户名 |
| `realName` | String | 真实姓名 |
| `bookId` | Long | 图书ID |
| `bookName` | String | 书名 |
| `author` | String | 作者 |
| `isbn` | String | ISBN |
| `borrowTime` | String | 借书时间 |
| `returnTime` | String | 还书时间 |
| `status` | int | 状态 |
| `createTime` | String | 创建时间 |

### 5.4 GET `/api/borrow/my` — 查询个人借阅记录 (读者)

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `pageNum` | int | 否 | 1 | 页码 |
| `pageSize` | int | 否 | 10 | 每页条数 |
| `status` | int | 否 | — | 状态筛选 |

---

## 6. 数据统计 (`/api/dashboard`)

### 6.1 GET `/api/dashboard/stats` — 获取首页统计

**响应**: `DashboardVO`

| 字段 | 类型 | 说明 |
|------|------|------|
| `totalBooks` | Long | 图书总量 |
| `totalCategories` | Long | 分类数量 |
| `totalUsers` | Long | 用户数量 |
| `borrowedBooks` | Long | 当前借阅中数量 |
| `todayBorrows` | Long | 今日借书数 |
| `todayReturns` | Long | 今日还书数 |

---

## 附录：通用返回体说明

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 全局错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录/token过期 |
| 403 | 无权限 |
| 500 | 服务器内部错误 |
| 1001 | 用户不存在 |
| 1002 | 账号已禁用 |
| 1003 | 用户名已存在 |
| 1004 | 密码错误 |
| 2001 | 图书不存在 |
| 2002 | 库存不足 |
| 2003 | 图书已下架 |
| 2101 | 分类不存在 |
| 2102 | 分类下存在图书 |
| 3001 | 借阅记录不存在 |
| 3002 | 该书已借出 |
| 3003 | 该书已归还 |
| 3004 | 借阅超出上限(5本) |
| 4001 | token无效 |
| 4002 | token过期 |
