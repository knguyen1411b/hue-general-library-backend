# Hue General Library Backend

A production-oriented Spring Boot backend for a digital library management system, covering authentication, member lifecycle, circulation, subscriptions, fines, payments, notifications, and warehouse location management for physical book items.

> [!IMPORTANT]
> This service is API-first. Use Swagger UI for endpoint exploration and payload contracts.

## Why This Project

This project demonstrates backend engineering practices expected in real products:

- Domain-based module structure (`auth`, `user`, `book`, `rental`, `fine`, `payment`, `notification`, `warehouse`, ...)
- JWT access/refresh token flow with password reset workflow
- PostgreSQL + Flyway migration-based schema management
- Cloudinary integration for file/image upload
- Background schedulers for operational jobs (rental, notification)
- Validation, exception handling, and consistent API response wrappers
- Unit/integration-oriented test setup with H2 profile

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT (`jjwt`)
- Cloudinary SDK
- Spring Mail
- springdoc OpenAPI (Swagger UI)
- Gradle

## Main API Areas

All APIs are versioned under `/api/v1`.

- `auth`: sign in/up, refresh token, sign out, change/reset password
- `users`: profile (`/me`) and administrative user management
- `categories`, `books`, `book-items`: catalog and inventory
- `rentals`, `fines`, `payments`: circulation and penalty/payment lifecycle
- `subscriptions`, `user-subscriptions`: membership plans and enrollments
- `library-cards`: card issuance and card request flows
- `notifications`: user/admin notification operations
- `warehouse`: floors, aisles, shelves, positions
- `audit-logs`, `configurations`

Swagger UI:

- `http://localhost:${PORT}/swagger-ui/index.html`

## Project Structure

```text
src/main/java/org/app/backend
├── common        # shared dto, exception, constants, swagger helpers
├── config        # security and application configuration
├── core          # cross-cutting services (file, mail)
└── modules       # domain modules (auth, user, book, rental, ...)

src/main/resources
├── application.yaml
└── db/migration  # Flyway SQL migrations
```

## Getting Started

### 1) Prerequisites

- JDK 21
- PostgreSQL
- Gradle (or use provided wrapper)

### 2) Configure Environment

Copy `.env.example` to `.env` and provide required values:

- App metadata: `APPLICATION_NAME`, `APPLICATION_VERSION`
- Super admin bootstrap account
- JWT secrets and expiration settings
- CORS allow-list (`CORS_ALLOWED_ORIGIN_PATTERNS`)
- PostgreSQL datasource settings
- Mail credentials (SMTP)
- Cloudinary credentials
- Runtime tuning (Tomcat + task pool)

> [!NOTE]
> The Gradle run task reads variables from the root `.env` file automatically.

### 3) Run Locally

```bash
./gradlew clean build
./gradlew bootRun
```

Windows:

```bash
gradlew.bat clean build
gradlew.bat bootRun
```

### 4) Run Tests

```bash
./gradlew test
```

Tests use H2 in-memory database via `src/test/resources/application-test.yaml`.

## Database & Migration

- Flyway is enabled in `application.yaml`
- Initial schema is managed in `src/main/resources/db/migration/V1__init_schema.sql`
- JPA `ddl-auto` is currently `update` for local development convenience

> [!WARNING]
> For staging/production, prefer stricter migration-only schema control (avoid relying on `ddl-auto`).

## Docker

Quick start with Docker Compose:

```bash
docker compose up --build
```

Or build/run manually with the provided `Dockerfile`:

```bash
docker build -t hue-library-backend .
docker run --env-file .env -p 8080:8080 hue-library-backend
```

## Profiles

- Default profile: development-friendly settings in `application.yaml`
- Production profile: stricter defaults in `application-prod.yaml`

Run with production profile:

```bash
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## Production Readiness Notes

Before deploying publicly, review and harden:

- Secret management (do not store real secrets in repo)
- CORS policy and frontend callback URLs
- SMTP account policies and app-password rotation
- Monitoring/logging and error alerting
- CI pipeline for test + formatting checks

## Team

- **Tech Lead / Main Developer & Project Manager**: Khánh Nguyên (`knguyen1411b`)

### Core Members

- `nhathuynguyen19`
- `naundylan`
- `miracleSw`
- `Danhminhtai`

### Contribution Board

- Contributors list: https://github.com/knguyen1411b/hue-general-library-backend/graphs/contributors
