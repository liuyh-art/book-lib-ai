# 企业级图书管理系统

基于 **SpringBoot 3 + Vue 3** 的前后端分离图书管理系统，集成 **Spring AI + DeepSeek** 智能助手，支持双角色权限控制、图书借还、数据统计与 Prometheus 监控。

## 技术栈

### 后端
- SpringBoot 3.2 + Java 17
- MySQL 8 + MyBatis 3.0.3 (XML 配置)
- PageHelper 2.1.0 分页
- JWT (jjwt 0.12.3) 无状态鉴权
- Spring Security Crypto (BCrypt) 密码加密
- Spring Boot Actuator + Micrometer Prometheus
- Spring AOP / Validation
- Hutool 5.8.25 工具集
- Spring AI 1.0.0-M6 (DeepSeek 集成)

### 前端
- Vue 3.4 (Composition API + `<script setup>`)
- Vite 8 构建
- Element Plus 2.5 UI 组件库
- Vue Router 4 + 路由守卫
- Pinia 状态管理
- Axios 请求封装
- ECharts 6 + Vue-ECharts 8 数据可视化

## 项目结构

```
├── backend/
│   ├── pom.xml
│   ├── API_DOCUMENT.md                        # API 文档
│   └── src/main/
│       ├── resources/
│       │   ├── application.yml                # 项目配置
│       │   ├── book_rule.txt                  # AI 知识库规则文件
│       │   └── mapper/                        # MyBatis XML映射
│       │       ├── SysUserMapper.xml
│       │       ├── BookCategoryMapper.xml
│       │       ├── BookMapper.xml
│       │       └── BorrowRecordMapper.xml
│       └── java/com/example/book/
│           ├── BookApplication.java           # 启动类
│           ├── common/                        # 公共类
│           │   ├── Result.java                # 统一返回体
│           │   ├── ErrorCode.java             # 错误码枚举
│           │   ├── BusinessException.java     # 业务异常
│           │   ├── GlobalExceptionHandler.java # 全局异常处理
│           │   └── JwtInterceptor.java        # JWT拦截器
│           ├── config/                        # 配置类
│           │   ├── JwtConfig.java
│           │   ├── WebMvcConfig.java          # CORS + 拦截器
│           │   └── MetricsConfig.java         # Prometheus指标
│           ├── controller/                    # 控制器层
│           │   ├── AuthController.java
│           │   ├── UserController.java
│           │   ├── CategoryController.java
│           │   ├── BookController.java
│           │   ├── BorrowController.java
│           │   ├── DashboardController.java
│           │   └── BookAiController.java      # AI 智能助手
│           ├── service/                       # 服务接口
│           │   ├── SysUserService.java
│           │   ├── BookCategoryService.java
│           │   ├── BookService.java
│           │   └── BorrowRecordService.java
│           │   └── impl/                      # 服务实现
│           │       ├── SysUserServiceImpl.java
│           │       ├── BookCategoryServiceImpl.java
│           │       ├── BookServiceImpl.java
│           │       ├── BorrowRecordServiceImpl.java
│           │       ├── BookAiAgentService.java # AI 对话服务
│           │       └── BookRagService.java    # RAG 知识库检索
│           ├── mapper/                        # Mapper接口
│           ├── entity/                        # 实体类
│           ├── dto/                           # 入参
│           ├── vo/                            # 出参
│           └── util/
│               ├── JwtUtil.java               # JWT工具类
│               └── BookAgentTool.java         # AI 工具函数
├── frontend/
│   ├── package.json
│   ├── vite.config.js                         # Vite配置 + API/Actuator代理
│   ├── index.html
│   └── src/
│       ├── main.js                            # 入口
│       ├── App.vue
│       ├── api/                               # API请求封装
│       │   ├── request.js                     # Axios拦截器
│       │   ├── auth.js
│       │   ├── user.js
│       │   ├── category.js
│       │   ├── book.js
│       │   ├── borrow.js
│       │   ├── dashboard.js
│       │   └── ai.js                          # AI 对话接口
│       ├── router/
│       │   └── index.js                       # 路由守卫 + 权限控制
│       ├── stores/
│       │   └── user.js                        # Pinia用户状态
│       ├── components/
│       │   └── AIConsultant/
│       │       └── index.vue                  # AI 智能助手组件
│       └── views/
│           ├── login/Login.vue                # 登录/注册
│           ├── layout/Layout.vue              # 主布局
│           ├── home/Home.vue                  # 首页看板
│           ├── user/User.vue                  # 用户管理
│           ├── category/Category.vue          # 分类管理
│           ├── book/Book.vue                  # 图书管理
│           ├── borrow/Borrow.vue              # 借阅管理
│           └── profile/Profile.vue            # 个人中心
├── database/
│   ├── init.sql                               # 建表SQL + 初始化数据
│   ├── fix_password.sql                       # 密码修复脚本
│   └── index_optimize.sql                     # 索引优化脚本
├── README.md
└── CLAUDE.md
```

## 数据库设计

### 四张核心表

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `sys_user` | 系统用户 | username(唯一), password(BCrypt), role(1=管理员,2=读者), status |
| `book_category` | 图书分类 | category_name(唯一), sort |
| `book` | 图书信息 | book_name, author, publisher, isbn(唯一), category_id, stock, total_stock |
| `borrow_record` | 借阅记录 | user_id, book_id, borrow_time, return_time, status(0=借出,1=归还) |

所有表均包含 `is_deleted`, `create_time`, `update_time` 三个通用字段，并建有合适的单字段/联合索引。

### 初始化账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通读者 | reader | 123456 |

## API 接口总览

### 认证模块 `/api/auth`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | 公开 |
| POST | `/api/auth/register` | 注册 | 公开 |
| GET | `/api/auth/me` | 获取当前用户 | 登录 |

### 用户管理 `/api/user`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/user/page` | 分页查询 | 管理员 |
| POST | `/api/user` | 新增用户 | 管理员 |
| PUT | `/api/user` | 编辑用户 | 管理员 |
| DELETE | `/api/user/{id}` | 删除用户 | 管理员 |
| PUT | `/api/user/password` | 修改密码 | 登录 |

### 分类管理 `/api/category`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/category/list` | 全量列表 | 登录 |
| GET | `/api/category/page` | 分页查询 | 管理员 |
| GET | `/api/category/{id}` | 详情 | 管理员 |
| POST | `/api/category` | 新增 | 管理员 |
| PUT | `/api/category` | 编辑 | 管理员 |
| DELETE | `/api/category/{id}` | 删除 | 管理员 |

### 图书管理 `/api/book`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/book/page` | 多条件分页 | 登录 |
| GET | `/api/book/list` | 全部上架图书 | 登录 |
| GET | `/api/book/{id}` | 详情 | 登录 |
| POST | `/api/book` | 新增 | 管理员 |
| PUT | `/api/book` | 编辑 | 管理员 |
| DELETE | `/api/book/{id}` | 删除 | 管理员 |

### 借阅管理 `/api/borrow`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/borrow/borrow` | 借书 | 读者 |
| POST | `/api/borrow/return` | 还书 | 读者 |
| GET | `/api/borrow/page` | 全部记录 | 管理员 |
| GET | `/api/borrow/my` | 个人记录 | 读者 |

### AI 智能助手 `/api/ai`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/ai/chat` | SSE 对话流 | 登录 |

### 数据统计 `/api/dashboard`
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/dashboard/stats` | 首页统计 | 登录 |

## AI 智能助手

系统集成了 **Spring AI + DeepSeek** 作为智能图书助手：

### 架构
- **BookAiController** — SSE (Server-Sent Events) 流式对话端点
- **BookAiAgentService** — 基于 Spring AI 的 `ChatClient` + 工具调用，驱动 DeepSeek 模型进行多轮对话
- **BookRagService** — 读取 `book_rule.txt` 知识库规则，为 AI 提供借阅规则上下文（RAG）
- **BookAgentTool** — 通过 `@Tool` 注解注册业务工具函数，AI 可自动调用查询图书、用户信息等

### 能力
- 📚 查询图书信息（按名称、作者、分类）
- 👤 查询用户借阅记录
- 📋 回答借阅规则相关问题（基于 RAG 知识库）
- 💬 流式响应，实时展示思考过程

## 项目启动

### 前置条件
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+

### 1. 初始化数据库
```bash
mysql -u root -p < database/init.sql
```

### 2. 启动后端
```bash
cd backend
mvn clean package -DskipTests
java -jar target/book-manager-1.0.0.jar
```
或者使用 IDE 直接运行 `BookApplication.java`

后端默认监听 `http://localhost:8081`

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端默认监听 `http://localhost:3000`

Vite 已配置代理转发 `/api` 和 `/actuator` → `http://localhost:8081`

### 4. 访问系统
打开浏览器访问 `http://localhost:3000`
- 管理员：admin / 123456
- 读者：reader / 123456

### 5. Prometheus 监控
后端暴露 `/actuator/prometheus` 端点，可配置 Prometheus 服务抓取：
```bash
# 安装 Prometheus 后，配置 scrape_configs 指向后端
prometheus --config.file=prometheus.yml
```
访问 `http://localhost:9090` 查看 Prometheus UI

## 监控指标

| 指标名 | 类型 | 说明 | 标签 |
|--------|------|------|------|
| `book_borrow_total` | Counter | 借书总次数 | type="borrow" |
| `book_return_total` | Counter | 还书总次数 | type="return" |
| `jvm_*` | Gauge | JVM 指标 (Micrometer 自动) | application |
| `system_*` | Gauge | 系统指标 | application |

## 并发借书防超借解决方案

本系统采用 **乐观锁 + 事务 + 库存预校验** 三重保障：

### 方案原理
1. **数据库层乐观锁**：扣减库存时使用 `UPDATE book SET stock = stock - 1 WHERE id = ? AND stock > 0`，利用 MySQL 行锁保证同一时刻只有一个请求可扣减成功
2. **业务层前置校验**：`Service` 中先查询库存是否充足，再进行扣减
3. **事务保障**：`@Transactional` 保证借书操作（扣库存 + 新增借阅记录）原子性
4. **业务规则校验**：每人同时借阅不超过5本、同一本书不可重复借出

### 关键代码
```java
// borrowBook() 中的核心逻辑
@Transactional
public void borrowBook(Long userId, Long bookId) {
    Book book = bookMapper.selectById(bookId);  // 1. 查询库存
    if (book.getStock() <= 0) {
        throw new BusinessException(ErrorCode.BOOK_OUT_OF_STOCK); // 2. 校验
    }
    // ... 其他校验 ...
    int updated = bookMapper.updateStock(bookId, book.getStock() - 1); // 3. 乐观锁扣减
    if (updated == 0) {
        throw new BusinessException(ErrorCode.BOOK_OUT_OF_STOCK, "库存更新失败");
    }
    borrowRecordMapper.insert(record); // 4. 新增记录
}
```

### 防超借结论
- **单实例/单库**：乐观锁 + 事务完全解决，无需引入分布式锁
- **高并发场景**：若需更高吞吐，可在 `book` 表增加 `version` 字段实现版本号乐观锁，或引入 Redis 分布式锁

## 线上部署

### 后端部署
```bash
# 构建
cd backend
mvn clean package -DskipTests

# 部署（修改 application.yml 中数据库连接信息）
scp target/book-manager-1.0.0.jar user@server:/app/
java -jar /app/book-manager-1.0.0.jar --spring.profiles.active=prod
```

### 前端部署
```bash
cd frontend
npm run build
# 将 dist/ 目录部署到 Nginx
```

### Nginx 配置示例
```nginx
server {
    listen 80;
    server_name book.example.com;

    root /var/www/book-manager/dist;
    index index.html;

    location /api/ {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```
