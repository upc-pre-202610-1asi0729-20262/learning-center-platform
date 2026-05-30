# ACME Learning Center Platform

ACME Learning Center Platform is a DDD-based REST API built with Spring Boot, Java 26, and MySQL.
The project follows a modular architecture with bounded contexts and applies CQRS terminology consistently through command and query services.

## Quick Start

### Prerequisites

- Java 26
- Maven 3.9+
- MySQL 8+

### 1) Configure database and app settings

The project uses profile-based configuration:

- `src/main/resources/application.properties` (shared defaults)
- `src/main/resources/application-dev.properties` (development)
- `src/main/resources/application-prod.properties` (production)

By default, the app runs with `dev` profile unless `SPRING_PROFILES_ACTIVE` is provided.

Environment variables (aligned with `Dockerfile`):

- `DATABASE_URL` (database host)
- `DATABASE_PORT`
- `DATABASE_NAME`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `PORT`
- `SPRING_PROFILES_ACTIVE`
- `JWT_SECRET` (required for `prod`)

You can start from `.env.example`.

### 2) Run the application

```bash
SPRING_PROFILES_ACTIVE=dev mvn clean spring-boot:run
```

Run with production profile (requires all prod env vars):

```bash
SPRING_PROFILES_ACTIVE=prod \
DATABASE_URL=localhost \
DATABASE_PORT=3306 \
DATABASE_NAME=learning-center-os \
DATABASE_USER=root \
DATABASE_PASSWORD=password \
JWT_SECRET=replace-with-a-strong-random-secret \
PORT=8080 \
mvn clean spring-boot:run
```

### 3) Open API docs

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Build and Test

```bash
mvn clean compile
mvn test
```

Run tests with explicit development profile if needed:

```bash
SPRING_PROFILES_ACTIVE=dev mvn test
```

Note: `.mvn/jvm.config` includes `--sun-misc-unsafe-memory-access=allow` to suppress Java 26 terminal deprecation warnings emitted by Lombok internals.

## Architecture Overview

The platform is split into three bounded contexts:

- `profiles`: user profile lifecycle and profile lookup capabilities
- `learning`: courses, learning paths, students, and enrollments
- `iam`: authentication, user/role management, JWT issuance and verification

Cross-context communication is implemented via explicit anti-corruption layer (ACL) interfaces and adapters.

## API Surface (by Context)

- Profiles: `/api/v1/profiles`
- Learning:
  - `/api/v1/courses`
  - `/api/v1/courses/{courseId}/learning-path-items`
  - `/api/v1/students`
  - `/api/v1/students/{studentRecordId}/enrollments`
  - `/api/v1/enrollments`
- IAM:
  - `/api/v1/authentication`
  - `/api/v1/users`
  - `/api/v1/roles`

## Security Model

- JWT-based stateless authentication
- Password hashing via BCrypt
- Open endpoints:
  - `/api/v1/authentication/**`
  - `/v3/api-docs/**`
  - `/swagger-ui.html`
  - `/swagger-ui/**`
  - `/swagger-resources/**`
  - `/webjars/**`
- All other endpoints require authentication

### Get a token (example)

```bash
curl -X POST "http://localhost:8080/api/v1/authentication/sign-in" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

Then include:

```bash
Authorization: Bearer <your-token>
```

## DDD + CQRS Conventions Used

- Write operations are represented as commands and handled by `*CommandService`.
- Read operations are represented as queries and handled by `*QueryService`.
- Domain model stays persistence-agnostic.
- Repository ports live in `*/domain/repositories`.
- JPA repositories target persistence entities and are named `*PersistenceRepository`.
- Infrastructure adapters bridge domain repositories and JPA persistence.

## Error Handling and I18n

- Application flow uses `Result<T, ApplicationError>` in the application layer.
- REST responses are assembled centrally through `ResponseEntityAssembler` and `ErrorResponseAssembler`.
- Global exception handling is implemented in `shared/interfaces/rest/GlobalExceptionHandler`.
- Localized messages are supported through `Accept-Language` using `messages*.properties` bundles.
- Currently supported locales: `en` (default), `es`.

## Project Structure

```text
src/main/java/com/acme/center/platform/
  iam/
  learning/
  profiles/
  shared/
```

Each bounded context is organized around:

- `application` (command/query services)
- `domain` (aggregates, entities, value objects, commands, queries)
- `infrastructure` (persistence adapters, technical services)
- `interfaces` (REST controllers/resources/assemblers)

## Development Conventions

### Layering and Persistence Boundaries

- Domain aggregates, entities, and value objects live in context `domain` packages and remain persistence-agnostic.
- Domain repository ports live under context `domain/repositories` packages.
- Spring Data JPA repositories target persistence entities only (`*PersistenceRepository`).
- Repository adapters in infrastructure bridge domain repository ports and persistence repositories.
- Shared bases are separated: domain aggregates use `shared/domain/model/aggregates/AbstractDomainAggregateRoot`, while persistence entities use infrastructure persistence bases such as `shared/infrastructure/persistence/jpa/entities/AuditableAbstractPersistenceEntity`.

### Lombok Usage Convention

- Domain model (`*/domain/*`): prefer explicit behavior; use Lombok only when it does not hide invariants.
- Avoid `@Data` in aggregates/entities to prevent accidental broad mutability.
- Infrastructure (`*/infrastructure/*`): Lombok can be used more broadly for technical boilerplate.
- REST resources favor Java records.

### Logging Convention

- Use Lombok `@Slf4j` for technical logging in application handlers, controllers, filters, and infrastructure services.
- Keep domain model classes (`*/domain/*`) free from logging framework annotations.

## Additional Documentation

- `docs/user-stories.md`
- `docs/class-diagram.puml`
- `docs/software-architecture.dsl`

## Reference Links

- Maven: https://maven.apache.org/guides/index.html
- Spring Boot: https://docs.spring.io/spring-boot/
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/
- Spring Security: https://docs.spring.io/spring-security/reference/

