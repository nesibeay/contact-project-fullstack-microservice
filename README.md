# 📒 Contact Service

A **Spring Boot** microservice for managing a corporate employee directory. Provides a secure REST API to create, search, and manage contact information with **role-based access control** via Keycloak.

---

## ✨ Features

- 📋 **Contact Directory** — List and search employees (name, department, job title)
- 🔍 **Full-text search** — Search across first name, last name, department, and job title
- 🔐 **Keycloak Integration** — JWT-based OAuth2 authentication; `ADMIN` role required for write operations
- 🙈 **Data Privacy** — Phone numbers are hidden from non-admin users automatically
- 🗑️ **Soft Delete** — Deleted contacts are never truly removed from the database
- 📄 **Pagination & Sorting** — All list endpoints are paginated
- 📖 **Swagger / OpenAPI** — Interactive API docs at `/swagger-ui.html`
- ✅ **Input Validation** — Phone number and email format validation on create

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + OAuth2 Resource Server (JWT) |
| Identity Provider | Keycloak (`turksat` realm) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |

---

## 📐 Project Structure

```
src/main/java/com/directory/contact/
├── config/
│   ├── SecurityConfig.java       # OAuth2 JWT + CORS + Method Security
│   └── WebConfig.java
├── controller/
│   └── ContactController.java    # REST API endpoints
├── service/
│   └── ContactService.java       # Business logic + phone/name sanitisation
├── repository/
│   └── ContactRepository.java    # JPA repository with custom search query
├── entity/
│   └── Contact.java              # JPA entity with soft-delete + auditing
├── mapper/
│   └── ContactMapper.java        # MapStruct mapper (entity ↔ DTO)
├── dto/
│   ├── ContactRequest.java       # Incoming payload with validation
│   ├── ContactResponse.java      # Outgoing payload
│   ├── ApiResponse.java          # Generic wrapper: { success, message, data, timestamp }
│   └── ErrorResponse.java
└── exception/
    ├── ContactNotFoundException.java
    ├── ContactAlreadyExistsException.java
    └── GlobalExceptionHandler.java
```

---

## 🔌 API Endpoints

Base path: `/api/v1/contacts`

| Method | Path | Auth Required | Role | Description |
|--------|------|:---:|:---:|-------------|
| `GET` | `/` | ✗ | — | List all contacts (paginated) |
| `GET` | `/search?query=` | ✗ | — | Search contacts |
| `POST` | `/` | ✓ | `ADMIN` | Create a new contact |
| `DELETE` | `/{id}` | ✓ | `ADMIN` | Soft-delete a contact |

> **Note:** GET endpoints are public. Phone numbers are masked for non-admin callers.

### Query Parameters (GET `/`)

| Param | Default | Description |
|-------|---------|-------------|
| `page` | `0` | Page index (0-based) |
| `size` | `10` | Page size |
| `sortBy` | `firstName` | Field to sort by |

---

## ⚙️ Setup & Configuration

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL running on `localhost:5432`
- Keycloak running on `localhost:8081` with a realm named `turksat`

### 1. Database

```sql
CREATE DATABASE directory_db;
```

### 2. Keycloak Setup

1. Start Keycloak and open the admin console
2. Create a new **realm** named `turksat`
3. Create a **client** (e.g. `contact-frontend`) with appropriate redirect URIs
4. Create roles: `ADMIN`, `USER`
5. Create users and assign roles as needed

### 3. Configuration

Copy the example config and fill in your values:

```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

Edit `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/directory_db
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/realms/turksat
```

> ⚠️ **Never commit `application.yaml` with real credentials.** The file is listed in `.gitignore`.

### 4. Run

```bash
./mvnw spring-boot:run
```

The service starts on **http://localhost:8080**.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## 🔒 Security Model

```
Public (no token needed)
  GET /api/v1/contacts          → returns contacts (phone hidden)
  GET /api/v1/contacts/search   → returns contacts (phone hidden)

Authenticated (any valid JWT)
  POST /api/v1/contacts         → requires ADMIN role
  DELETE /api/v1/contacts/{id}  → requires ADMIN role
```

JWT tokens are issued by Keycloak. The service validates them using the JWKS endpoint of the `turksat` realm. Roles are extracted from the `realm_access.roles` claim.

---

## 📦 Build

```bash
# Run tests
./mvnw test

# Package as JAR
./mvnw package -DskipTests

# Run the JAR
java -jar target/contact-service-0.0.1-SNAPSHOT.jar
```

---

## 📄 License

This project is for internal/educational use.
