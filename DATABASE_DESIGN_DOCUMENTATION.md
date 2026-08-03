# 📚 Database Design Documentation

# Library Management System

---

# Overview

The Library Management System follows a normalized relational database design where each entity has a single responsibility.

Instead of directly borrowing a **Book**, users borrow a **Book Copy**. This mirrors how real-world libraries operate, where a title may have multiple physical copies.

The design focuses on:

* Data consistency
* Scalability
* Maintainability
* Real-world business workflows

---

# Entity Relationship Diagram (Logical)

```text
                    +----------------+
                    |     USERS      |
                    +----------------+
                    | id             |
                    | name           |
                    | email          |
                    | password       |
                    | role           |
                    +----------------+
                           |
                           | 1
                           |
                           | *
                    +----------------+
                    |     LOANS      |
                    +----------------+
                    | id             |
                    | borrowed_at    |
                    | due_date       |
                    | returned_at    |
                    | status         |
                    | user_id (FK)   |
                    | copy_id (FK)   |
                    +----------------+
                           ^
                           |
                           | *
                           |
                           | 1
                    +----------------+
                    |  BOOK_COPIES   |
                    +----------------+
                    | id             |
                    | barcode        |
                    | shelf_number   |
                    | status         |
                    | book_id (FK)   |
                    +----------------+
                           ^
                           |
                           | *
                           |
                           | 1
                    +----------------+
                    |     BOOKS      |
                    +----------------+
                    | id             |
                    | title          |
                    | author         |
                    | isbn           |
                    | edition        |
                    | deleted        |
                    +----------------+
```

---

# Entities

---

# User

Represents every authenticated person in the system.

## Fields

| Field     | Description                |
| --------- | -------------------------- |
| id        | Primary Key                |
| name      | Full Name                  |
| email     | Unique Email               |
| password  | Encrypted Password         |
| role      | MEMBER / LIBRARIAN / ADMIN |
| createdAt | Created Timestamp          |
| updatedAt | Updated Timestamp          |

---

## Relationships

```
User
  |
  | 1
  |
  | *
Loan
```

One user can borrow many books over time.

---

# Book

Represents a logical book title.

Example

```
Clean Code
```

This does **not** represent a physical copy.

---

## Fields

| Field     | Description           |
| --------- | --------------------- |
| id        | Primary Key           |
| title     | Book Title            |
| author    | Author Name           |
| isbn      | ISBN                  |
| edition   | Edition               |
| deleted   | Soft Delete Flag      |
| deletedAt | Soft Delete Timestamp |
| createdAt | Created Timestamp     |
| updatedAt | Updated Timestamp     |

---

## Relationships

```
Book
  |
  | 1
  |
  | *
BookCopy
```

One book may have multiple physical copies.

Example

```
Clean Code

Copy 1
Copy 2
Copy 3
Copy 4
```

---

# Book Copy

Represents one physical copy of a book.

Users always borrow a **Book Copy**, never the Book itself.

---

## Fields

| Field       | Description       |
| ----------- | ----------------- |
| id          | Primary Key       |
| barcode     | Unique Barcode    |
| shelfNumber | Shelf Location    |
| status      | Copy Status       |
| book_id     | FK -> Books       |
| createdAt   | Created Timestamp |
| updatedAt   | Updated Timestamp |

---

## Book Copy Status

```
AVAILABLE
BORROWED
RESERVED
LOST
```

---

## Relationships

```
Book
   |
   | 1
   |
   | *
BookCopy
```

```
BookCopy
     |
     | 1
     |
     | *
Loan
```

---

# Loan

Represents one borrowing transaction.

Each borrow creates one Loan.

Returning the book updates the same Loan.

---

## Fields

| Field        | Description         |
| ------------ | ------------------- |
| id           | Primary Key         |
| user_id      | FK -> User          |
| book_copy_id | FK -> Book Copy     |
| borrowedAt   | Borrow Timestamp    |
| dueDate      | Due Date            |
| returnedAt   | Return Timestamp    |
| status       | BORROWED / RETURNED |
| createdAt    | Created Timestamp   |
| updatedAt    | Updated Timestamp   |

---

## Loan Status

```
BORROWED

RETURNED
```

---

## Relationships

```
User
  |
  | 1
  |
  | *
Loan
```

```
BookCopy
     |
     | 1
     |
     | *
Loan
```

---

# Why Separate Book and Book Copy?

Suppose a library owns

```
Clean Code
```

It may own

```
Barcode 001

Barcode 002

Barcode 003

Barcode 004
```

If users borrowed Book directly, the system would never know

* Which copy is available
* Which copy is lost
* Which copy is damaged
* Shelf location

Separating Book and Book Copy solves this.

---

# Borrow Workflow

```
User

↓

Book

↓

Find AVAILABLE Book Copy

↓

Create Loan

↓

Book Copy Status

AVAILABLE

↓

BORROWED
```

---

# Return Workflow

```
Member returns book

↓

Librarian scans barcode

↓

Find Book Copy

↓

Find Active Loan

↓

Loan Status

BORROWED

↓

RETURNED

↓

Book Copy

BORROWED

↓

AVAILABLE
```

---

# Current Business Rules

## Books

* ISBN must be unique.
* Soft delete supported.
* Deleted books cannot be borrowed.

---

## Book Copies

* Barcode must be unique.
* Shelf number identifies physical location.
* Status controls borrow eligibility.

Only

```
AVAILABLE
```

copies can be borrowed.

---

## Loans

* Borrow duration = 15 days.
* One Loan represents one borrow.
* Returning updates existing Loan.
* Book Copy becomes AVAILABLE after return.

---

# Security Rules

## MEMBER

Can

* Borrow Book
* View Active Loans
* View Loan History

Cannot

* Add Books
* Modify Books
* Manage Copies

---

## LIBRARIAN

Can

* Add Books
* Update Books
* Delete Books
* Add Copies
* Update Shelf
* Update Copy Status
* Process Returns

---

## ADMIN

Full access.

---

# Future Database Enhancements

## Waitlist

```
WAITLIST

id

user_id

book_id

position

created_at
```

---

## Payments

```
PAYMENTS

id

loan_id

amount

status

paid_at
```

---

## Notifications

```
NOTIFICATIONS

id

user_id

type

message

status
```

---

## Audit Logs

```
AUDIT_LOGS

id

user_id

action

entity

entity_id

created_at
```

---

# Design Principles

This schema follows

* Normalization
* Referential Integrity
* Soft Deletes
* Business-driven Modeling
* Separation of Logical Book and Physical Book Copy
* Scalable Transaction History

The current design is intentionally extensible so features such as waitlists, renewals, fines, notifications, and payments can be added without major schema changes.
