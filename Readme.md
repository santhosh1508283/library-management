# 📚 Library Management System

A backend REST API for managing a library's books, physical book copies, member loans, and waitlists — built with **Spring Boot 4**, **Spring Security (JWT)**, and **Spring Data JPA (MySQL)**.

It supports three roles (`MEMBER`, `LIBRARIAN`, `ADMIN`), each with a distinct set of permissions, and models the real-world flow of a library: a **Book** (the title/catalog entry) can have many **Book Copies** (physical items with barcodes), which members **borrow** and **return** as **Loans**, and can **join a waitlist** for when no copies are available.

---

## ✨ Features

- **JWT-based authentication** — signup, login, short-lived access tokens + long-lived refresh tokens, and logout (refresh token revocation)
- **Role-based access control** — `MEMBER`, `LIBRARIAN`, `ADMIN`, enforced with Spring Security method security (`@PreAuthorize`)
- **Book catalog management** — create, update, list, fetch, and soft-delete books
- **Book copy management** — track individual physical copies (barcode, shelf location, status) per book
- **Loan lifecycle** — borrow, return, view active loans, and view loan history
- **Waitlist** — join, view, and cancel a waitlist entry when a book has no available copies
- **Admin controls** — promote/change a user's role
- **Centralized error handling** — consistent JSON error shape across the API, including field-level validation errors
- **Soft deletes** — books and users are never hard-deleted; they're flagged so history stays intact

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JJWT 0.12.7 (JWT) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Build Tool | Maven (with Maven Wrapper) |
| Boilerplate reduction | Lombok |

---

## 🗂️ Project Structure

```
library-management/
├── src/main/java/com/santhosh/library/
│   ├── controller/       # REST controllers (Auth, Book, BookCopy, Loan, Waitlist, Admin)
│   ├── service/          # Interfaces + implementations holding business logic
│   ├── repository/       # Spring Data JPA repositories
│   ├── entity/           # JPA entities and enums (Role, LoanStatus, etc.)
│   ├── dto/               # Request/response DTOs
│   ├── exception/        # Custom domain exceptions
│   ├── handler/           # @RestControllerAdvice global exception handler
│   ├── security/         # JwtService, JwtAuthenticationFilter
│   ├── config/            # SecurityConfig (filter chain, password encoder)
│   └── utils/              # SecurityUtils (current authenticated user helper)
├── src/main/resources/
│   └── application.properties
├── Docs/                  # Learning notes (JDBC, JPA/Hibernate, Spring IoC, AOP, Reflection)
├── API_DOCUMENTATION.md   # Full endpoint-by-endpoint API reference
└── pom.xml
```

---

## 👥 Roles & Permissions

| Role | Description |
|---|---|
| `MEMBER` | Default role on signup. Can browse books, borrow/return their own books (borrow only — return is done by staff), view their own active loans and loan history, and join/view/cancel their own waitlist entries. |
| `LIBRARIAN` | Manages the catalog and physical inventory: create/update/soft-delete books, create/update/delete book copies, and process book returns. |
| `ADMIN` | Everything a `LIBRARIAN` can do, plus user administration (changing a user's role). |

Every new user signs up as `MEMBER`. Role upgrades (e.g. to `LIBRARIAN` or `ADMIN`) are done via the admin **Update Role** endpoint — see [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md) for details.

---

## 🔑 Authentication Model

- On **signup** or **login**, the API returns an **access token** (short-lived, `15 min`) and a **refresh token** (long-lived, `7 days`).
- Send the access token on every protected request:
  ```http
  Authorization: Bearer <accessToken>
  ```
- When the access token expires, call `POST /api/v1/auth/refresh` with the refresh token to get a new access token without logging in again.
- `POST /api/v1/auth/logout` revokes a refresh token so it can no longer be used to mint new access tokens.
- All `/api/v1/auth/**` endpoints are public; every other endpoint requires a valid access token.

---

## ⚙️ Getting Started

### Prerequisites

- Java 17 or higher
- Maven (or use the bundled `./mvnw` wrapper)
- A running MySQL instance

### 1. Clone the repository

```bash
git clone <repository-url>
cd library-management
```

### 2. Configure environment variables

The app reads its datasource and JWT config from environment variables (see `src/main/resources/application.properties`):

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:mysql://localhost:3306/library_db` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret key used to sign JWTs |

Create a MySQL database matching your `DB_URL` (e.g. `library_db`) — the schema is auto-created/updated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

Example (Linux/macOS):

```bash
export DB_URL=jdbc:mysql://localhost:3306/library_db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-256-bit-secret
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will start on:

```
http://localhost:8080
```

### 4. Try it out

```bash
# Sign up
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","password":"password123"}'

# Use the returned accessToken on protected endpoints
curl http://localhost:8080/api/v1/books \
  -H "Authorization: Bearer <accessToken>"
```

---

## 📖 API Documentation

Full endpoint-by-endpoint documentation — including request/response bodies, required roles, and error cases — lives in **[`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md)**.

Quick summary of resource groups:

| Group | Base Path | Purpose |
|---|---|---|
| Auth | `/api/v1/auth` | Signup, login, refresh, logout |
| Books | `/api/v1/books` | Catalog CRUD |
| Book Copies | `/api/v1/books/{bookId}/copies`, `/api/v1/book-copies` | Physical inventory management |
| Loans | `/api/v1/loans` | Borrow, return, active loans, history |
| Waitlist | `/api/v1/waitlist` | Join/view/cancel waitlist for a book |
| Admin | `/api/v1/admin` | Role management |

---

## ❗ Error Handling

All errors return a consistent JSON shape:

```json
{
  "timestamp": "2026-08-03T10:15:20",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found"
}
```

Validation failures additionally include a field-level `errors` map:

```json
{
  "timestamp": "2026-08-03T10:15:20",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation Failed",
  "errors": {
    "email": "Invalid email",
    "password": "Password must be at least 6 characters"
  }
}
```

See [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md) for the full list of status codes and error cases per endpoint.

---

## 🚧 Future Scope

- Waitlist Management — v2 (auto-notify on availability, reservation windows, auto-expiration, waitlist-to-loan conversion)
- Loan Renewal
- Fine Calculation
- Payment Integration
- Email Notifications
- Search APIs
- Pagination
- Admin Dashboard
- Swagger / OpenAPI
- Docker Support
- Unit & Integration Tests

---

## 📄 License

Add your license of choice here.
