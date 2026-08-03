# Library Management System – API Documentation

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

Receive bookId
│
▼
Find Book
│
▼
Find one AVAILABLE BookCopy
│
├───────────────► No copy?
│                     │
│                     ▼
│              Add to waitlist (later)
│
▼
Get logged-in User
│
▼
Create Loan
│
▼
Update BookCopy status → BORROWED
│
▼
Return Loan details
