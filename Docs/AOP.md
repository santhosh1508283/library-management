# Spring AOP (Aspect-Oriented Programming) - Part 1
*Library Management Project Notes*

---

# Table of Contents

1. Why AOP?
2. The Problem Without AOP
3. Cross-Cutting Concerns
4. Business Logic
5. What is AOP?
6. Core Terminologies
    - Aspect
    - Advice
    - Join Point
    - Pointcut
7. Real Library Management Example
8. AOP Flow
9. Summary

---

# Why was AOP introduced?

Imagine we are building our Library Management System.

```java
@Service
public class BookService {

    public void issueBook() {

        System.out.println("Issuing Book");

    }

}
```

Everything looks clean.

---

Manager comes and says

> "Log every service method."

Now we write

```java
public void issueBook() {

    System.out.println("Method Started");

    System.out.println("Issuing Book");

    System.out.println("Method Finished");

}
```

---

Next day

Manager says

> "Measure execution time."

Now

```java
public void issueBook() {

    long start = System.currentTimeMillis();

    System.out.println("Method Started");

    System.out.println("Issuing Book");

    System.out.println("Method Finished");

    long end = System.currentTimeMillis();

    System.out.println(end - start);

}
```

---

Next day

Manager says

> "Every database operation should be transactional."

Now

```java
public void issueBook() {

    beginTransaction();

    long start = System.currentTimeMillis();

    System.out.println("Method Started");

    System.out.println("Issuing Book");

    System.out.println("Method Finished");

    commit();

}
```

---

Next day

Manager says

> "Check permissions before issuing books."

Now

```java
public void issueBook() {

    checkPermission();

    beginTransaction();

    long start = System.currentTimeMillis();

    System.out.println("Method Started");

    System.out.println("Issuing Book");

    System.out.println("Method Finished");

    commit();

}
```

---

Our actual business logic is only

```java
System.out.println("Issuing Book");
```

Everything else is repeated code.

---

# The Problem

Imagine

```java
UserService
```

```java
BookService
```

```java
LoanService
```

```java
InventoryService
```

Every service contains

- Logging
- Transactions
- Security
- Performance Measurement

Again.

Again.

Again.

This leads to

- Duplicate Code
- Difficult Maintenance
- Hard to Read Business Logic

---

# Business Logic

Business Logic means

> The actual work your application performs.

Example

```java
issueBook();
```

```java
returnBook();
```

```java
createUser();
```

```java
calculateFine();
```

These are business requirements.

---

# Cross-Cutting Concerns

Cross-Cutting Concerns are features that appear in many different classes.

Examples

- Logging
- Transactions
- Security
- Caching
- Exception Handling
- Performance Monitoring

These are **not** business logic.

They support business logic.

---

Visual

```text
Business Logic

↓

Issue Book

↓

Return Book

↓

Create User
```

Cross-Cutting Concerns

```text
Logging

Transaction

Security

Caching

Performance
```

Notice

The same concern appears everywhere.

---

# What is AOP?

AOP stands for

**Aspect-Oriented Programming**

It separates

```text
Business Logic
```

from

```text
Cross-Cutting Concerns
```

Instead of writing

```java
public void issueBook(){

    log();

    transaction();

    businessLogic();

    commit();

}
```

We simply write

```java
@Transactional
public void issueBook(){

    businessLogic();

}
```

Spring automatically adds the transaction code.

---

# How does Spring do this?

You already know the answer.

Using

- Dynamic Proxy
- Reflection

The proxy executes extra logic before and after the real method.

---

Visual

```text
Client

↓

Proxy

↓

Business Method
```

becomes

```text
Client

↓

Proxy

↓

Before Logic

↓

Business Method

↓

After Logic

↓

Return
```

---

# Core AOP Terminologies

There are four important terms.

- Aspect
- Advice
- Join Point
- Pointcut

Let's understand them one by one.

---

# Join Point

Suppose

```java
@Service
public class BookService {

    public void addBook(){}

    public void deleteBook(){}

    public void issueBook(){}

}
```

Every method is a place where Spring *can* execute additional logic.

```text
BookService

↓

addBook()

↓

deleteBook()

↓

issueBook()
```

Each method execution is called a

**Join Point**

---

Definition

> A Join Point is a place where Spring AOP can intercept method execution.

In Spring AOP

Join Point = Method Execution

---

# Advice

Advice means

> Extra code executed before, after or around a method.

Example

```java
@Before(...)
public void logStart(){

    System.out.println("Method Started");

}
```

Another example

```java
@After(...)
public void logEnd(){

    System.out.println("Method Finished");

}
```

Business logic remains unchanged.

Advice contains supporting logic.

---

Examples of Advice

- Logging
- Transaction
- Security
- Cache
- Retry
- Performance Measurement

---

# Pointcut

Suppose our application contains

```text
UserService

BookService

LoanService

InventoryService
```

Should logging run on every method?

Maybe not.

Suppose we only want logging for

```text
BookService.*
```

or

```text
All Service Methods
```

The rule that selects which methods should receive Advice is called a

**Pointcut**

---

Visual

```text
All Join Points

↓

Pointcut

↓

Selected Methods

↓

Advice Executes
```

---

# Aspect

Aspect is simply a class that contains

- Pointcut
- Advice

Example

```java
@Aspect
@Component
public class LoggingAspect {

}
```

Inside this class we define

- Which methods to intercept
- What extra code to execute

Instead of adding logging to 100 classes,

we keep it in one place.

---

Visual

```text
Aspect

↓

Pointcut

↓

Advice
```

---

# Easy Memory Trick

| Term | Meaning |
|-------|----------|
| Join Point | A method that can be intercepted |
| Advice | Extra code |
| Pointcut | Rule selecting methods |
| Aspect | Class containing Pointcut + Advice |

---

# Library Management Example

Suppose

```java
issueBook()
```

```java
returnBook()
```

```java
addBook()
```

Logging Aspect

```text
Aspect

↓

Pointcut

↓

All methods inside Service package

↓

Advice

↓

Print

Method Started

↓

Execute Method

↓

Method Finished
```

Without modifying any service class.

---

# Business Logic vs Cross-Cutting Concerns

```text
Business Logic

↓

Issue Book
```

Cross-Cutting Concerns

```text
Logging

↓

Security

↓

Transaction

↓

Caching

↓

Performance
```

Business Logic focuses on solving the problem.

Cross-Cutting Concerns support the application.

---

# AOP Execution Flow

```text
Client

↓

Proxy

↓

Advice

↓

Business Method

↓

Advice

↓

Return
```

The proxy executes Advice.

The original class remains unchanged.

---

# Summary

We learned

- Why AOP was introduced.
- What problem it solves.
- Difference between Business Logic and Cross-Cutting Concerns.
- Join Point.
- Advice.
- Pointcut.
- Aspect.
- Basic execution flow.

---

# Next Part

In Part 2 we will learn

- Dynamic Proxy Review
- Spring AOP Architecture
- Proxy Creation
- Interceptors
- Interceptor Chain
- Multiple Annotations
- Method-wise Interceptor Chains
- Internal Working of Spring AOP

# Spring AOP (Aspect-Oriented Programming) - Part 2
*Library Management Project Notes*

---

# Table of Contents

1. Dynamic Proxy Review
2. How Spring AOP Works
3. Why Proxy is Required
4. AOP Proxy Creation
5. Interceptors
6. Interceptor Chain
7. Method-wise Interceptor Chains
8. Multiple Annotations
9. Reflection + AOP
10. Complete Internal Flow
11. Summary

---

# Dynamic Proxy Review

Before learning AOP internally, let's quickly revise Dynamic Proxy.

Earlier we learned

```text
Client

↓

Proxy

↓

InvocationHandler

↓

Real Object
```

The client never talks directly to the real object.

Instead

```java
bookService.issueBook();
```

actually becomes

```java
proxy.issueBook();
```

The proxy forwards the call to

```java
handler.invoke(...)
```

The handler decides

- What should happen before the method
- Whether the real method should execute
- What should happen after the method

---

# Why Proxy is Required?

Suppose

```java
@Service
public class BookService {

    public void issueBook() {

        System.out.println("Issue Book");

    }

}
```

Without a proxy

```text
Client

↓

BookService

↓

issueBook()
```

Output

```text
Issue Book
```

There is no place where Spring can execute

- Logging
- Transactions
- Security
- Cache

---

With Proxy

```text
Client

↓

BookServiceProxy

↓

BookService
```

Now

```java
proxy.issueBook();
```

can execute

```text
Before Logic

↓

Real Method

↓

After Logic
```

without changing the original class.

---

# AOP Proxy Creation

Suppose

```java
@Service
public class BookService {

    @Transactional
    public void issueBook(){}

}
```

Application starts.

Spring scans

```text
BookService
```

↓

Reflection reads methods

```java
clazz.getDeclaredMethods();
```

↓

Finds

```java
@Transactional
```

↓

Spring decides

```text
Normal Bean?

↓

No

↓

Create Proxy
```

Instead of storing

```text
BookService
```

inside IoC,

Spring stores

```text
BookServiceProxy
```

The proxy internally contains the real

```text
BookService
```

---

# What does @Autowired receive?

Suppose

```java
@Autowired
BookService service;
```

Question

Do we receive

```text
BookService
```

?

No.

We actually receive

```text
BookServiceProxy
```

The proxy internally delegates to the real object.

---

# How does Spring know to create a proxy?

Reflection scans every method.

Conceptually

```java
Method[] methods =
        clazz.getDeclaredMethods();

for(Method method : methods){

    if(method.isAnnotationPresent(
            Transactional.class
    )){

        // This bean needs Transaction Proxy

    }

}
```

Reflection discovers the annotation.

Spring creates the appropriate proxy.

---

# What is an Interceptor?

Earlier in Java Dynamic Proxy we had

```java
InvocationHandler
```

In Spring AOP we generally use

```text
Interceptor
```

Conceptually they play a similar role.

Their responsibility is

```text
Before Logic

↓

Call Next

↓

After Logic
```

---

Example

Transaction Interceptor

```text
Begin Transaction

↓

Next

↓

Commit
```

Logging Interceptor

```text
Print Method Started

↓

Next

↓

Print Method Finished
```

Each interceptor performs only one responsibility.

---

# Interceptor Chain

Suppose

```java
@Transactional
public void issueBook(){}
```

and our application also has a Logging Aspect.

Spring builds

```text
Client

↓

Logging Interceptor

↓

Transaction Interceptor

↓

Real Method
```

Execution

```text
Method Started

↓

Begin Transaction

↓

issueBook()

↓

Commit

↓

Method Finished
```

Notice

Each interceptor wraps the next one.

---

# How does each Interceptor work?

Conceptually

```java
invoke(){

    before();

    next.invoke();

    after();

}
```

Every interceptor simply performs its work and forwards the call.

---

Visual

```text
Interceptor 1

↓

Interceptor 2

↓

Interceptor 3

↓

Business Method
```

Every interceptor says

```text
Do my work

↓

Pass to next

↓

Finish my work
```

---

# Method-wise Interceptor Chains

Suppose

```java
@Service
public class BookService {

    @Transactional
    public void issueBook(){}

    @Cacheable
    public Book getBook(Long id){}

}
```

Spring creates only

```text
One Proxy
```

Not two.

Internally the proxy stores metadata.

Conceptually

```text
issueBook()

↓

Transaction Interceptor

--------------------------

getBook()

↓

Cache Interceptor
```

The proxy chooses the correct interceptor chain depending on the invoked method.

---

# Method 1

Client

```java
service.issueBook();
```

Proxy checks

```text
Applicable Interceptors?

↓

Transaction
```

Flow

```text
Client

↓

Proxy

↓

Transaction Interceptor

↓

Business Method
```

---

# Method 2

Client

```java
service.getBook(10);
```

Proxy checks

```text
Applicable Interceptors?

↓

Cache
```

Flow

```text
Client

↓

Proxy

↓

Cache Interceptor

↓

Business Method
```

Same proxy.

Different interceptor chain.

---

# Multiple Annotations

Suppose

```java
@Transactional
@Cacheable
public Book getBook(Long id){}
```

Applicable interceptors become

```text
Cache

↓

Transaction

↓

Business Method
```

(Exact order depends on Spring's ordering rules.)

Execution

```text
Cache Check

↓

Cache Miss

↓

Begin Transaction

↓

Business Method

↓

Commit

↓

Store Result in Cache

↓

Return
```

If cache already contains the object

```text
Cache Hit

↓

Return Object
```

Business method never executes.

Transaction never starts.

---

# Why Multiple Interceptors?

Imagine one huge handler

```java
if(transaction){}

if(cache){}

if(logging){}

if(async){}

if(retry){}

if(security){}
```

Very difficult to maintain.

Instead

Spring creates

```text
Transaction Interceptor

Logging Interceptor

Cache Interceptor

Retry Interceptor

Security Interceptor
```

Each interceptor performs one responsibility.

This follows the

**Single Responsibility Principle (SRP).**

---

# Reflection + AOP

Reflection is responsible for

- Finding methods
- Reading annotations

Example

```java
method.isAnnotationPresent(
        Transactional.class
);
```

Reflection itself does not execute transactions.

It only discovers metadata.

Spring uses this metadata to

- Create Proxy
- Build Interceptor Chain

---

Relationship

```text
Reflection

↓

Discovers Annotation

↓

Spring

↓

Creates Proxy

↓

Adds Interceptors

↓

Runtime Execution
```

Reflection and AOP are different concepts.

Reflection discovers.

AOP acts.

---

# Complete Internal Flow

Application Starts

↓

Component Scan

↓

Reflection

↓

Read Methods

↓

Read Annotations

↓

@Transactional Found

↓

Bean Requires Proxy

↓

Generate Proxy

↓

Build Interceptor Chain

↓

Store Proxy inside IoC Container

↓

@Autowired returns Proxy

↓

Client invokes Method

↓

Proxy selects applicable Interceptors

↓

Execute Chain

↓

Real Method

↓

Return Result

---

# Complete Execution Flow

```text
Client

↓

Proxy

↓

Interceptor 1

↓

Interceptor 2

↓

Interceptor 3

↓

Business Method

↑

Interceptor 3

↑

Interceptor 2

↑

Interceptor 1

↑

Return
```

---

# Summary

We learned

- Dynamic Proxy Review
- Why Proxy is required
- Spring AOP Proxy Creation
- Reflection discovers annotations
- Proxy stores interceptor metadata
- Interceptors
- Interceptor Chain
- Method-wise interceptor chains
- Multiple annotations
- Relationship between Reflection and AOP

---

# Next Part

Part 3 covers

- @Transactional Internal Working
- Logging Aspect
- Cache Aspect
- Async Aspect
- Retry Aspect
- Real Spring Examples
- Interview Questions
- Final Summary

# Spring AOP (Aspect-Oriented Programming) - Part 3
*Library Management Project Notes*

---

# Table of Contents

1. @Transactional Internals
2. Logging Aspect
3. Cache Aspect
4. Async Aspect
5. Retry Aspect
6. How Spring Chooses Interceptors
7. Complete Request Flow
8. Reflection vs Dynamic Proxy vs AOP
9. Advantages & Limitations
10. Interview Questions
11. Final Summary

---

# @Transactional Internal Working

Suppose

```java
@Service
public class BookService {

    @Transactional
    public void issueBook() {

        loanRepository.save(...);

        inventoryRepository.update(...);

    }

}
```

We only wrote

```java
@Transactional
```

Where is

- beginTransaction()
- commit()
- rollback()

Answer:

Inside Spring's Transaction Interceptor.

---

## Without Spring AOP

We would write

```java
public void issueBook() {

    beginTransaction();

    try{

        loanRepository.save(...);

        inventoryRepository.update(...);

        commit();

    }catch(Exception e){

        rollback();

        throw e;

    }

}
```

---

## With Spring AOP

We write only

```java
@Transactional
public void issueBook(){

    loanRepository.save(...);

    inventoryRepository.update(...);

}
```

Spring Proxy does

```text
Begin Transaction

↓

Business Method

↓

Commit

↓

Return
```

If exception occurs

```text
Begin Transaction

↓

Business Method

↓

Exception

↓

Rollback

↓

Throw Exception
```

---

# Conceptual Transaction Interceptor

Conceptually it behaves like

```java
invoke(){

    beginTransaction();

    try{

        next.invoke();

        commit();

    }catch(Exception e){

        rollback();

        throw e;

    }

}
```

Notice

Your service class never contains transaction code.

---

# Logging Aspect

Suppose every service method should log

```text
Method Started

Method Finished
```

Without AOP

```java
public void issueBook(){

    log();

    businessLogic();

    log();

}
```

With AOP

```java
@Before(...)
public void logStart(){}

@After(...)
public void logEnd(){}
```

Flow

```text
Logging

↓

Business Method

↓

Logging
```

No modification to business code.

---

# Cache Aspect

Suppose

```java
@Cacheable
public Book getBook(Long id){}
```

Execution

```text
Client

↓

Cache Interceptor

↓

Cache Hit?

```

If YES

```text
Return Cached Object
```

Business method never executes.

If NO

```text
Business Method

↓

Store Result in Cache

↓

Return
```

---

# Async Aspect

Suppose

```java
@Async
public void sendEmail(){}
```

Normally

```text
Client

↓

Wait

↓

Email Sent

↓

Return
```

With Async

```text
Client

↓

Async Interceptor

↓

Create New Thread

↓

Return Immediately

↓

Background Thread

↓

sendEmail()
```

The caller doesn't wait.

---

# Retry Aspect

Suppose

```java
@Retryable
public void processPayment(){}
```

Flow

```text
Call Payment

↓

Failed?

↓

Retry

↓

Retry Again

↓

Retry Again

↓

Success
```

The business method knows nothing about retries.

---

# Security Aspect

Suppose

```java
public void deleteBook(){}
```

Requirement

Only ADMIN should execute this.

Interceptor

```text
Check Permission

↓

Allowed?

↓

YES

↓

Business Method
```

If NO

```text
Throw Exception
```

Business method never executes.

---

# Performance Monitoring

Suppose we want execution time.

Interceptor

```text
Start Time

↓

Business Method

↓

End Time

↓

Print Time
```

Output

```text
issueBook()

Execution Time = 152 ms
```

---

# Exception Logging

Without AOP

```java
try{

}catch(Exception e){

    log.error(...);

}
```

inside every method.

With AOP

Exception Aspect

```text
Business Method

↓

Exception

↓

Log Exception

↓

Throw Exception
```

---

# How Spring Chooses Interceptors

Application Startup

↓

Reflection scans methods

↓

Finds

```java
@Transactional
```

↓

Add Transaction Interceptor

---

Finds

```java
@Cacheable
```

↓

Add Cache Interceptor

---

Finds

```java
@Async
```

↓

Add Async Interceptor

---

Finds Logging Aspect

↓

Add Logging Interceptor

---

Final Chain

```text
Client

↓

Logging

↓

Cache

↓

Transaction

↓

Business Method
```

(Actual order depends on Spring configuration.)

---

# Complete Request Flow

Suppose

```java
@Transactional
public void issueBook(){}
```

Application starts

↓

Reflection

↓

Read Annotation

↓

Proxy Created

↓

Transaction Interceptor Added

↓

IoC stores Proxy

↓

@Autowired receives Proxy

↓

Client

```java
service.issueBook();
```

↓

Proxy

↓

Transaction Interceptor

↓

Real Method

↓

Commit

↓

Return

---

# Reflection vs Dynamic Proxy vs AOP

## Reflection

Responsible for

- Reading methods
- Reading annotations
- Reading fields
- Creating objects

Example

```java
method.isAnnotationPresent(
        Transactional.class
);
```

Reflection discovers metadata.

---

## Dynamic Proxy

Responsible for

- Intercepting method calls

Example

```text
Client

↓

Proxy

↓

Handler

↓

Real Object
```

---

## Spring AOP

Responsible for

- Transactions
- Logging
- Security
- Caching
- Async
- Retry

Uses

- Reflection
- Dynamic Proxy

to implement all these features.

---

Visual

```text
Reflection

↓

Find Annotation

↓

Spring

↓

Create Proxy

↓

Interceptor Chain

↓

Business Method
```

---

# Advantages of AOP

- Clean Business Logic
- No Duplicate Code
- Better Maintainability
- Separation of Concerns
- Easy to add new functionality
- Reusable Aspects
- Less Boilerplate

---

# Limitations

- Slight Runtime Overhead
- Harder Debugging
- Self-invocation limitation (calling one method from another within the same class bypasses the proxy in proxy-based Spring AOP)
- Too many aspects can make execution flow difficult to follow

---

# Common AOP Use Cases

| Feature | Annotation |
|----------|------------|
| Transaction | `@Transactional` |
| Async | `@Async` |
| Cache | `@Cacheable` |
| Retry | `@Retryable` |
| Logging | Custom Aspect |
| Security | Spring Security Aspects |
| Performance | Custom Aspect |

---

# Interview Questions

## What is AOP?

Aspect-Oriented Programming separates

Business Logic

from

Cross-Cutting Concerns.

---

## Why is Dynamic Proxy required?

Without Proxy

Spring cannot execute

- Logging
- Transactions
- Security

before or after method execution.

---

## Difference between Join Point and Pointcut?

Join Point

Every method that can be intercepted.

Pointcut

Rule selecting which Join Points receive Advice.

---

## What is Advice?

Extra logic executed

- Before
- After
- Around

a method.

---

## What is Aspect?

A class containing

- Pointcuts
- Advice

---

## What is an Interceptor?

A component that wraps method execution.

Typical structure

```text
Before

↓

Next

↓

After
```

---

## Does Spring create one proxy per method?

No.

Spring creates

One Proxy per Bean.

Different methods inside the bean may use different interceptor chains.

---

## Does every method execute every interceptor?

No.

Spring determines applicable interceptors for the invoked method.

Example

```java
@Transactional
methodA()
```

↓

Transaction Interceptor

---

```java
@Cacheable
methodB()
```

↓

Cache Interceptor

---

## Does Reflection execute transactions?

No.

Reflection only discovers annotations.

Spring AOP uses that metadata to build proxies and interceptor chains.

---

# Final Summary

Business Logic

```text
Issue Book

Return Book

Create User
```

Cross-Cutting Concerns

```text
Logging

Transaction

Security

Cache

Retry

Async
```

Spring Flow

```text
Reflection

↓

Discover Metadata

↓

Proxy Creation

↓

Interceptor Chain

↓

Business Method
```

Architecture

```text
Client

↓

Proxy

↓

Interceptor 1

↓

Interceptor 2

↓

Interceptor 3

↓

Business Method

↑

Return
```

Remember

- Reflection **discovers**
- Dynamic Proxy **intercepts**
- AOP **adds reusable cross-cutting behavior**

Together they enable features like

- `@Transactional`
- `@Async`
- `@Cacheable`
- `@Retryable`
- Logging
- Security
- Performance Monitoring

without modifying your business logic.

---

# Spring AOP Complete ✅

You now understand:

- Why AOP exists
- How Spring creates proxies
- How interceptor chains work
- How `@Transactional` works internally
- How Reflection and Dynamic Proxy enable Spring AOP
- The architecture behind the most commonly used Spring annotations