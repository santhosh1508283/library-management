# 📖 API Documentation — Library Management System

## Base URL

```
http://localhost:8080/api/v1
```

## Authentication

Every endpoint except `/api/v1/auth/**` requires a JWT **access token** sent as a Bearer token:

```http
Authorization: Bearer <accessToken>
```

Access tokens expire after **15 minutes**. Use the refresh endpoint to get a new one without re-logging in. Refresh tokens expire after **7 days**.

## Roles

| Role | Description |
|---|---|
| `MEMBER` | Default role assigned on signup. Can browse the catalog, borrow books, view their own active loans/history, and manage their own waitlist entries. |
| `LIBRARIAN` | Manages the book catalog and physical inventory, and processes book returns. |
| `ADMIN` | Has all `LIBRARIAN` permissions, plus the ability to change any user's role. |

Each endpoint below lists exactly which role(s) may call it. "Authenticated" means any logged-in user regardless of role.

## HTTP Status Codes Used

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Resource Created |
| 204 | Success, No Content |
| 400 | Bad Request (validation failure) |
| 401 | Unauthorized (missing/invalid/expired token, bad credentials) |
| 403 | Forbidden (authenticated, but role not permitted) |
| 404 | Resource Not Found |
| 409 | Conflict (duplicate resource / state conflict) |
| 500 | Internal Server Error |

## Standard Error Response

```json
{
  "timestamp": "2026-08-03T10:15:20",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found"
}
```

Validation errors (HTTP 400) additionally include an `errors` map of field → message:

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

---

# 1. Auth APIs

Base path: `/api/v1/auth` — all endpoints in this group are **public** (no token required).

## 1.1 Sign Up

Creates a new user account with the default role `MEMBER`, and immediately logs them in (returns tokens).

```http
POST /api/v1/auth/signup
```

**Authorization:** Public

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `name` | string | required (not blank) |
| `email` | string | required, must be a valid email |
| `password` | string | required, minimum 6 characters |

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response — `201 Created`**

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "MEMBER"
}
```

**What happens:**
- Rejects the request if the email is already registered.
- Password is hashed with BCrypt before being stored.
- New user is always created with role `MEMBER`.

**Errors**

| Status | Case |
|---|---|
| `400` | Missing/invalid `name`, `email`, or `password` |
| `409` | Email already registered |

---

## 1.2 Login

```http
POST /api/v1/auth/login
```

**Authorization:** Public

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `email` | string | required, valid email |
| `password` | string | required, minimum 6 characters |

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response — `200 OK`**

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "MEMBER"
}
```

**Errors**

| Status | Case |
|---|---|
| `400` | Missing/invalid `email` or `password` format |
| `401` | Email not found, or password does not match |

---

## 1.3 Refresh Token

Issues a new access token for a still-valid, non-revoked refresh token.

```http
POST /api/v1/auth/refresh
```

**Authorization:** Public (requires a valid refresh token in the body)

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `refreshToken` | string | required |

```json
{
  "refreshToken": "<refreshToken>"
}
```

**Response — `200 OK`**

```json
{
  "accessToken": "<new-jwt>",
  "refreshToken": "<same-refreshToken>",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "MEMBER"
}
```

**What happens:** validates the refresh token exists, is not revoked, and has not expired; issues a new access token; updates the token's `lastUsedAt` timestamp. The same refresh token is returned (it is not rotated).

**Errors**

| Status | Case |
|---|---|
| `400` | Missing `refreshToken` |
| `401` | Refresh token not found, revoked, or expired/invalid |

---

## 1.4 Logout

Revokes a refresh token so it can no longer be used to obtain new access tokens.

```http
POST /api/v1/auth/logout
```

**Authorization:** Public (requires a valid refresh token in the body)

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `refreshToken` | string | required |

```json
{
  "refreshToken": "<refreshToken>"
}
```

**Response — `204 No Content`**

**Errors**

| Status | Case |
|---|---|
| `400` | Missing `refreshToken` |
| `401` | Refresh token not found, already revoked, or invalid |

---

# 2. Book APIs

Base path: `/api/v1/books`

## 2.1 Create Book

```http
POST /api/v1/books
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `title` | string | required (not blank) |
| `author` | string | required (not blank) |
| `edition` | string | required (not blank) |
| `isbn` | string | required (not blank) |

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "1st",
  "isbn": "9780132350884"
}
```

**Response — `201 Created`**

```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "1st",
  "isbn": "9780132350884"
}
```

**Errors**

| Status | Case |
|---|---|
| `400` | Missing required field(s) |
| `401` | Missing/invalid token |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `409` | A (non-deleted) book with the same details already exists |

---

## 2.2 Get All Books

```http
GET /api/v1/books
```

**Authorization:** Authenticated (any role)

**Response — `200 OK`**

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

Soft-deleted books are excluded from the result.

---

## 2.3 Get Book by ID

```http
GET /api/v1/books/{id}
```

**Authorization:** Authenticated (any role)

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book ID |

**Response — `200 OK`**

```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "1st",
  "isbn": "9780132350884"
}
```

**Errors**

| Status | Case |
|---|---|
| `404` | Book not found (or has been soft-deleted) |

---

## 2.4 Update Book

```http
PUT /api/v1/books/{id}
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book ID |

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `title` | string | required (not blank) |
| `author` | string | required (not blank) |
| `edition` | string | required (not blank) |
| `isbn` | string | required (not blank) |

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "edition": "2nd",
  "isbn": "9780132350884"
}
```

**Response — `200 OK`** — returns the updated `BookResponse` (same shape as Create Book).

**Errors**

| Status | Case |
|---|---|
| `400` | Missing required field(s) |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book not found |

---

## 2.5 Delete Book (Soft Delete)

```http
DELETE /api/v1/books/{id}
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book ID |

**Response — `204 No Content`**

**What happens:** the book is flagged as deleted (not removed from the database), so historical loan/waitlist records remain intact. Deleted books no longer appear in `GET /books`, `GET /books/{id}`, and cannot be borrowed or waitlisted.

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book not found |

---

# 3. Book Copy APIs

A **Book Copy** represents one physical, barcoded instance of a `Book` on a specific shelf.

## 3.1 Create Book Copy

```http
POST /api/v1/books/{bookId}/copies
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `bookId` | Long | ID of the parent book |

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `bookId` | Long | required |
| `barcode` | string | required (not blank), must be unique |
| `shelfNumber` | string | required (not blank) |

```json
{
  "bookId": 1,
  "barcode": "BC000001",
  "shelfNumber": "A-12"
}
```

**Response — `201 Created`**

```json
{
  "id": 4,
  "bookId": 1,
  "title": "Clean Code",
  "barcode": "BC000001",
  "shelfNumber": "A-12",
  "status": "AVAILABLE"
}
```

New copies are created with status `AVAILABLE`.

**Errors**

| Status | Case |
|---|---|
| `400` | Missing required field(s) |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Parent book not found |
| `409` | Barcode already exists |

---

## 3.2 Get Copies of a Book

```http
GET /api/v1/books/{bookId}/copies
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `bookId` | Long | ID of the parent book |

**Response — `200 OK`**

```json
[
  {
    "id": 4,
    "bookId": 1,
    "title": "Clean Code",
    "barcode": "BC000001",
    "shelfNumber": "A-12",
    "status": "AVAILABLE"
  }
]
```

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book not found |

---

## 3.3 Get Book Copy by ID

```http
GET /api/v1/book-copies/{id}
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book copy ID |

**Response — `200 OK`** — single `BookCopyResponse` (see shape above).

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book copy not found |

---

## 3.4 Update Book Copy Shelf Number

```http
PATCH /api/v1/book-copies/{id}/shelf
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book copy ID |

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `shelfNumber` | string | required (not blank) |

```json
{
  "shelfNumber": "B-21"
}
```

**Response — `204 No Content`**

**Errors**

| Status | Case |
|---|---|
| `400` | Missing `shelfNumber` |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book copy not found |

---

## 3.5 Update Book Copy Status

```http
PATCH /api/v1/book-copies/{id}/status
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book copy ID |

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `status` | enum | required. One of `AVAILABLE`, `BORROWED`, `LOST`, `DAMAGED` |

```json
{
  "status": "LOST"
}
```

**Response — `204 No Content`**

**Errors**

| Status | Case |
|---|---|
| `400` | Missing/invalid `status` |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book copy not found |

---

## 3.6 Delete Book Copy (Logical Delete)

Marks the copy as `LOST` rather than removing it, preserving loan history.

```http
DELETE /api/v1/book-copies/{id}
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `id` | Long | Book copy ID |

**Response — `204 No Content`**

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | Book copy not found |

---

# 4. Loan APIs

Base path: `/api/v1/loans`

## 4.1 Borrow a Book

```http
POST /api/v1/loans
```

**Authorization:** `MEMBER`

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `bookId` | Long | required |

```json
{
  "bookId": 1
}
```

**Business Flow**

1. Validate the book exists (and is not deleted).
2. Find the first `AVAILABLE` copy of that book.
3. Create a `Loan` for the current authenticated user, `borrowedAt = now`, `dueDate = now + 15 days`.
4. Mark that book copy as `BORROWED`.

**Response — `201 Created`**

```json
{
  "loanId": 10,
  "bookId": 1,
  "copyId": 4,
  "bookTitle": "Clean Code",
  "barcode": "BC000001",
  "borrowedAt": "2026-08-03T12:00:00",
  "dueDate": "2026-08-18T12:00:00",
  "status": "BORROWED"
}
```

**Errors**

| Status | Case |
|---|---|
| `400` | Missing `bookId` |
| `403` | Caller is not `MEMBER` |
| `404` | Book not found, or no `AVAILABLE` copy exists for the book |

> **Note:** There is currently no automatic waitlist enrollment when no copy is available — the request simply fails with `404`. Joining the waitlist is a separate, explicit call (see Waitlist APIs below).

---

## 4.2 Return a Book

```http
POST /api/v1/loans/return
```

**Authorization:** `LIBRARIAN`, `ADMIN`

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `barcode` | string | required (not blank) |

```json
{
  "barcode": "BC000001"
}
```

**Business Flow**

1. Find the book copy by barcode.
2. Find its active (`BORROWED`) loan.
3. Set `returnedAt = now` and mark the loan `RETURNED`.
4. Mark the book copy `AVAILABLE`.

**Response — `204 No Content`**

**Errors**

| Status | Case |
|---|---|
| `400` | Missing `barcode` |
| `403` | Caller is not `LIBRARIAN` or `ADMIN` |
| `404` | No book copy with that barcode, or no active loan exists for that copy |

---

## 4.3 Get My Active Loans

```http
GET /api/v1/loans/me
```

**Authorization:** `MEMBER`

Returns the calling member's currently `BORROWED` (not yet returned) loans.

**Response — `200 OK`**

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

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `MEMBER` |

---

## 4.4 Get My Loan History

```http
GET /api/v1/loans/history
```

**Authorization:** `MEMBER`

Returns **all** of the calling member's loans (borrowed and returned), most recent first.

**Response — `200 OK`**

```json
[
  {
    "loanId": 18,
    "bookId": 5,
    "title": "Effective Java",
    "barcode": "BC000045",
    "borrowedAt": "2026-08-01T10:30:00",
    "dueDate": "2026-08-16T10:30:00",
    "returnedAt": null,
    "status": "BORROWED"
  },
  {
    "loanId": 15,
    "bookId": 2,
    "title": "Clean Code",
    "barcode": "BC000012",
    "borrowedAt": "2026-07-01T09:00:00",
    "dueDate": "2026-07-16T09:00:00",
    "returnedAt": "2026-07-10T17:00:00",
    "status": "RETURNED"
  }
]
```

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `MEMBER` |

---

# 5. Waitlist APIs

Base path: `/api/v1/waitlist`

## 5.1 Join Waitlist

Lets a member join the waitlist for a book (typically used when no copies are currently available).

```http
POST /api/v1/waitlist
```

**Authorization:** `MEMBER`

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `bookId` | Long | required |

```json
{
  "bookId": 123
}
```

**Response — `204 No Content`**

**Business Rules**

- The authenticated user is taken from the JWT/security context.
- A user cannot join the same book's waitlist twice while an existing entry is `WAITING`.
- Deleted books cannot be waitlisted.
- A prior `CANCELLED`, `EXPIRED`, or `FULFILLED` entry does **not** block re-joining.
- A successful call creates a new entry with status `WAITING`.

**Errors**

| Status | Error | Description |
|---|---|---|
| `403` | Forbidden | Caller is not `MEMBER` |
| `404` | Book Not Found | Book does not exist or has been deleted |
| `409` | Waitlist Already Exists | User already has a `WAITING` entry for this book |

---

## 5.2 Get My Waitlist

Returns all waitlist entries belonging to the currently authenticated member, across all statuses.

```http
GET /api/v1/waitlist/me
```

**Authorization:** `MEMBER`

**Response — `200 OK`**

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
  }
]
```

**Ordering:** most recently joined first (`createdAt DESC`).

**Statuses returned:** `WAITING`, `NOTIFIED`, `FULFILLED`, `EXPIRED`, `CANCELLED`.

**Errors**

| Status | Case |
|---|---|
| `403` | Caller is not `MEMBER` |

---

## 5.3 Cancel Waitlist Entry

Lets a member cancel their own active (`WAITING`) waitlist entry.

```http
DELETE /api/v1/waitlist/{waitlistId}
```

**Authorization:** `MEMBER`

**Path Parameters**

| Param | Type | Description |
|---|---|---|
| `waitlistId` | Long | ID of the waitlist entry |

**Response — `204 No Content`**

**Business Rules**

- A member can cancel only their own entry.
- The entry must currently be `WAITING`.
- The entry is not physically deleted — its status is changed to `CANCELLED`, preserving history.

**Errors**

| Status | Error | Description |
|---|---|---|
| `403` | Forbidden | Caller is not `MEMBER` |
| `404` | Waitlist Not Found | Entry doesn't exist, doesn't belong to the caller, or is not currently `WAITING` |

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

`NOTIFIED`, `FULFILLED`, and `EXPIRED` are modeled as valid statuses but nothing in the system currently transitions an entry into them automatically — that's part of the future waitlist automation work below.

---

# 6. Admin APIs

Base path: `/api/v1/admin`

## 6.1 Update User Role

Changes a user's role. Used to promote a `MEMBER` to `LIBRARIAN`, grant `ADMIN` access, etc.

```http
PATCH /api/v1/admin/users/role
```

**Authorization:** `ADMIN`

**Request Body**

| Field | Type | Rules |
|---|---|---|
| `email` | string | required, must be a valid email — identifies the target user |
| `role` | enum | required. One of `MEMBER`, `LIBRARIAN`, `ADMIN` |

```json
{
  "email": "john@example.com",
  "role": "LIBRARIAN"
}
```

**Response — `204 No Content`**

**What happens:** looks the user up by email and updates their role. If the user already has the requested role, the call is a no-op and still returns `204`.

**Errors**

| Status | Case |
|---|---|
| `400` | Missing/invalid `email` or `role` |
| `403` | Caller is not `ADMIN` |
| `404` | No user found with that email |

---

# Business Rules Summary

## Borrowing

- Only `MEMBER` can borrow books.
- Only `AVAILABLE` book copies can be borrowed; the system assigns the first available copy automatically (you cannot pick a specific copy).
- Borrowing creates a `Loan` and flips the assigned copy to `BORROWED`.
- Loan duration is fixed at **15 days** from the moment of borrowing.

## Returning

- Only `LIBRARIAN` and `ADMIN` can process returns.
- Returns are looked up by **barcode**, which uniquely identifies the physical copy.
- Returning sets the loan's `returnedAt`, flips its status to `RETURNED`, and flips the copy back to `AVAILABLE`.

## Deletion Semantics

- **Books** are soft-deleted (flagged, not removed) so loan/waitlist history stays valid.
- **Book copies** are "deleted" by setting their status to `LOST`, for the same reason.
- **Users** carry a `deleted` flag in the schema for the same soft-delete pattern, though no endpoint currently exposes user deletion.

---

# Future Enhancements

* Waitlist Management — v2
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
