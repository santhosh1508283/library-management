# Technical Debt & Future Improvements

This document tracks improvements intentionally postponed while building the Library Management System. The goal is to finish the core business logic first and then revisit these items one by one.

---

# Authentication Improvements

## 1. Hash Refresh Tokens

**Current**

* Refresh tokens are stored in plain text.

**Future**

* Store BCrypt-hashed refresh tokens, similar to passwords.

**Benefit**

* Prevents token misuse if the database is compromised.

---

## 2. Refresh Token Rotation

**Current**

* Same refresh token is reused until expiry.

**Future**

* Validate current refresh token.
* Generate new refresh token.
* Revoke old refresh token.
* Return new access token + new refresh token.

---

## 3. Logout From All Devices

**Current**

* Logout revokes only one refresh token.

**Future**

* Revoke every active refresh token belonging to the user.

---

## 4. Device Detection

**Current**

* Device ID is hardcoded as `"web"`.

**Future**

* Store:

    * Browser
    * Operating System
    * Device Name
    * User-Agent

---

## 5. Session Management

Allow users to:

* View active sessions.
* Logout a specific device.
* Logout all devices.

---

## 6. Store IP Address

Track:

* Login IP
* Last Used IP
* Country (optional)

Useful for security and suspicious login detection.

---

## 7. Login History

Maintain:

* Login Time
* Logout Time
* Device
* IP
* Status

---

## 8. JWT Claims

Current claim:

* email

Future claims:

* userId
* role
* permissions
* tokenVersion

---

## 9. Token Version

Support invalidating every JWT after:

* Password change
* Account compromise

---

## 10. Scheduled Cleanup

Create a scheduled job to remove:

* Expired refresh tokens
* Revoked tokens older than X days

---

## 11. Refresh Token Status

Current:

* boolean revoked

Future:

* ACTIVE
* REVOKED
* EXPIRED

---

## 12. Refresh Token Table Improvements

Current Fields:

* id
* user_id
* refresh_token
* revoked
* device_id
* expires_at
* created_at
* last_used_at

Future Fields:

* hashed_refresh_token
* browser
* operating_system
* ip_address
* status

---

## 13. Replace @ManyToOne With userId (Optional)

Current:

* RefreshToken -> User relationship

Future:

* Store only userId for higher-performance services if needed.

---

## 14. Repository Queries

Potential additions:

* findByUser(...)
* findByUserAndRevokedFalse(...)
* deleteByExpiresAtBefore(...)
* countByUserAndRevokedFalse(...)

---

## 15. Better Exception Hierarchy

Current:

* InvalidCredentialsException

Future:

* InvalidTokenException
* TokenExpiredException
* RefreshTokenRevokedException
* AccessTokenExpiredException

---

## 16. Access Token Blacklist

Only if immediate JWT invalidation is required.
Likely implemented using Redis.

---

## 17. Redis

Possible use cases:

* Refresh Tokens
* OTP
* Rate Limiting
* Blacklist
* Session Cache

---

## 18. Rate Limiting

Limit:

* Login attempts
* OTP requests
* Refresh requests

---

## 19. Email Verification

Signup → Email Verification → Login

---

## 20. Forgot Password

Flow:

* Email
* OTP
* Reset Password
* Revoke all refresh tokens

---

## 21. Password Change

On password change:

* Revoke every refresh token
  or
* Increment token version

---

## 22. Spring Security Authorities

Current:

* Collections.emptyList()

Future:

* ROLE_MEMBER
* ROLE_LIBRARIAN
* ROLE_ADMIN

---

## 23. Method Security

Use:

* @PreAuthorize
* @PostAuthorize

instead of manual authorization checks.

---

## 24. OAuth2 Login

Future support:

* Google
* GitHub
* Microsoft

---

## 25. Testing

Add:

* Unit Tests
* Integration Tests
* MockMvc Tests
* Security Tests

---

# Common Project Improvements

## 1. Auditing Base Entity

Currently every entity contains fields like:

* createdAt
* updatedAt

Future:
Create a reusable base entity.

Example:

* BaseEntity

    * createdAt
    * updatedAt

Then:

* User extends BaseEntity
* Book extends BaseEntity
* Loan extends BaseEntity
* RefreshToken extends BaseEntity

Benefit:

* Removes duplicated fields from every entity.
* Keeps auditing consistent.

---

## 2. Enable Spring Data JPA Auditing

Instead of manually using:

* @CreationTimestamp
* @UpdateTimestamp

Evaluate using:

* @CreatedDate
* @LastModifiedDate
* @EnableJpaAuditing

---

## 3. Better Global Exception Handling

Current:

* Basic ErrorResponse

Future:

* Standard API error format
* Error codes
* Validation error aggregation
* Correlation/Trace IDs
* Consistent logging

Possible response:

{
"timestamp": "...",
"status": 401,
"error": "Unauthorized",
"code": "AUTH-001",
"message": "Access token expired",
"path": "...",
"traceId": "..."
}

---

## 4. Logging

Add structured logging for:

* Authentication
* Borrowing
* Returning
* Reservation
* Errors

Avoid logging passwords or JWTs.

---

## 5. Configuration Cleanup

Move:

* JWT secret
* Token expiry
* Device defaults
* CORS
* Security properties

into configuration classes.

---

## 6. Constants

Replace magic strings like:

* "web"

with constants or enums.

---

## 7. DTO Mapping

Consider introducing a mapper (manual or MapStruct) when DTO/entity conversions grow.

---

## 8. Package Structure

Review package organization once the project grows to keep responsibilities clear.

---

# Business Features (After Authentication)

* Book Management
* Book Copies
* Borrow Book
* Return Book
* Reservation / Waitlist
* Fine Calculation
* Notifications
* Admin APIs
* Concurrency Handling
* Transactions Around Borrow/Return

---

# Guiding Principle

Finish the complete Library Management System first.

After the project is functionally complete, revisit this document and implement improvements one by one instead of over-engineering early.

Once the core features are working, we'll do one dedicated cleanup sprint:

Decide soft delete behavior for Book

Option 1: Deleted books still reserve ISBN (current behavior).
Option 2: Allow re-adding same ISBN using existsByIsbnAndDeletedFalse(...).
Option 3 (preferred production approach): If ISBN exists and the book is soft-deleted, restore the existing record instead of creating a new one.

✅ Generic ApiResponse<T>
✅ Global auditing (createdAt, updatedAt) via a base entity
✅ Global soft delete support
✅ Refresh token hashing (BCrypt)
✅ Device-aware refresh tokens
✅ Mapper layer (or MapStruct)
✅ Better exception hierarchy
✅ Pagination for book listing
✅ Search & filtering
✅ API documentation (Swagger/OpenAPI)
✅ Caching where appropriate
✅ Logging & monitoring improvements
