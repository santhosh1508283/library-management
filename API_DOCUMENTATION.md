# 📖 API Documentation

# Base URL

```text
http://localhost:8080/api/v1
```

---

# Authentication

JWT Token is required for all protected endpoints.

Header

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# Roles

| Role      | Description                                              |
| --------- | -------------------------------------------------------- |
| MEMBER    | Can borrow books, return books, and view their own loans |
| LIBRARIAN | Manages books, book copies, and issues/accepts returns   |
| ADMIN     | Full system access                                       |

---

# HTTP Status Codes

| Code | Meaning               |
| ---- | --------------------- |
| 200  | Success               |
| 201  | Resource Created      |
| 204  | Success (No Content)  |
| 400  | Bad Request           |
| 401  | Unauthorized          |
| 403  | Forbidden             |
| 404  | Resource Not Found    |
| 409  | Conflict              |
| 500  | Internal Server Error |

---

# Authentication APIs

## Login

**POST**

```http
/api/v1/auth/login
```

Request

```json
{
  "email": "john@example.com",
  "password": "password"
}
```

Response

```json
{
  "token": "<JWT_TOKEN>"
}
```

---

## Register

**POST**

```http
/api/v1/auth/register
```

Request

```json
{
  "name": "John",
  "email": "john@example.com",
  "password": "password"
}
```

---

# Book APIs

---

## Create Book

**POST**

```http
/api/v1/books
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "1st",
  "isbn": "9780132350884"
}
```

Response

```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "1st",
  "isbn": "9780132350884"
}
```

---

## Get All Books

**GET**

```http
/api/v1/books
```

Authorization

```text
Authenticated User
```

Response

```json
[
  {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "edition": "1st",
    "isbn": "9780132350884"
  }
]
```

---

## Get Book By Id

**GET**

```http
/api/v1/books/{bookId}
```

Authorization

```text
Authenticated User
```

---

## Update Book

**PUT**

```http
/api/v1/books/{bookId}
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "2nd",
  "isbn": "9780132350884"
}
```

---

## Delete Book (Soft Delete)

**DELETE**

```http
/api/v1/books/{bookId}
```

Authorization

```text
LIBRARIAN
ADMIN
```

Response

```http
204 No Content
```

---

# Book Copy APIs

---

## Create Book Copy

**POST**

```http
/api/v1/books/{bookId}/copies
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "barcode": "BC000001",
  "shelfNumber": "A-12"
}
```

---

## Get Copies of a Book

**GET**

```http
/api/v1/books/{bookId}/copies
```

Authorization

```text
LIBRARIAN
ADMIN
```

---

## Get Book Copy

**GET**

```http
/api/v1/book-copies/{id}
```

Authorization

```text
LIBRARIAN
ADMIN
```

---

## Update Shelf Number

**PATCH**

```http
/api/v1/book-copies/{id}/shelf
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "shelfNumber": "B-21"
}
```

Response

```http
204 No Content
```

---

## Update Book Copy Status

**PATCH**

```http
/api/v1/book-copies/{id}/status
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "status": "LOST"
}
```

Response

```http
204 No Content
```

---

## Delete Book Copy

Logical delete by changing status to LOST.

**DELETE**

```http
/api/v1/book-copies/{id}
```

Authorization

```text
LIBRARIAN
ADMIN
```

Response

```http
204 No Content
```

---

# Loan APIs

---

## Borrow Book

**POST**

```http
/api/v1/loans
```

Authorization

```text
MEMBER
```

Request

```json
{
  "bookId": 1
}
```

Business Flow

* Validate Book
* Find first AVAILABLE Book Copy
* Create Loan
* Mark Book Copy as BORROWED

Response

```json
{
  "loanId": 10,
  "bookId": 1,
  "bookTitle": "Clean Code",
  "copyId": 4,
  "barcode": "BC000001",
  "borrowedAt": "2026-08-03T12:00:00",
  "dueDate": "2026-08-18T12:00:00",
  "status": "BORROWED"
}
```

---

## Return Book

**POST**

```http
/api/v1/loans/return
```

Authorization

```text
LIBRARIAN
ADMIN
```

Request

```json
{
  "barcode": "BC000001"
}
```

Business Flow

* Find Book Copy using barcode
* Find active loan
* Mark Loan as RETURNED
* Set returnedAt
* Mark Book Copy AVAILABLE

Response

```http
204 No Content
```

---

## Get Active Loans

**GET**

```http
/api/v1/loans/me
```

Authorization

```text
MEMBER
```

Response

```json
[
  {
    "loanId": 12,
    "bookId": 5,
    "title": "Effective Java",
    "barcode": "BC000045",
    "borrowedAt": "2026-08-01T10:30:00",
    "dueDate": "2026-08-16T10:30:00"
  }
]
```

---

## Loan History

**GET**

```http
/api/v1/loans/history
```

Authorization

```text
MEMBER
```

Response

```json
[
  {
    "loanId": 15,
    "bookId": 2,
    "title": "Clean Code",
    "barcode": "BC000012",
    "borrowedAt": "2026-07-01T09:00:00",
    "dueDate": "2026-07-16T09:00:00",
    "returnedAt": "2026-07-10T17:00:00",
    "status": "RETURNED"
  },
  {
    "loanId": 18,
    "bookId": 5,
    "title": "Effective Java",
    "barcode": "BC000045",
    "borrowedAt": "2026-08-01T10:30:00",
    "dueDate": "2026-08-16T10:30:00",
    "returnedAt": null,
    "status": "BORROWED"
  }
]
```

---

# Common Error Response

```json
{
  "timestamp": "2026-08-03T10:15:20",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found"
}
```

---

# Business Rules

## Borrow

* Only MEMBER can borrow books.
* Only AVAILABLE book copies can be borrowed.
* Borrowing creates a Loan.
* Borrowing changes Book Copy status to BORROWED.
* Borrow duration is 15 days.

---

## Return

* Only LIBRARIAN and ADMIN can process returns.
* Barcode uniquely identifies the physical book copy.
* Returning updates Loan status to RETURNED.
* Returning changes Book Copy status to AVAILABLE.

---

# Waitlist APIs

## 1. Join Waitlist

Allows a member to join the waitlist for a book when no available copies exist.

### Endpoint

```http
POST /api/v1/waitlist
```

### Authorization

```text
MEMBER
```

### Request Body

```json
{
  "bookId": 123
}
```

### Response

```http
204 No Content
```

### Business Rules

* The authenticated user is obtained from the JWT/security context.
* The user cannot join the same book's waitlist more than once while the existing entry is `WAITING`.
* Deleted books cannot be added to the waitlist.
* A previous `CANCELLED`, `EXPIRED`, or `FULFILLED` entry does not prevent the user from joining again.
* A successful request creates a new waitlist entry with status `WAITING`.

### Possible Errors

| Status | Error                   | Description                             |
| ------ | ----------------------- | --------------------------------------- |
| `404`  | Book Not Found          | Book does not exist or has been deleted |
| `409`  | Waitlist Already Exists | User is already waiting for the book    |

---

## 2. Get My Waitlist

Returns all waitlist entries belonging to the currently authenticated member.

### Endpoint

```http
GET /api/v1/waitlist/me
```

### Authorization

```text
MEMBER
```

### Request

No request body or query parameters are required.

The authenticated user is determined from the JWT/security context.

### Response

```http
200 OK
```

```json
[
  {
    "waitlistId": 12,
    "bookId": 5,
    "title": "Clean Code",
    "status": "WAITING",
    "joinedAt": "2026-08-09T14:30:00"
  },
  {
    "waitlistId": 8,
    "bookId": 2,
    "title": "Effective Java",
    "status": "FULFILLED",
    "joinedAt": "2026-08-07T10:15:00"
  },
  {
    "waitlistId": 4,
    "bookId": 9,
    "title": "Design Patterns",
    "status": "CANCELLED",
    "joinedAt": "2026-08-01T09:20:00"
  }
]
```

### Ordering

Entries are returned with the **most recently joined entries first**, based on `createdAt DESC`.

### Business Rules

* Only the authenticated user's waitlist entries are returned.
* All statuses are included:

    * `WAITING`
    * `NOTIFIED`
    * `FULFILLED`
    * `EXPIRED`
    * `CANCELLED`
* Pagination and filtering can be added later.

---

## 3. Cancel Waitlist

Allows a member to cancel their own active waitlist entry.

### Endpoint

```http
DELETE /api/v1/waitlist/{waitlistId}
```

### Authorization

```text
MEMBER
```

### Request

No request body is required.

The waitlist ID is provided as a path variable.

```http
DELETE /api/v1/waitlist/12
```

### Response

```http
204 No Content
```

### Business Rules

* A member can cancel only their own waitlist entry.
* The waitlist entry must currently have status `WAITING`.
* The entry is not physically deleted from the database.
* Instead, its status is changed to `CANCELLED`.
* This preserves waitlist history.

### Possible Errors

| Status | Error              | Description                                                                      |
| ------ | ------------------ | -------------------------------------------------------------------------------- |
| `404`  | Waitlist Not Found | Entry does not exist, does not belong to the user, or is not currently `WAITING` |

---

## Waitlist Status Lifecycle

```text
WAITING
   │
   ├──→ CANCELLED
   │
   ├──→ NOTIFIED
   │       │
   │       ├──→ FULFILLED
   │       │
   │       └──→ EXPIRED
   │
   └──→ FULFILLED
```

### Current V1 Scope

The current implementation supports:

* Joining a waitlist
* Viewing personal waitlist history
* Cancelling a waiting entry
* Maintaining waitlist status history

Future enhancements can include:

* Automatic notification when a copy becomes available
* Reservation windows
* Email/push notifications
* Automatic expiration
* Waitlist-to-loan conversion
* Pagination and filtering


# Future Enhancements

* Waitlist Management - v2
* Loan Renewal
* Fine Calculation
* Payment Integration
* Email Notifications
* Search APIs
* Pagination
* Admin Dashboard
* Swagger/OpenAPI
* Docker Support
* Unit & Integration Tests
