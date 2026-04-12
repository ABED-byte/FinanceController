# Finance Tracker API

Personal finance backend built with **Java 21** and **Spring Boot 3**. Users manage their own transactions, categories, savings goals, and dashboard metrics. Access is controlled with **JWT** and **role-based** endpoints.

## Tech stack

- Spring Boot 3.5 (Web, Security, Data JPA, Validation)
- JWT (**jjwt** 0.12)
- **MySQL**
- **Redis** (Caching)
- Lombok, Maven
- **SpringDoc OpenAPI 3** (Swagger UI)

## Prerequisites

- JDK **21**
- **MySQL 8** running locally
- **Redis server** running locally (port 6379)
- Maven 3.9+

## Configuration

Main settings live in `src/main/resources/application.properties`.

| Property / env | Purpose |
|----------------|---------|
| `spring.datasource.url` | MySQL JDBC URL (default targets database `finance_db`) |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials (required if not inlined in properties) |
| `JWT_SECRETKEY` | Base64-encoded secret for HS256 (must be long enough; **set in production**) |
| `REDIS_HOST` / `REDIS_PORT` | Redis server connection (defaults to localhost:6379) |
| `jwt.expiration-ms` | JWT lifetime (default 24 hours) |

**Tests** use `src/test/resources/application-test.properties` (in-memory H2). Run with:

```bash
mvn test
```

## Run the application

```bash
mvn spring-boot:run
```

Default URL: **http://localhost:8080**

Ensure MySQL and Redis are running. Set `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRETKEY` env vars if they are not hardcoded in `application.properties`.

## Caching

The application uses **Redis** for caching expensive dashboard aggregations and category lists. 

- **Caches**: `dashboard_summary`, `dashboard_trends`, `categories`
- **TTL**: 10 minutes
- **Eviction**: Automatic on transaction, category, or goal updates to ensure data consistency.

## API documentation (Swagger)

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Use **Authorize** and send `Bearer <token>` after logging in.

## Authentication and roles

- **Register** (`POST /api/auth/register`): body `{ "name", "email", "password" }`. New accounts are always **`VIEWER`** (role cannot be chosen by the client).
- **Login** (`POST /api/auth/login`): returns `token`, `role`, `name`.

| Role | Typical access |
|------|------------------|
| **VIEWER** | Read transactions, categories, dashboard summary, goals |
| **ANALYST** | Viewer + create/update data where allowed + dashboard trends |
| **ADMIN** | Full access, user management, soft-delete transactions, category budget updates |

Exact rules are enforced with `@PreAuthorize` on controllers.

## Admin: promote users

Admins can grant **ANALYST** or **ADMIN** explicitly:

- **`POST /api/users/{id}/promote`** — body `{ "role": "ANALYST" }` or `{ "role": "ADMIN" }`

To set any role (including **`VIEWER`** to revoke elevated access):

- **`PATCH /api/users/{id}/role`** — body `{ "role": "VIEWER" | "ANALYST" | "ADMIN" }`

You cannot demote the **last** remaining **ADMIN** (guarded in the service layer).

Other admin endpoints: `GET /api/users`, `PATCH /api/users/{id}/status`.

## Seeded data (empty database only)

On first startup when the `users` table is empty, `DataSeeder` creates:

| Email | Password | Role |
|-------|----------|------|
| admin@finance.com | admin123 | ADMIN |
| analyst@finance.com | analyst123 | ANALYST |
| viewer@finance.com | viewer123 | VIEWER |

Sample categories, transactions, and a current-month savings goal are attached to the **analyst** user.

## API overview

| Area | Base path |
|------|-----------|
| Auth | `/api/auth` |
| Transactions | `/api/transactions` |
| Categories | `/api/categories` |
| Savings goals | `/api/goals` |
| Dashboard | `/api/dashboard` |
