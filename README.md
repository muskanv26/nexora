# Nexora

**Engineering growth and readiness platform**  
**Java 21 • Spring Boot 3 • PostgreSQL • JPA/Hibernate • Maven • Docker**

## Overview

Nexora is a modular backend platform for managing structured software-engineering learning roadmaps, milestones, and task-level progress.

The project is built with a layered Spring Boot architecture and focuses on clean API design, reusable persistence, validation, observability, and maintainable backend foundations.

## Core Capabilities

- **Roadmaps** — Organize structured engineering-learning paths.
- **Milestones** — Break roadmaps into measurable stages.
- **Task-level Progress** — Track progress at an actionable task level.
- **REST APIs** — Expose application functionality through controller/service/repository layers.
- **Persistence** — PostgreSQL with Spring Data JPA/Hibernate.
- **UUID-based Domain Entities** — Use UUID identifiers for application entities.
- **Auditing Foundations** — Reusable persistence/auditing support for tracking domain changes.
- **Validation** — Validate incoming API requests at the application boundary.
- **Centralized Exceptions** — Consistent error handling across REST APIs.
- **API Documentation** — OpenAPI/Swagger documentation for API exploration.
- **Monitoring** — Spring Boot Actuator for application health and operational visibility.

## Architecture

```text
Client
  |
  v
REST Controllers
  |
  v
Services / Business Logic
  |
  v
Repositories
  |
  v
PostgreSQL
```

The backend follows a layered architecture with clear separation between controllers, services, repositories, DTOs, and domain entities.

## Technology Stack

| Layer | Technologies |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot 3.3, Spring Data JPA, Hibernate |
| APIs | REST, Jakarta Bean Validation |
| Database | PostgreSQL |
| Database Migrations | Flyway |
| Documentation | OpenAPI / Springdoc |
| Monitoring | Spring Boot Actuator |
| Build | Maven |
| Containerization | Docker |
| Testing | Spring Boot Test / JUnit |

## Repository Structure

```text
nexora/
├── backend/
│   └── nexora-api/
│       ├── src/
│       ├── Dockerfile
│       ├── docker-compose.yml
│       └── pom.xml
├── frontend/
├── docs/
├── .gitignore
└── README.md
```

## Backend Module

The primary backend module is located at `backend/nexora-api` and uses Java 21 with Spring Boot 3.3. The Maven configuration includes Spring Web, Spring Data JPA, PostgreSQL, Flyway, validation, Actuator, Springdoc OpenAPI, Lombok, and Spring Boot Test.

## Local Development

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL
- Docker

### Run with Maven

```bash
cd backend/nexora-api
./mvnw test
./mvnw spring-boot:run
```

On Windows, use the Maven wrapper command available in the project or run the equivalent Maven commands from the module directory.

### Configuration

Configure the PostgreSQL connection through environment variables or the project's application configuration. Keep credentials and other secrets outside version control.

## Engineering Practices

Nexora is structured around backend engineering practices that make the codebase easier to extend:

- Layered architecture
- DTO-based API boundaries
- Repository abstraction with Spring Data JPA
- Database migrations with Flyway
- Request validation
- Centralized exception handling
- API documentation
- Application health monitoring
- Docker-based local infrastructure

## Project Documentation

Additional product and architecture documentation is maintained under `docs/`.

- [Product Vision](docs/PRODUCT_VISION.md)
- [Project Roadmap](docs/ROADMAP.md)
- [Architecture Overview](docs/ARCHITECTURE_OVERVIEW.md)

## Status

The repository currently contains the backend foundation and project structure for the Nexora platform. Features are being developed incrementally toward a broader engineering-readiness workflow.

## Author

**Muskan Varshney**  
[GitHub](https://github.com/muskanv26) • [LinkedIn](https://www.linkedin.com/in/muskan-varshney-b2a9a5335/)