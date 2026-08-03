# 📚 Library Management System

A production-oriented **Library Management System** built using **Java**, **Spring Boot**, **Spring Security**, **JWT Authentication**, **Hibernate/JPA**, and **MySQL**.

The project is designed using real-world backend engineering practices rather than simple CRUD operations. It models how an actual library operates by separating **Books**, **Book Copies**, **Loans**, authentication, authorization, and future extensibility such as waitlists, payments, and notifications.

---

# 🚀 Features

## Authentication & Authorization

* JWT based authentication
* Spring Security
* Role Based Access Control (RBAC)
* Roles

    * MEMBER
    * LIBRARIAN
    * ADMIN

---

## Books

* Create Book
* Update Book
* Soft Delete Book
* Get Book By Id
* Get All Books

---

## Book Copies

Each physical copy of a book is managed independently.

Features

* Add Book Copy
* View Book Copies
* Update Shelf Number
* Update Status
* Mark Copy as Lost

Book Copy Status

* AVAILABLE
* BORROWED
* RESERVED
* LOST

---

## Loan Management

* Borrow Book
* Return Book
* View Active Loans
* View Loan History

Loan Status

* BORROWED
* RETURNED

---

# 🏗 Architecture

The project follows a layered architecture.

```text
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
MySQL
```

Each layer has a single responsibility.

* Controller → Handles HTTP requests.
* Service → Business logic.
* Repository → Database operations.
* Entity → Database mapping.
* DTO → Request/Response objects.
* Exception → Global exception handling.
* Security → JWT authentication and authorization.

---

# 🛠 Technology Stack

| Technology         | Purpose                        |
| ------------------ | ------------------------------ |
| Java 21            | Programming Language           |
| Spring Boot        | Backend Framework              |
| Spring Security    | Authentication & Authorization |
| JWT                | Stateless Authentication       |
| Hibernate / JPA    | ORM                            |
| MySQL              | Database                       |
| Maven              | Dependency Management          |
| Lombok             | Boilerplate Reduction          |
| Jakarta Validation | Request Validation             |

---

# 📂 Project Structure

```text
src
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── exception
 ├── security
 ├── config
 └── utils
```

---

# 🔐 Authentication

The application uses **JWT Authentication**.

Flow

```text
User Login
      │
      ▼
JWT Generated
      │
      ▼
JWT sent in Authorization Header
      │
      ▼
Spring Security validates token
      │
      ▼
Authenticated User available through SecurityContext
```

---

# 👥 Roles

## MEMBER

* View Books
* Borrow Books
* Return Books
* View Active Loans
* View Loan History

---

## LIBRARIAN

Everything a MEMBER can do plus

* Add Books
* Update Books
* Delete Books
* Manage Book Copies

---

## ADMIN

Full access to the system.

---

# 📖 Business Workflow

## Borrow Book

```text
Member
    │
    ▼
Select Book
    │
    ▼
System finds first AVAILABLE copy
    │
    ▼
Loan Created
    │
    ▼
Book Copy marked BORROWED
```

---

## Return Book

```text
Member
    │
Returns Physical Book
    │
    ▼
Librarian scans barcode
    │
    ▼
Active Loan located
    │
    ▼
Loan marked RETURNED
    │
    ▼
Book Copy becomes AVAILABLE
```

---

# 🗄 Database Design

Core Entities

* User
* Book
* BookCopy
* Loan

Relationships

```text
Book
 │
 ├──────────────< BookCopy

User
 │
 ├──────────────< Loan

BookCopy
 │
 ├──────────────< Loan
```

---

# ✅ Current Features Completed

* JWT Authentication
* Role Based Authorization
* Book Management
* Book Copy Management
* Borrow Book
* Return Book
* Active Loans
* Loan History
* Soft Delete for Books
* Global Exception Handling
* Request Validation

---

# 🚧 Planned Features

* Waitlist
* Fine Calculation
* Payment Module
* Loan Renewal
* Search APIs
* Pagination
* Notifications
* Admin Dashboard
* Email Integration
* Audit Logging
* Docker
* Flyway Migration
* Unit Tests
* Integration Tests
* Swagger / OpenAPI

---

# ▶ Running the Project

### Clone Repository

```bash
git clone <repository-url>
```

### Configure Database

Update:

```properties
application.properties
```

with your MySQL configuration.

### Run

```bash
mvn spring-boot:run
```

---

# 📸 Screenshots

Screenshots and API examples will be added as the project evolves.

---

# 📚 Learning Objectives

This project is built to practice production-grade backend development concepts including:

* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate/JPA
* REST API Design
* Transaction Management
* Exception Handling
* RBAC
* Database Modeling
* Clean Architecture
* Real-world Business Logic

---

# 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

Feel free to open an issue or submit a pull request.

---

# ⭐ Support

If you found this project helpful, consider giving it a ⭐ on GitHub.
