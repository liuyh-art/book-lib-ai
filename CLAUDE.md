# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Backend
cd backend
mvn clean package -DskipTests          # Build
java -jar target/book-manager-1.0.0.jar  # Run (port 8081)

# Frontend
cd frontend
npm install                            # Install deps
npm run dev                            # Dev server (port 3000, proxies /api + /actuator → 8081)

# Database
mysql -u root -p < database/init.sql   # Init schema + seed data
```

**Java 17 required.** Spring Boot 3.2 `jakarta.*`.

## Architecture

```
controller → service(interface+impl) → mapper → MyBatis XML → MySQL
                                      ↕
                    Result<T>, JwtInterceptor (ThreadLocal), GlobalExceptionHandler

AI Layer:
  BookAiController → BookAiAgentService (ChatClient + @Tool) → BookRagService (RAG)
                                                            → BookAgentTool (tool functions)
```

### Backend layers (package `com.example.book`)
- **controller/** — REST endpoints. Methods: `listUsers(...)`, `addUser(...)`. Route via `@RequestMapping("/api/xxx")`. Get current user via `JwtInterceptor.getCurrentUser()`.
- **dto/** — Request payloads with `@NotBlank`/`@NotNull` validation annotations.
- **vo/** — Response view objects (includes joined fields like `categoryName`, `userName`).
- **service/ + service/impl/** — Business logic. `@Transactional` on write operations.
- **mapper/** — MyBatis interfaces. Methods map to XML in `resources/mapper/`.
- **entity/** — Data objects mirroring DB columns (camelCase).
- **common/** — `Result<T>` (unified response), `ErrorCode` (enum), `BusinessException`, `GlobalExceptionHandler`, `JwtInterceptor`.

### AI Layer (package `com.example.book`)
- **BookAiController** — SSE (Server-Sent Events) streaming chat endpoint (`GET /api/ai/chat`)
- **BookAiAgentService** — Spring AI `ChatClient` with tool calling, drives DeepSeek model
- **BookRagService** — Reads `book_rule.txt` as RAG context for borrowing rules
- **BookAgentTool** — `@Tool`-annotated methods for AI to query books, users, etc.

### Key patterns
- **All SQL in XML** — zero `@Select`/`@Update` annotations. XML uses `<include refid="WhereClause"/>` and `<sql>` fragments for DRY queries.
- **All queries filter `is_deleted = 0`** via `<sql id="WhereClause">`.
- **PageHelper pagination** — call `PageHelper.startPage(pageNum, pageSize)` before a mapper method. The XML must NOT have manual `LIMIT/OFFSET`.
- **N+1 prevention** — batch load related data via `Map<Long, Entity>` in service layer instead of per-row `convertToVO()` queries.

### Permission model
- **Admin (role=1)**: user/category/book CRUD, all borrow records, dashboard stats.
- **Reader (role=2)**: browse books, borrow/return, personal records only.
- Frontend: route meta `roles: [1]` hides admin menus. Backend: JwtInterceptor checks role.

### Database (4 tables)
- `sys_user` — login, BCrypt password, role (1=admin, 2=reader)
- `book_category` — name, sort order
- `book` — name, author, isbn, stock/total_stock, category_id (FK)
- `borrow_record` — user_id (FK), book_id (FK), borrow_time, return_time, status

## Common Commands

```bash
cd backend
mvn clean compile                           # Quick compilation check
mvn clean package -DskipTests               # Production JAR
java -jar target/book-manager-1.0.0.jar     # Start server (port 8081)

cd frontend
npm install                                 # First time setup
npm run dev                                 # Start dev server (hot reload, port 3000)
npm run build                               # Production build → dist/
```

## Testing (no test suite configured yet)
- Manual API testing: start backend + frontend, use the web UI.
- Actuator health: `http://localhost:8081/actuator/health`
- Prometheus: `http://localhost:8081/actuator/prometheus`

## Common pitfalls
- **When adding a new mapper method**, add the SQL in the XML file under `resources/mapper/`, NOT as an annotation.
- **PageHelper conflict**: never put `LIMIT/OFFSET` in the XML if the service calls `PageHelper.startPage()`.
- **Concurrent stock**: use `decrementStock(id)` (atomic `stock - 1`) and `incrementStock(id)` (atomic `stock + 1`) in BookMapper, NOT read-then-write.
- **Lombok required**: IDE needs Lombok plugin for `@Data` on entities.
- **Spring AI milestone repository**: The `pom.xml` includes a `spring-milestones` repository (`https://repo.spring.io/milestone`) required for `spring-ai-openai-spring-boot-starter`. Ensure Maven has access.
- **AI API key**: DeepSeek API key is configured in `application.yml`. Do not commit real keys to git.
