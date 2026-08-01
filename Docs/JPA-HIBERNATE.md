# 📖 02 - JPA & Hibernate

> Project: **Library Management System**
>
> Goal:
>
> Understand how Hibernate works internally instead of treating it like magic.

---

# Before Learning JPA

In the previous chapter, we wrote everything manually.

```java
Connection connection = DriverManager.getConnection(...);

PreparedStatement statement =
        connection.prepareStatement(sql);

statement.setString(1, user.getName());

statement.executeUpdate();

statement.close();

connection.close();
```

Every time we wanted to insert a row we had to:

- Open Connection
- Create SQL
- Create Statement
- Set Values
- Execute
- Close Statement
- Close Connection

Imagine writing this for

```
Users
Books
Authors
Loans
Payments
Reservations
Fine
Notifications
```

It becomes repetitive.

There had to be a better way.

---

# Why JPA Exists

JPA stands for

```
Java Persistence API
```

Notice the last word.

```
API
```

JPA is **NOT** a framework.

JPA is **NOT** an implementation.

It is only a **Specification**.

Think about JDBC.

JDBC says

```
Every database driver must implement these methods.
```

Similarly,

JPA says

```
Every ORM Framework must implement these interfaces.
```

Examples:

```
EntityManager

EntityTransaction

Query

Persistence Context
```

JPA only defines

```
WHAT should happen.
```

It never says

```
HOW should happen.
```

---

# Example

Suppose JPA says

```java
entityManager.persist(user);
```

JPA does not know

- SQL
- MySQL
- JDBC

It only says

```
persist()

↓

Should save entity.
```

Who actually saves it?

Hibernate.

---

# Why Hibernate Exists

Hibernate is the implementation of JPA.

Think like this.

```
JPA

↓

Rules

↓

Hibernate

↓

Implementation
```

Hibernate knows

- SQL Generation
- Dirty Checking
- Cache
- Transactions
- JDBC
- Connection
- Database Dialects

Hibernate does all heavy lifting.

---

# Relationship

```
Application

↓

Spring Data JPA

↓

JPA

↓

Hibernate

↓

JDBC

↓

MySQL Driver

↓

MySQL
```

Every layer exists for one reason.

---

# Spring Data JPA

Spring developers noticed something.

Almost every project has methods like

```java
findById()

save()

delete()

findAll()
```

Every developer writes them again.

Spring said

```
We'll generate these automatically.
```

Spring Data JPA sits on top of JPA.

It simply calls EntityManager internally.

---

# So who actually inserts into DB?

Suppose we call

```java
userRepository.save(user);
```

Internally

```
save()

↓

EntityManager.persist()

↓

Hibernate

↓

JDBC

↓

MySQL
```

Eventually...

Everything still reaches JDBC.

---

# Entity

Suppose

```java
@Entity
public class User {

    @Id
    private Long id;

    private String name;

}
```

This is no longer a normal Java object.

Hibernate now understands

```
User

↓

users table
```

Every object

↓

One row

---

# Without @Entity

```java
User user = new User();
```

Hibernate says

```
I don't know this object.
```

---

# With @Entity

Hibernate says

```
This object belongs to database.
```

Now it can

```
Insert

Update

Delete

Find
```

---

# EntityManager

This is the heart of JPA.

Everything starts here.

```java
@PersistenceContext
private EntityManager entityManager;
```

EntityManager is responsible for

```
Persist

Find

Merge

Remove

Flush
```

Almost every Hibernate operation starts from EntityManager.

---

# Why EntityManager Exists

Suppose we call

```java
entityManager.persist(user);
```

Question.

Should Hibernate immediately execute SQL?

Not always.

Maybe later.

Maybe inside transaction.

Maybe rollback.

Someone has to manage all these entities.

That someone is EntityManager.

---

# EntityManager Responsibilities

```
Manage Entities

↓

Track Changes

↓

Maintain Persistence Context

↓

Call Hibernate

↓

Execute SQL
```

---

# Persistence Context

This is the biggest topic in Hibernate.

Persistence Context is simply

```
Collection of Managed Objects
```

Think of it like

```java
Map<PrimaryKey, Entity>
```

Conceptually.

Not actual implementation.

---

Initially

```
Persistence Context

(empty)
```

---

Suppose

```java
User user = new User();
```

Where is user?

```
Only JVM Heap.
```

Hibernate knows nothing.

---

Suppose

```java
entityManager.persist(user);
```

Now

```
Persistence Context

↓

User(id=null)
```

Hibernate starts managing this object.

---

# Managed Object

Once object enters Persistence Context

Hibernate continuously watches it.

Example

```java
user.setName("Santhosh");
```

Hibernate already knows.

Again

```java
user.setName("Rahul");
```

Hibernate knows.

Again

```java
user.setEmail("abc@gmail.com");
```

Hibernate knows.

Why?

Because object is managed.

---

# The Four Entity States

Every Entity always belongs to one state.

```
Transient

↓

Managed

↓

Detached

↓

Removed
```

---

# 1. Transient

Example

```java
User user = new User();
```

Only Java knows.

Hibernate doesn't.

Database doesn't.

Persistence Context doesn't.

```
JVM Heap

↓

User
```

Nothing else.

---

# 2. Managed

```java
entityManager.persist(user);
```

Now

```
Persistence Context

↓

User
```

Hibernate tracks every change.

```
user.setName()

↓

Tracked
```

```
user.setEmail()

↓

Tracked
```

```
user.setPassword()

↓

Tracked
```

---

# Important

Managed means

```
Hibernate owns this object.
```

Whatever changes you make

Hibernate notices automatically.

No save() needed.

---

# 3. Detached

Suppose transaction finishes.

Persistence Context is destroyed.

Object still exists.

```
User

↓

Still in Heap
```

But Hibernate stopped tracking it.

Now object is called

```
Detached
```

Changing detached object

```java
user.setName("ABC");
```

does nothing.

Because Hibernate is no longer watching it.

---

# 4. Removed

Suppose

```java
entityManager.remove(user);
```

Hibernate marks object

```
Removed
```

At Flush

```
DELETE
```

is generated.

Object may still exist in Java until transaction ends,

but Hibernate has scheduled it for deletion.

---

# Summary

```
new User()

↓

Transient

↓

persist()

↓

Managed

↓

Transaction Ends

↓

Detached

↓

remove()

↓

Removed
```

---

# Interview Questions

### Q1 Why was JPA created?

To provide a standard ORM specification independent of any implementation.

---

### Q2 Is JPA a framework?

No.

It is only a specification.

---

### Q3 Who implements JPA?

Hibernate.

(EclipseLink is another implementation.)

---

### Q4 Does JPA know JDBC?

No.

Hibernate talks to JDBC.

JPA only defines interfaces.

---

### Q5 What is EntityManager?

The main JPA interface responsible for managing entity lifecycle.

---

### Q6 What is Persistence Context?

A collection of managed entities currently tracked by Hibernate.

---

### Q7 Difference between Transient and Managed?

Transient:

Hibernate doesn't know object.

Managed:

Hibernate tracks every modification.

---

## Next Chapter

We'll learn

- persist()
- find()
- merge()
- remove()
- Dirty Checking
- Flush
- Why `persist()` with `IDENTITY` immediately executes an INSERT
- Why `merge()` returns another object
- First-Level Cache

# 📖 02 - JPA & Hibernate (Part 2)

---

# `persist()`

The first method everyone learns.

```java
User user = new User();

entityManager.persist(user);
```

Many beginners think:

```
persist()

↓

INSERT INTO database
```

❌ This is **not always true**.

The real flow is

```
persist()

↓

Persistence Context

↓

Managed Entity

↓

Flush

↓

INSERT
```

`persist()` **does not mean "insert into database".**

It means

> "Start managing this object."

---

# What actually happens?

Suppose

```java
User user = new User();

user.setName("Santhosh");
```

Current state

```
Transient
```

Hibernate doesn't know this object.

Now

```java
entityManager.persist(user);
```

Hibernate does

```
Persistence Context

↓

Add User Object
```

Now

```
Managed
```

No SQL is required to become managed.

Hibernate simply remembers the object.

---

# Then why did INSERT happen immediately?

Remember our project.

We had

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Why did Hibernate immediately print

```sql
insert into users ...
```

Because of

```
IDENTITY
```

---

# Why IDENTITY forces INSERT

Think about this.

Initially

```java
user.getId()
```

returns

```
null
```

But database generates the ID.

Example

```
Database

↓

101
```

Hibernate needs that value immediately.

The only way to know it is

```
INSERT

↓

Database generates ID

↓

Hibernate reads generated ID

↓

user.setId(101)
```

So with

```
IDENTITY
```

Hibernate executes INSERT immediately.

Not because persist() always inserts.

But because it needs the generated ID.

---

# If we used SEQUENCE

Suppose

```java
@GeneratedValue(strategy = GenerationType.SEQUENCE)
```

Hibernate can ask database

```
Give next sequence value.
```

Database returns

```
205
```

Now Hibernate already knows

```
id = 205
```

No INSERT needed yet.

Actual INSERT can wait until Flush.

This is why IDENTITY behaves differently.

---

# Important

```
persist()

↓

Managed
```

Not

```
persist()

↓

INSERT
```

INSERT timing depends on strategy and flushing.

---

# `find()`

Example

```java
User user =
entityManager.find(User.class,1L);
```

Many think

```
find()

↓

SELECT
```

Again...

Not always.

Hibernate first checks

```
Persistence Context
```

---

# Flow of find()

```
find(id)

↓

Persistence Context

↓

Found?

↓

YES

↓

Return Object
```

No SQL.

---

If not found

```
Persistence Context

↓

Not Found

↓

Database

↓

SELECT

↓

Store in Persistence Context

↓

Return Managed Object
```

---

# First Level Cache

Persistence Context is also called

```
First Level Cache
```

Suppose

```java
User u1 =
entityManager.find(User.class,1L);

User u2 =
entityManager.find(User.class,1L);
```

Question.

How many SQL queries?

Only

```
ONE
```

Why?

Second call

```
find()

↓

Persistence Context

↓

Already Present

↓

Return Same Object
```

No database call.

---

# Very Important

```java
u1 == u2
```

returns

```
true
```

Both variables point to the exact same object.

This is why

```
find()

↓

Persistence Context First
```

---

# `merge()`

This confused almost everyone.

Let's understand carefully.

Suppose transaction finished.

Persistence Context destroyed.

Object still exists.

```
Detached
```

Example

```java
user.setName("Rahul");
```

Hibernate doesn't care anymore.

Detached objects are not tracked.

---

Now

```java
entityManager.merge(user);
```

What happens?

Many think

```
Detached Object

↓

Managed
```

❌ Wrong.

Hibernate does NOT manage the same object.

---

# Actual Flow

```
merge()

↓

Check Persistence Context
```

If managed object exists

```
Copy values

↓

Return Managed Object
```

Detached object remains detached.

---

If managed object doesn't exist

Hibernate checks database.

```
Database

↓

Load Entity

↓

Managed

↓

Copy Values

↓

Return Managed Object
```

Again

Detached object never changes.

---

# Example

Detached

```
User A

↓

name = Rahul
```

Managed

```
User B

↓

name = Santhosh
```

Merge

↓

Copy

```
Rahul

↓

Managed Object
```

Now

Managed

```
name = Rahul
```

Returned object

```
User B
```

Detached object

```
Still Detached
```

---

# Why merge() returns object?

Because

Returned object is the managed one.

Always use

```java
user = entityManager.merge(user);
```

if you want managed instance.

---

# What if row doesn't exist?

Suppose

```
ID = 100
```

doesn't exist.

Hibernate will eventually insert a new row when the transaction is flushed.

The exact SQL depends on whether Hibernate determines the entity is new or existing based on its identifier and mapping strategy.

---

# `remove()`

Example

```java
entityManager.remove(user);
```

Requirements

Object must be

```
Managed
```

Hibernate marks it

```
Removed
```

Actual SQL

```sql
DELETE
```

is generated during Flush.

---

# What if Detached?

```java
entityManager.remove(detachedUser);
```

Hibernate throws

```
IllegalArgumentException
```

because detached objects are not managed.

Correct approach

```java
User user =
entityManager.find(User.class,id);

entityManager.remove(user);
```

---

# Dirty Checking

One of Hibernate's greatest features.

Suppose

```java
User user =
entityManager.find(User.class,1L);

user.setName("Rahul");
```

Where is

```
UPDATE
```

?

Nowhere.

You never called

```
save()
```

Still database updates.

Why?

Dirty Checking.

---

# How Dirty Checking Works

Initially

Hibernate remembers

```
Old Values

Name = Santhosh
```

After

```java
user.setName("Rahul");
```

Current values

```
Rahul
```

At Flush

Hibernate compares

```
Old

↓

Current

↓

Different

↓

Generate UPDATE
```

If nothing changed

```
No UPDATE
```

generated.

---

# Why save() isn't required

Because

Managed objects

↓

Dirty Checking

↓

Flush

↓

Automatic UPDATE

Hibernate is continuously watching managed entities.

---

# Summary

* `persist()` makes an entity **managed**. SQL execution depends on ID strategy and flush timing.
* `find()` checks the **Persistence Context first**, then the database if needed.
* The Persistence Context acts as Hibernate's **First-Level Cache**.
* `merge()` does **not** reattach the same object; it copies values into a managed entity and returns that managed instance.
* `remove()` requires a **managed entity** and schedules it for deletion.
* Dirty Checking automatically detects changes to managed entities and generates `UPDATE` statements during flush.


# 📖 02 - JPA & Hibernate (Part 3)

---

# Flush

One of the most misunderstood concepts.

Many developers think

```
Flush

↓

Commit
```

They are **not** the same.

---

# What is Flush?

Flush means

> Synchronize the Persistence Context with the database.

It does **NOT** mean the transaction is committed.

---

Suppose

```java
@Transactional
public void updateUser() {

    User user = entityManager.find(User.class, 1L);

    user.setName("Rahul");

}
```

Current state

```
Persistence Context

↓

Managed User

↓

Name = Rahul
```

Database

```
Still

Santhosh
```

Nothing has happened yet.

---

Now Flush starts.

Hibernate performs

```
Dirty Checking

↓

Generate SQL

↓

Borrow Connection

↓

Create PreparedStatement

↓

Execute SQL
```

Database now becomes

```
Rahul
```

But transaction is **still not committed**.

---

# What if transaction fails?

Suppose

```java
user.setName("Rahul");

entityManager.flush();

throw new RuntimeException();
```

Question.

Will database finally contain Rahul?

No.

Because

```
Flush

↓

SQL Executed

↓

Transaction Rollback

↓

Database Restored
```

Flush synchronizes.

Commit makes it permanent.

---

# Flush Lifecycle

```
Persistence Context

↓

Dirty Checking

↓

Generate SQL

↓

Borrow Connection

↓

PreparedStatement

↓

JDBC

↓

Database

↓

Transaction Still Open
```

---

# Commit

Commit means

```
Everything successful.

↓

Make changes permanent.
```

Transaction lifecycle

```
Transaction Starts

↓

Business Logic

↓

Flush

↓

Commit

↓

Persistence Context Destroyed
```

---

# Difference

| Flush                | Commit                        |
| -------------------- | ----------------------------- |
| Synchronizes changes | Permanently saves transaction |
| SQL may execute      | Transaction ends              |
| Can rollback later   | Cannot rollback after commit  |

---

# Transaction Lifecycle

Suppose

```java
@Transactional
public void createUser() {

}
```

Spring actually performs something similar to

```
Start Transaction

↓

Create Persistence Context

↓

Create EntityManager

↓

Execute Method

↓

Flush

↓

Commit

↓

Destroy Persistence Context

↓

Return Connection
```

Everything happens automatically.

---

# What is Persistence Context lifetime?

Persistence Context exists only for the transaction.

```
Transaction Starts

↓

Persistence Context Created

↓

Transaction Ends

↓

Destroyed
```

That is why

Managed

↓

Detached

after transaction finishes.

---

# Hibernate Internals

Suppose

```java
entityManager.persist(user);
```

Internally

```
persist()

↓

Persistence Context

↓

Managed Entity

↓

(No SQL yet)
```

Later

```
Flush()

↓

Dirty Checking

↓

Generate INSERT

↓

Borrow Connection

↓

PreparedStatement

↓

executeUpdate()

↓

Return Connection
```

Hibernate itself **never talks to MySQL directly**.

Hibernate still uses JDBC.

---

# Complete Internal Flow

```
entityManager.persist(user)

↓

Persistence Context

↓

Managed Entity

↓

Flush

↓

Hibernate SQL Generator

↓

Connection Pool

↓

Connection

↓

PreparedStatement

↓

JDBC Driver

↓

TCP/IP

↓

MySQL
```

Exactly the same JDBC code we wrote manually.

Hibernate simply hides it.

---

# Connection Pool

Earlier

We manually created

```java
DriverManager.getConnection(...)
```

Every request.

Very expensive.

Modern applications use

```
Connection Pool
```

Example

```
HikariCP
```

Pool

```
Connection 1

Connection 2

Connection 3

...

Connection 10
```

Suppose

100 users arrive.

Hibernate doesn't create 100 connections.

Instead

```
Borrow Connection

↓

Execute SQL

↓

Return Connection
```

Next request uses the same connection.

---

# Who creates Connection Pool?

Spring Boot.

During startup

Spring Boot creates

```
HikariDataSource

↓

Pool Created

↓

10 Connections
```

Hibernate simply borrows from it.

---

# EntityManager Proxy

One of the most important interview topics.

We injected

```java
@PersistenceContext
private EntityManager entityManager;
```

Question.

UserService is Singleton.

Then EntityManager should also be Singleton?

No.

Spring injects

```
EntityManager Proxy
```

Not actual EntityManager.

---

# Why Proxy?

Suppose

```
Request A

↓

Transaction A
```

Needs

```
EntityManager A
```

Another request

```
Request B

↓

Transaction B
```

Needs

```
EntityManager B
```

Both requests use same service object.

But different EntityManagers.

Proxy decides

```
Current Transaction?

↓

Return Correct EntityManager
```

This is why EntityManager is thread-safe.

---

# Singleton Service

Suppose

```
@Service

UserService
```

Spring creates

```
One Object
```

Only once.

Question.

Can 100 users use it?

Yes.

Because methods don't store execution state.

Threads do.

---

Suppose

```java
public void createUser(String name){

    User user = new User();

}
```

Thread A

```
Stack

↓

user
```

Thread B

```
Another Stack

↓

user
```

Different local variables.

No conflict.

---

# Dangerous Example

```java
private User currentUser;
```

Now

```
Heap

↓

currentUser
```

Shared.

Thread A

↓

Changes

Thread B

↓

Changes

Race Condition.

---

# Safe Rule

Singleton beans should be

```
Stateless
```

Store request data

* Method Parameters
* Local Variables
* Database

Never

```
Instance Fields
```

unless synchronized correctly.

---

# Complete Request Flow

```
HTTP Request

↓

DispatcherServlet

↓

Controller

↓

Service (Singleton)

↓

Repository

↓

Spring Data JPA

↓

EntityManager Proxy

↓

Real EntityManager

↓

Persistence Context

↓

Hibernate

↓

Connection Pool

↓

Connection

↓

PreparedStatement

↓

JDBC Driver

↓

TCP/IP

↓

MySQL
```

---

# Common Misconceptions

## ❌ persist() inserts immediately

Correct

```
persist()

↓

Managed

↓

Flush

↓

INSERT
```

IDENTITY is a special case.

---

## ❌ merge() makes detached object managed

Correct

```
Detached Object

↓

Copy Values

↓

Managed Object Returned
```

Original object remains detached.

---

## ❌ Dirty Checking happens at commit

Correct

Dirty Checking happens during

```
Flush
```

Commit only finalizes the transaction.

---

## ❌ EntityManager is Singleton

Correct

Injected object is

```
Proxy
```

Actual EntityManager exists per transaction.

---

## ❌ Hibernate replaces JDBC

Correct

Hibernate uses JDBC internally.

JDBC is always present.

---

# Interview Questions

## Why was Hibernate created?

To automate JDBC boilerplate and provide ORM capabilities.

---

## Does Hibernate use JDBC?

Yes.

Always.

---

## What is Persistence Context?

A collection of managed entities tracked during a transaction.

---

## Difference between Flush and Commit?

Flush synchronizes.

Commit permanently saves.

---

## Why is EntityManager injected as Proxy?

To provide a different real EntityManager for each transaction while allowing singleton services.

---

## Why are Singleton Services thread-safe?

Because business logic uses local variables.

Local variables belong to each thread's stack.

---

## Why does IDENTITY execute INSERT immediately?

Because Hibernate must obtain the generated primary key from the database.

---

## Difference between persist() and merge()

| persist                 | merge                     |
| ----------------------- | ------------------------- |
| New Entity              | Detached Entity           |
| Makes object managed    | Returns managed copy      |
| Original object managed | Original remains detached |

---

# Final Summary

```
Application

↓

Spring Data JPA

↓

EntityManager

↓

Persistence Context

↓

Hibernate

↓

Connection Pool

↓

JDBC

↓

Driver

↓

Database
```

Every layer has one responsibility:

* **Spring Data JPA** → Generates repositories.
* **JPA** → Defines the API.
* **EntityManager** → Manages entity lifecycle.
* **Persistence Context** → Tracks managed entities.
* **Hibernate** → Generates SQL and performs ORM.
* **Connection Pool** → Efficiently manages database connections.
* **JDBC** → Standard Java database API.
* **Driver** → Speaks the database protocol.
* **Database** → Stores the data.

Understanding this flow means you no longer treat Hibernate or Spring Data JPA as magic—you understand exactly what each layer contributes.
