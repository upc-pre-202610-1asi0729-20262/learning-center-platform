# ACME Learning Center Platform

ACME Learning Center Platform is a state-of-the-art RESTful API built on the foundations of Domain-Driven Design (DDD). Developed with Java 26, Spring Boot 4, and MySQL, the platform provides a robust and scalable solution for managing educational ecosystems. It features a modular architecture organized into bounded contexts and strictly adheres to Command Query Responsibility Segregation (CQRS) principles, ensuring a clean separation between data modification and retrieval operations.

## Tech Stack

- **Language:** Java 26
- **Framework:** Spring Boot 4.0.6
- **Database:** MySQL 8+
- **Persistence:** Spring Data JPA / Hibernate
- **Security:** Spring Security with JWT (JSON Web Tokens)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven 3.9+
- **Containerization:** Docker

## Prerequisites

- Java 26 JDK
- Maven 3.9+
- MySQL 8.0+
- Docker (optional)

## Project Structure

The project is organized into bounded contexts following DDD principles:

```text
src/main/java/com/acme/center/platform/
├── iam/        # Identity and Access Management (Auth, Users, Roles)
├── learning/   # Learning context (Courses, Students, Enrollments)
├── profiles/   # User Profile lifecycle
└── shared/     # Shared kernel (Domain bases, Infrastructure, Interfaces)
```

Each context is further divided into:
- `application`: Command and Query services implementing the application logic.
- `domain`: Aggregates, entities, value objects, and repository interfaces.
- `infrastructure`: Persistence adapters, external service implementations.
- `interfaces`: REST controllers, DTOs (resources), and assemblers.

## Configuration

The project uses profile-based configuration located in `src/main/resources`:
- `application.properties`: Shared defaults.
- `application-dev.properties`: Development settings (default).
- `application-prod.properties`: Production settings.

### Environment Variables

Required environment variables (especially for the `prod` profile):

| Variable                 | Description                | Default (Dev)                         |
|--------------------------|----------------------------|---------------------------------------|
| `DATABASE_URL`           | MySQL host address         | `localhost`                           |
| `DATABASE_PORT`          | MySQL port                 | `3306`                                |
| `DATABASE_NAME`          | Database name              | `learning_center_platform`            |
| `DATABASE_USER`          | Database username          | `root`                                |
| `DATABASE_PASSWORD`      | Database password          | `password`                            |
| `PORT`                   | Application port           | `8080`                                |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile      | `dev`                                 |
| `JWT_SECRET`             | Secret key for JWT signing | `replace-with-a-strong-random-secret` |

## Getting Started

### 1) Database Setup
Ensure MySQL is running and create the database specified in `DATABASE_NAME`.

### 2) Run the Application

#### Using Maven
```bash
# Run with default 'dev' profile
./mvnw clean spring-boot:run

# Run with specific profile and environment variables
SPRING_PROFILES_ACTIVE=prod \
DATABASE_URL=localhost \
DATABASE_PORT=3306 \
DATABASE_NAME=learning-center-os \
DATABASE_USER=root \
DATABASE_PASSWORD=password \
JWT_SECRET=your-secret \
PORT=8080 \
./mvnw clean spring-boot:run
```

#### Using Docker
```bash
# Build the image
docker build -t acme-learning-platform .

# Run the container
docker run -p 8080:8080 \
  -e DATABASE_URL=host.docker.internal \
  -e DATABASE_NAME=learning-center-os \
  -e DATABASE_USER=root \
  -e DATABASE_PASSWORD=password \
  -e JWT_SECRET=your-secret \
  acme-learning-platform
```

### 3) API Documentation
Once running, access the interactive documentation at:
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## Scripts and Commands

- `mvn clean compile`: Compile the project.
- `mvn test`: Run unit and integration tests.
- `mvn package`: Package the application into a JAR file.
- `mvn spring-boot:run`: Launch the application.

Note: `.mvn/jvm.config` includes `--sun-misc-unsafe-memory-access=allow` to suppress Java 26 terminal deprecation warnings.

## API Surface

### IAM (Identity and Access Management)
- `POST /api/v1/authentication/sign-up`: Register a new user.
- `POST /api/v1/authentication/sign-in`: Authenticate and get a JWT.
- `/api/v1/users`: User management.
- `/api/v1/roles`: Role management.

### Learning
- `/api/v1/courses`: Course management.
- `/api/v1/students`: Student management.
- `/api/v1/enrollments`: Enrollment management.

### Profiles
- `/api/v1/profiles`: User profile management.

## Security Model

- **Authentication:** Stateless JWT-based.
- **Password Storage:** BCrypt hashing.
- **Authorization:** Token must be included in the header: `Authorization: Bearer <token>`.

## Development Conventions

- **DDD + CQRS:** Write operations use `CommandService`, read operations use `QueryService`.
- **Persistence Agnostic:** Domain model stays independent of JPA. Repositories in `domain` are interfaces; implementations live in `infrastructure`.
- **Lombok:** Used for technical boilerplate; avoided in aggregates to maintain invariants.
- **Error Handling:** Centralized via `GlobalExceptionHandler` and `ResponseEntityAssembler`.
- **I18n:** Supported via `Accept-Language` header (Default: `en`, also supports `es`).

## Additional Documentation

Refer to the `docs/` folder for more details:
- [User Stories](docs/user-stories.md).
- [Software Architecture (Structurizr DSL)](docs/software-architecture.dsl).
- [Class Diagrams (PlantUML)](docs/class-diagram.puml).

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.

