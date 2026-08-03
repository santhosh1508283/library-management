Step 1: Define the borrow flow

Before writing any code, let's define the business rules.

User clicks "Borrow"

The system should:

Validate the user exists.
Validate the book exists.
Check if there is an AVAILABLE copy.
If an available copy exists:
Create a Loan record.
Mark the copy as BORROWED.
Set:
borrowedAt
dueAt (e.g., now + 14 days)
status = BORROWED
If no copy is available:
Add the user to the waitlist (we'll implement this later).
Step 2: Required entities

We already have:

✅ User
✅ Book
✅ BookCopy

Now we need the Loan entity.

Step 3: Loan entity

A loan represents one borrowing transaction.

Loan
----
id
user_id
book_copy_id
borrowed_at
due_at
returned_at
status
created_at
updated_at

Notice something important:

👉 Loan should reference BookCopy, not Book.

Why?

Because the user borrows a physical copy, not the abstract book.

Example:

Book
Harry Potter

Copies
BC001
BC002
BC003

If I borrow BC002, the system must know exactly which copy is with me.

Step 4: LoanStatus enum
public enum LoanStatus {
BORROWED,
RETURNED,
OVERDUE
}

No need to add LOST here.

If a borrowed copy is lost, that's handled through the BookCopy status and possibly a fine.

Step 5: Endpoint

We'll start with:

POST /api/v1/loans

Request:

{
"bookId": 1
}

Notice there is no userId.

The logged-in user is already available from Spring Security, so we should take the user from the JWT rather than trusting the client to send a user ID.

Before coding

The first thing we should do is create the Loan entity, because almost everything else depends on it.

We'll create:

Loan
LoanStatus
LoanRepository

and then implement the borrow service. That keeps the flow incremental and clean.