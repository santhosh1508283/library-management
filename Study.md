# Library Management System

## Overview

A backend application built using **Java, Spring Boot, Spring Data JPA, Hibernate, MySQL, and Redis** to simulate a real-world Library Management System.

The goal of this project is not only to build a working application but also to understand backend architecture, database design, authentication, transactions, concurrency, event-driven communication, caching, and scalable system design.

---[Readme.md](Readme.md)

# Actors

## 1. Member[Readme.md](Readme.md)

A library member can:

* Sign up
* Sign in
* Search books
* Borrow books
* Return books
* View borrowed books
* Join a waitlist when a book is unavailable

---

## 2. Librarian

A librarian can:

* Add books
* Add book copies
* Update book information
* Remove books
* Manage inventory
* View active loans

---

# Business Rules

* A member can borrow **at most 5 books** at a time.
* A member cannot borrow the same physical copy twice.
* A book may have multiple physical copies.
* If all copies are unavailable, the member can join the waitlist.
* Waitlist follows **First Come First Serve (FIFO)**.
* When a copy becomes available, the first member in the waitlist is notified.
* Users are soft-deleted for audit purposes.
* A user with active loans cannot be deleted.

---

# Domain Model

## User

Represents a member or librarian.

Fields:

* id
* name
* email
* password
* role
* deleted
* deletedAt
* created_at
* updated_at

---

## Book

Represents the logical book.

Example:

Clean Code

Fields:

* id
* title
* author
* isbn
* edition
* created_at
* updated_at

---

## BookCopy

Represents an individual physical copy.

Example:

Clean Code

Copy #1

Barcode: BC001

Shelf: A-12

Fields:

* id
* book_id
* barcode
* shelf_number
* status
* created_at
* updated_at

Status:

* AVAILABLE
* BORROWED
* LOST
* DAMAGED

---

## Loan

Represents one borrowing transaction.

Fields:

* id
* user_id
* book_copy_id
* borrowed_at
* due_date
* returned_at
* status
* payment_id (optional)
* created_at
* updated_at

Status:

* BORROWED
* RETURNED
* OVERDUE

---

## Waitlist

Represents users waiting for a book.

Fields:

* id
* user_id
* book_id
* created_at

FIFO ordering is based on created_at.

---

# Relationships

User

```
User (1)
      |
      | borrows
      |
Loan (N)
```

Book

```
Book (1)
      |
      | has
      |
BookCopy (N)
```

Book Copy

```
BookCopy (1)
      |
      | borrowed in
      |
Loan (N over time)
```

Only one active loan exists for a copy at any moment.

Waitlist

```
Book (1)
      |
      | waiting users
      |
Waitlist (N)

User (1)
      |
      | joins
      |
Waitlist (N)
```

---

# Overall ER Diagram

```
                 Book
                  |
             1 ------- N
                  |
              BookCopy
                  |
             1 ------- N
                  |
                 Loan
                /    \
               /      \
          N  /          \  N
            /            \
         User          Payment (future)

Book
 |
 | 1
 |
 N
Waitlist
 |
 N
 |
User
```

---

# Authentication (Future)

* JWT Access Token
* JWT Refresh Token
* BCrypt Password Encoding
* Redis for Refresh Token storage
* Role-based Authorization

Roles:

* MEMBER
* LIBRARIAN

---

# Borrow Flow

1. Member clicks Borrow.
2. Validate JWT.
3. Check member exists.
4. Check active loan count (< 5).
5. Find an available BookCopy.
6. Lock the selected BookCopy.
7. Create Loan.
8. Mark BookCopy as BORROWED.
9. Commit transaction.
10. Publish BookBorrowed event.
11. Notification service sends email asynchronously.

---

# Return Flow

1. Member returns a BookCopy.
2. Validate active Loan.
3. Mark Loan as RETURNED.
4. Mark BookCopy as AVAILABLE.
5. Commit transaction.
6. Publish BookReturned event.
7. Check waitlist.
8. Notify first waiting member.

---

# Planned Architecture

```
                Client

                   |

             REST Controller

                   |

             Service Layer

        ---------------------
        |                   |
 Borrow Service     Waitlist Service

                   |

            Repository Layer

                   |

               Hibernate

                   |

                  JDBC

                   |

            MySQL Driver

                   |

               MySQL Server
```

---

# Future Enhancements

* Redis Cache
* Email Notification Service
* Kafka/RabbitMQ
* Payment Module
* Fine Calculation
* Reservation Expiry
* Audit Logging
* Elasticsearch for Book Search
* Docker
* Kubernetes
* CI/CD
* Unit Testing
* Integration Testing
* Monitoring (Prometheus + Grafana)

---

# Tech Stack

Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Redis

Security

* Spring Security
* JWT
* BCrypt

Build

* Maven

Future Infrastructure

* Docker
* Kubernetes
* Kafka
* Prometheus
* Grafana

---

# Current Progress

* ✅ MySQL setup completed
* ✅ Spring Boot project created
* ✅ Database created
* ✅ Understood JDBC architecture
* ✅ Understood Hibernate architecture
* ✅ Understood Spring Data JPA
* ✅ Designed entities and relationships
* ⏳ Implementing entities
* ⏳ Authentication
* ⏳ Borrow flow
* ⏳ Waitlist
* ⏳ Notifications

com.santhosh.library
│
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── exception
├── security
├── config
├── util
└── enums (optional)
