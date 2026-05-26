# Nobel Prize API (Ktor 3 + PostgreSQL/Neon)

Ktor 3 REST API serving Nobel Prize data with JWT auth and per-user favorites.
Persistence via Exposed + HikariCP against PostgreSQL (Neon.tech in prod, H2 in tests).

## Tech stack

- Kotlin 2.3 / JVM 21
- Ktor 3.5 (Netty, Auth-JWT, ContentNegotiation, CORS, StatusPages, CallLogging)
- Exposed 0.56 (core, jdbc, dao, java-time)
- HikariCP, PostgreSQL JDBC driver
- BCrypt (favre) for password hashing
- H2 (test scope) for the in-memory test database
- smiley4 ktor-openapi-tools (OpenAPI 3.1 + Swagger UI + Redoc)

## API docs

Once the server is running, open one of:

- **Swagger UI** — http://localhost:8080/swagger (try-it-out flow; click *Authorize* and paste the JWT)
- **Redoc** — http://localhost:8080/redoc
- **Raw spec** — http://localhost:8080/openapi.json

## Configuration

`src/main/resources/application.yaml` reads from env vars with defaults:

| Variable            | Purpose                                  | Default                                                  |
|---------------------|------------------------------------------|----------------------------------------------------------|
| `DATABASE_URL`      | JDBC URL for PostgreSQL                  | `jdbc:postgresql://localhost:5432/nobel_prize_db`        |
| `DATABASE_USER`     | DB user                                  | `postgres`                                               |
| `DATABASE_PASSWORD` | DB password                              | `postgres`                                               |
| `JWT_SECRET`        | HMAC256 secret (must be ≥ 32 chars)      | a long dev default — change before deploying             |

### Connecting to Neon.tech

Use the JDBC URL from Neon console, e.g.:

```
jdbc:postgresql://ep-XXXX.eu-central-1.aws.neon.tech/nobel_prize_db?sslmode=require
```

Set it via environment:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://ep-XXXX.eu-central-1.aws.neon.tech/nobel_prize_db?sslmode=require"
$env:DATABASE_USER = "your_neon_user"
$env:DATABASE_PASSWORD = "your_neon_password"
$env:JWT_SECRET = "your-32-or-more-character-secret-here"
./gradlew run
```

Tables are created automatically on first startup (`SchemaUtils.create`).
With `database.seedOnStartup: true` the API is pre-populated with a handful of real prizes (Einstein 1921 Physics, Curie 1903/1911, Hemingway 1954, Mandela 1993) if the `prizes` table is empty.

## Endpoints

| Method | Path                                        | Auth | Description                                  |
|--------|---------------------------------------------|------|----------------------------------------------|
| GET    | `/`                                         | -    | Health/welcome                               |
| GET    | `/health`                                   | -    | Liveness                                     |
| GET    | `/openapi.json`                             | -    | Machine-readable OpenAPI 3.1 spec            |
| GET    | `/swagger`                                  | -    | Swagger UI (interactive docs + try-it-out)   |
| GET    | `/redoc`                                    | -    | Redoc UI (three-pane reference docs)         |
| POST   | `/auth/register`                            | -    | `{username, password}` → `{token, ...}`      |
| POST   | `/auth/login`                               | -    | `{username, password}` → `{token, ...}`      |
| GET    | `/prizes`                                   | JWT  | List all prizes                              |
| GET    | `/prizes/{year}/{category}`                 | JWT  | Prize + laureates                            |
| GET    | `/prizes/{year}/{category}/laureates`       | JWT  | Just the laureates                           |
| GET    | `/users/me/favorites`                       | JWT  | Current user's favorite prizes               |
| POST   | `/users/me/favorites/{prizeId}`             | JWT  | Add prize to favorites                       |
| DELETE | `/users/me/favorites/{prizeId}`             | JWT  | Remove from favorites                        |

`category` ∈ `{physics, chemistry, medicine, literature, peace, economics}`.

JWT lifetime: 30 minutes (configurable via `jwt.expirationMinutes`).
Authorization header format: `Authorization: Bearer <token>`.

## Quick smoke test

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"hunter22hunter22"}'

# Save the token from the response, then:
TOKEN=...
curl http://localhost:8080/prizes -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/prizes/1921/physics -H "Authorization: Bearer $TOKEN"
curl -X POST http://localhost:8080/users/me/favorites/1 -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/users/me/favorites -H "Authorization: Bearer $TOKEN"
```

## Build & run

```bash
./gradlew run         # dev run (port 8080)
./gradlew test        # integration tests on H2
./gradlew shadowJar   # fat jar at build/libs/
```
