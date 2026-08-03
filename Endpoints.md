# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.0/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.0/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot # Library Management System – API Documentation

## Authentication APIs

### Sign Up

**POST** `/api/v1/auth/signup`

**Purpose**

* Register a new library member.
* Default role assigned: `MEMBER`.

---

### Login

**POST** `/api/v1/auth/login`

**Purpose**

* Authenticate user.
* Returns access token and refresh token.

---

### Refresh Token

**POST** `/api/v1/auth/refresh-token`

**Purpose**

* Generate a new access token using a valid refresh token.

---

### Logout

**POST** `/api/v1/auth/logout`

**Purpose**

* Revoke refresh token.
* User must login again after logout.

---

## Admin APIs

### Update User Role

**PATCH** `/api/v1/admin/users/role`

**Authorization**

* ADMIN only

**Purpose**

* Promote/demote users.
* Example:

    * MEMBER → LIBRARIAN
    * LIBRARIAN → ADMIN

---

# Book APIs

## Create Book

**POST** `/api/v1/books`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Add a new book to the library catalogue.

---

## Get All Books

**GET** `/api/v1/books`

**Purpose**

* Fetch all active books.
* Used by users while browsing library books.

---

## Get Book Details

**GET** `/api/v1/books/{bookId}`

**Purpose**

* Fetch details of a single book.

---

## Update Book

**PUT** `/api/v1/books/{bookId}`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Update book metadata.
* Example:

    * title
    * author
    * edition
    * ISBN

---

## Delete Book (Soft Delete)

**DELETE** `/api/v1/books/{bookId}`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Soft delete a book.
* Book is hidden from users.
* Physical copies remain in database.

---

# Book Copy APIs

A Book represents the title.

A BookCopy represents a physical copy.

Example:

Book:

* Clean Code

Copies:

* BC001
* BC002
* BC003

---

## Create Book Copy

**POST** `/api/v1/books/{bookId}/copies`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Add a physical copy of an existing book.

---

## Get All Copies of a Book

**GET** `/api/v1/books/{bookId}/copies`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* View all physical copies of a particular book.

---

## Get Book Copy Details

**GET** `/api/v1/book-copies/{id}`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Fetch details of a specific physical copy.

---

## Update Shelf Number

**PATCH** `/api/v1/book-copies/{id}/shelf`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Move a book copy to another shelf.

---

## Update Copy Status

**PATCH** `/api/v1/book-copies/{id}/status`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Update physical copy status.

Examples:

* AVAILABLE
* BORROWED
* LOST
* DAMAGED
* RESERVED

---

## Delete Book Copy

**DELETE** `/api/v1/book-copies/{id}`

**Authorization**

* ADMIN
* LIBRARIAN

**Purpose**

* Remove/decommission a physical copy.
* (Current implementation marks the copy as LOST; this will be revisited later.)

---

# Modules Completed

* ✅ MySQL setup
* ✅ Spring Boot setup
* ✅ JDBC architecture understanding
* ✅ Hibernate architecture understanding
* ✅ Spring Data JPA understanding
* ✅ Entity design
* ✅ Authentication
* ✅ Authorization using Spring Security
* ✅ Admin role management
* ✅ Book Management
* ✅ Book Copy Management

---

# Upcoming Modules

* ⏳ Loan (Borrow Book)
* ⏳ Return Book
* ⏳ Waitlist
* ⏳ Notifications
* ⏳ Fine Calculation
* ⏳ Dashboard & Reports

---

# Future Refactoring (Technical Debt)

* Create a common API response wrapper for all success responses.
* Introduce a global `BaseEntity` for:

    * id (optional)
    * createdAt
    * updatedAt
    * deleted
    * deletedAt
* Create mapper classes (or MapStruct) to replace repetitive DTO mapping.
* Centralize refresh-token validation logic.
* Revisit Book Copy deletion semantics (LOST vs REMOVED/DECOMMISSIONED).
* Filter inactive/deleted entities directly in repository methods where appropriate.
* Add audit logging for sensitive operations (role changes, status changes, deletions).
* Standardize repository query naming and custom queries.
* Improve global exception handling and error response consistency.
* Add pagination, sorting, and filtering to listing APIs.
* Add API documentation using OpenAPI/Swagger.
  DevTools](https://docs.spring.io/spring-boot/4.1.0/reference/using/devtools.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.




library-management/
│
├── README.md
│
├── docs/
│   ├── 01-JDBC.md
│   ├── 02-JPA-Hibernate.md
│   ├── 03-Spring-IoC-and-Beans.md
│   ├── 04-Spring-Data-JPA.md
│   ├── 05-Transactions.md
│   ├── 06-Annotations.md
│   ├── 07-Request-LifeCycle.md
│   └── 08-Interview-Questions.md
│
└── src/

HTTP Request
│
▼
DispatcherServlet
│
▼
Controller
│
▼
Service
│
▼
Repository
│
▼
EntityManager Proxy
│
▼
Persistence Context
│
▼
Hibernate
│
▼
HikariCP
│
▼
JDBC
│
▼
MySQL Driver
│
▼
MySQL

                 JVM

      ┌─────────────────────┐
      │        Heap         │
      │                     │
      │ UserService Bean    │
      │ UserRepository Bean │
      │ EntityManager Proxy │
      └─────────────────────┘

           ▲           ▲
           │           │
     Thread A      Thread B

      ┌────────┐   ┌────────┐
      │ Stack  │   │ Stack  │
      │ name   │   │ name   │
      │ user   │   │ user   │
      └────────┘   └────────┘

User
├── id
├── name
├── email
├── password
├── role
└── ...

Book
├── id
├── title
├── author
├── isbn
└── ...

BookCopy
├── id
├── book_id (FK)
├── barcode
├── shelf_no
├── status (AVAILABLE, BORROWED, RESERVED, LOST...)
├── created_at
└── updated_at

Loan
├── id
├── user_id (FK)
├── book_copy_id (FK)
├── borrowed_at
├── due_date
├── returned_at
├── status
└── payment_id (optional)

Reservation / Waitlist
├── id
├── user_id (FK)
├── book_id (FK)
├── created_at
└── priority/order

Fine
├── id
├── loan_id (FK)
├── amount
├── status
└── paid_at