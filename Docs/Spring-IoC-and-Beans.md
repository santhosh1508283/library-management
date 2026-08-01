# 📖 03 - Spring IoC & Beans

> Project: **Library Management System**

**Goal**

Understand how Spring creates and manages objects internally.

After reading this document, you should be able to answer:

- Why was Spring created?
- What is IoC?
- What is Dependency Injection?
- What is a Bean?
- What is the IoC Container?
- Who creates objects?
- Why don't we write `new UserService()`?

---

# Before Spring

Suppose we are building our Library Management project without Spring.

```java
public class Main {

    public static void main(String[] args) {

        UserService userService = new UserService();

        userService.createUser();
    }

}
```

Question.

Who created UserService?

Answer:

```
We did.
```

We wrote

```java
new UserService()
```

Everything was our responsibility.

---

Now suppose UserService needs UserRepository.

```java
public class UserService {

    private UserRepository repository;

}
```

Again

```java
UserRepository repository =
        new UserRepository();

UserService service =
        new UserService(repository);
```

Now suppose UserRepository needs

```
EntityManager
```

Again

```java
new EntityManager(...)
```

Suppose EntityManager needs

```
DataSource
```

Again

```java
new HikariDataSource(...)
```

Soon our application becomes

```
Main

↓

new UserService()

↓

new UserRepository()

↓

new EntityManager()

↓

new DataSource()

↓

new Connection()
```

Imagine 500 classes.

Managing them manually becomes impossible.

---

# Why Spring Exists

Spring solves one problem.

```
Object Management
```

Instead of developers creating every object,

Spring says

> "I'll create them."

---

# The Biggest Idea

Without Spring

```
Developer

↓

Creates Objects
```

With Spring

```
Spring

↓

Creates Objects
```

This idea is called

```
Inversion of Control
```

---

# What is Inversion of Control (IoC)?

Normally

```
Application

↓

Controls Object Creation
```

Spring changes this.

```
Spring

↓

Controls Object Creation
```

Control is inverted.

Hence the name

```
Inversion of Control
```

---

# Example

Without Spring

```java
UserService service =
        new UserService();
```

With Spring

```java
@Autowired
UserService service;
```

Notice.

We never wrote

```java
new UserService()
```

Spring created it.

---

# What is Dependency Injection?

Suppose

```java
public class UserService {

    private UserRepository repository;

}
```

UserService depends on UserRepository.

Without Spring

```java
UserRepository repository =
        new UserRepository();

UserService service =
        new UserService(repository);
```

We inject dependency manually.

Spring does the same thing automatically.

This process is called

```
Dependency Injection
```

Spring creates

```
Repository
```

Then gives it to

```
Service
```

Automatically.

---

# What is a Bean?

One of the most common interview questions.

Question.

Is every object a Bean?

No.

Example

```java
User user = new User();
```

Is this a Spring Bean?

No.

It is just a Java Object.

---

Now

```java
@Service
public class UserService {

}
```

Spring creates

```
UserService
```

Stores it inside its container.

Now it becomes

```
Spring Bean
```

Definition:

> A Bean is simply an object created, managed and maintained by the Spring IoC Container.

---

# Bean vs Normal Object

Normal Object

```java
User user = new User();
```

Created by

```
Developer
```

Managed by

```
Developer
```

Destroyed by

```
Garbage Collector
```

---

Spring Bean

```java
@Service
public class UserService {

}
```

Created by

```
Spring
```

Managed by

```
Spring
```

Injected by

```
Spring
```

Destroyed by

```
Spring
```

---

# What is IoC Container?

Imagine a giant box.

```
+--------------------------+
|                          |
|   Spring IoC Container   |
|                          |
|   UserService            |
|   UserRepository         |
|   DataSource             |
|   EntityManager          |
|                          |
+--------------------------+
```

This box stores all Spring Beans.

Whenever someone asks

```
Give me UserService
```

Container returns it.

---

# Why Container?

Suppose Controller needs UserService.

Instead of

```java
new UserService();
```

Spring simply checks

```
Container

↓

Already Exists?

↓

Yes

↓

Return Bean
```

No duplicate objects.

---

# Bean Creation

During application startup

Spring scans your project.

It looks for

```
@Component

@Service

@Repository

@Controller

@Configuration
```

Whenever it finds one

It creates the object.

Example

```java
@Service
public class UserService {

}
```

Spring internally performs something similar to

```java
UserService service =
        new UserService();

container.add(service);
```

Now the bean is available everywhere.

---

# Startup Flow

```
SpringApplication.run()

↓

Component Scan

↓

Find @Service

↓

Create Object

↓

Store in IoC Container

↓

Application Ready
```

---

# Our Project

Remember

```java
@SpringBootApplication
public class LibraryManagementApplication {

}
```

When we ran

```java
SpringApplication.run(...)
```

Spring started scanning packages.

It found

```
UserService
```

because of

```java
@Service
```

Spring created

```
UserService Bean
```

Stored it inside

```
IoC Container
```

Later

```java
@Autowired
UserService service;
```

Spring simply returned the same bean.

---

# Why don't we use new?

Suppose you write

```java
UserService service =
        new UserService();
```

Question.

Is this Spring Bean?

No.

Spring doesn't know this object exists.

Therefore

```
No Dependency Injection

No Transactions

No AOP

No Lifecycle Management
```

Always let Spring create Spring-managed objects.

---

# Summary

Without Spring

```
Developer

↓

Creates Objects

↓

Connects Objects

↓

Maintains Objects
```

With Spring

```
Spring

↓

Creates Beans

↓

Injects Dependencies

↓

Maintains Lifecycle

↓

Destroys Beans
```

---

# Interview Questions

## What is IoC?

Spring controls object creation instead of the application.

---

## What is Dependency Injection?

Providing required dependencies to an object instead of letting the object create them.

---

## What is a Bean?

An object created and managed by the Spring IoC Container.

---

## Is every Java object a Bean?

No.

Only objects managed by Spring are Beans.

---

## What is the IoC Container?

The container responsible for creating, storing, injecting and managing Spring Beans.

---

## Why shouldn't we use new for Services?

Because Spring cannot manage manually created objects.

# 📖 03 - Spring IoC & Beans (Part 1B)

---

# Component Scanning

Suppose our project structure looks like this.

```
com.santhosh.library
│
├── LibraryManagementApplication.java
│
├── controller
│   └── UserController.java
│
├── service
│   └── UserService.java
│
├── repository
│   └── UserRepository.java
│
├── entity
│   └── User.java
│
└── config
```

Question:

**How did Spring know that `UserServiceTesting` exists?**

We never registered it manually.

Answer:

```
Component Scanning
```

Spring automatically searches your project for classes annotated with Spring stereotypes and creates beans for them.

---

# What is Component Scanning?

When the application starts

```java
SpringApplication.run(...)
```

Spring begins scanning packages.

Think of it like this:

```
Open Package

↓

Read Class

↓

Has Spring Annotation?

↓

YES

↓

Create Bean

↓

Store in IoC Container
```

It repeats this process for every class inside the scanned packages.

---

# Example

Suppose Spring finds

```java
@Service
public class UserService {

}
```

Spring immediately performs something conceptually similar to

```java
UserService service = new UserService();

container.add(service);
```

Now `UserServiceTesting` becomes a Spring Bean.

---

Another class

```java
@Repository
public class UserRepository {

}
```

Spring performs

```java
UserRepository repository = new UserRepository();

container.add(repository);
```

---

Another class

```java
@Component
public class EmailSender {

}
```

Again

```
Create Object

↓

Store Bean
```

---

But this class

```java
public class User {

}
```

contains no Spring annotation.

Spring simply ignores it.

Why?

Because entities are **not** Spring Beans.

They are managed by Hibernate, not by the Spring IoC Container.

---

# Which Annotations are Scanned?

Spring scans these annotations:

```
@Component

@Service

@Repository

@Controller

@RestController

@Configuration
```

Every one of these eventually becomes a Spring Bean.

---

# The Secret Behind @Service

Open the source code of `@Service`.

You'll find something very similar to

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {

}
```

Notice

```java
@Component
```

This means

```
@Service

IS A

@Component
```

So Spring doesn't have separate logic for `@Service`.

It simply recognizes it as a Component.

---

# Same for Repository

Internally

```java
@Repository
```

is also

```java
@Component
```

---

# Same for Controller

```java
@Controller
```

↓

```java
@Component
```

---

# Same for RestController

```java
@RestController
```

contains

```java
@Controller
```

which contains

```java
@Component
```

Everything finally reaches

```
@Component
```

---

# Then Why So Many Annotations?

Excellent Interview Question.

Technically

this works

```java
@Component
public class UserService {

}
```

Exactly the same as

```java
@Service
public class UserService {

}
```

Application works perfectly.

So why introduce `@Service`?

Because it gives meaning.

---

Suppose another developer opens your project.

They immediately understand

```java
@Service
```

↓

Business Logic

---

```java
@Repository
```

↓

Database Layer

---

```java
@Controller
```

↓

Handles HTTP Requests

---

```java
@Configuration
```

↓

Creates Beans

---

The annotation itself documents the responsibility of the class.

---

# Additional Behaviour

Some annotations also provide extra framework behaviour.

Example:

```java
@Repository
```

Spring automatically converts database-specific exceptions into Spring DataAccessException.

This is called

```
Exception Translation
```

So these annotations are not just labels.

Some of them also provide additional functionality.

---

# Package Scanning

Now another question.

How does Spring know where to start scanning?

Remember

```java
@SpringBootApplication
public class LibraryManagementApplication {

}
```

Suppose this class is inside

```
com.santhosh.library
```

Spring starts scanning from here.

```
com.santhosh.library

↓

controller

↓

service

↓

repository

↓

config

↓

...
```

Everything inside this package is scanned.

---

# What if another package exists?

Suppose

```
com.other.application
```

Spring ignores it.

Because it is outside the root package.

---

# Why Keep SpringBootApplication at Root?

This is why every Spring Boot project looks like

```
com.company.project

│
├── Application.java
│
├── controller
├── service
├── repository
├── entity
```

Keeping the Application class at the root ensures every sub-package is scanned automatically.

---

# Wrong Structure

Suppose

```
com.company.app
│
└── Application.java

com.company.library.service
│
└── UserService.java
```

Spring starts scanning

```
com.company.app
```

It never reaches

```
com.company.library.service
```

Result

```
NoSuchBeanDefinitionException
```

because the bean was never created.

---

# Our Library Project

Remember this experiment.

We removed

```java
@Service
```

from

```java
UserService
```

Then we tried

```java
@Autowired
UserService userService;
```

Spring searched the container.

```
UserService Bean?

↓

Not Found
```

Therefore

```
NoSuchBeanDefinitionException
```

Exactly as expected.

---

# What if We Create Object Ourselves?

Suppose

```java
UserService service = new UserService();
```

Question

Will Spring know this object exists?

Answer

No.

Spring only manages objects that **it created**.

Your manually created object lives completely outside the IoC Container.

Therefore it will not receive

- Dependency Injection
- Transactions
- AOP
- Bean Lifecycle callbacks

---

# Bean Lifecycle (High Level)

Application Starts

↓

Component Scan

↓

Find Annotation

↓

Create Object

↓

Store Bean

↓

Inject Dependencies

↓

Application Ready

↓

Application Stops

↓

Destroy Beans

---

# Bean Naming

Suppose

```java
@Service
public class UserService {

}
```

Spring automatically creates the bean name

```
userService
```

Notice

```
UserService

↓

userService
```

First letter becomes lowercase.

Conceptually

```
Container

↓

"userService"

↓

UserService Object
```

---

# Complete Startup Flow

```
SpringApplication.run()

↓

Create IoC Container

↓

Component Scan

↓

Find @Service

↓

Create UserService

↓

Store Bean

↓

Find @Repository

↓

Create UserRepository

↓

Store Bean

↓

Inject Dependencies

↓

Application Ready
```

---

# Summary

```
@SpringBootApplication

↓

Component Scan

↓

@Service Found

↓

Create Bean

↓

Store in Container

↓

@Autowired

↓

Return Existing Bean
```

Spring never creates duplicate singleton beans.

Every request asking for `UserServiceTesting` receives the same bean instance.

---

# Interview Questions

## What is Component Scanning?

It is the process by which Spring searches packages for Spring-managed classes and creates beans.

---

## Does Spring scan the whole project?

No.

It scans from the package containing `@SpringBootApplication` and all of its sub-packages.

---

## Why does @Service work?

Because `@Service` is itself annotated with `@Component`.

---

## Can @Component replace @Service?

Yes.

But `@Service` makes the class responsibility much clearer and is the recommended annotation for the service layer.

---

## Why did removing @Service break our application?

Because Spring never created the bean.

When `@Autowired` requested `UserServiceTesting`, the IoC Container had no such bean.

---

## Are Entities Spring Beans?

No.

Entities are managed by Hibernate's Persistence Context.

They are not managed by Spring's IoC Container.

---

## Why should Application.java be in the root package?

Because Spring begins component scanning from the package containing `@SpringBootApplication`.

Keeping it at the root ensures all application packages are scanned automatically.

# 📖 03 - Spring IoC & Beans (Part 2A)

> Project: Library Management System

---

# @Configuration

Until now, Spring created beans automatically using annotations like

```java
@Service
@Repository
@Component
```

But what if we want Spring to manage a class that **we cannot modify**?

For example

```java
HikariDataSource

ObjectMapper

RestTemplate

PasswordEncoder
```

These classes belong to external libraries.

We cannot add

```java
@Component
```

to them.

So how do we make them Spring Beans?

Spring provides

```java
@Configuration
```

---

# What is @Configuration?

A class annotated with

```java
@Configuration
```

tells Spring

> "This class contains methods that create Spring Beans."

Example

```java
@Configuration
public class AppConfig {

}
```

During startup

Spring scans

```
@Configuration

↓

Create Config Bean

↓

Execute @Bean methods

↓

Store Returned Objects
```

---

# What is @Bean?

Suppose we want Spring to manage

```java
PasswordEncoder
```

We write

```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

}
```

Notice something.

We wrote

```java
new BCryptPasswordEncoder()
```

ourselves.

But because it is returned from

```java
@Bean
```

Spring stores it inside the IoC Container.

---

# What Actually Happens?

Conceptually Spring performs

```java
PasswordEncoder encoder =
        passwordEncoder();

container.add(encoder);
```

Now

```java
@Autowired
PasswordEncoder encoder;
```

works.

---

# @Component vs @Bean

Many beginners confuse these.

## @Component

Object creates itself.

```java
@Component
public class EmailService {

}
```

Spring directly creates

```java
new EmailService()
```

---

## @Bean

You create object manually.

Spring manages it afterwards.

```java
@Bean
public EmailService emailService(){

    return new EmailService();

}
```

---

Difference

| @Component | @Bean |
|------------|-------|
| Spring creates object | Developer creates object |
| Used for our classes | Used for external library classes |
| Class level | Method level |

---

# Which One Should We Use?

Suppose

```java
public class UserService {

}
```

Use

```java
@Service
```

Suppose

```java
ObjectMapper
```

Use

```java
@Bean
```

Because you cannot modify ObjectMapper source code.

---

# Why Configuration Class?

Suppose

```java
@Bean
public ObjectMapper mapper(){

}
```

Spring needs to know

where this method exists.

Therefore

```java
@Configuration
```

groups all bean definitions together.

---

# Our Project Example

Suppose tomorrow we need

```java
BCryptPasswordEncoder
```

Instead of

```java
new BCryptPasswordEncoder();
```

everywhere,

we create

```java
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();

    }

}
```

Now

```java
@Autowired
BCryptPasswordEncoder encoder;
```

works anywhere.

---

# @Autowired

This is probably the most used Spring annotation.

Suppose

```java
@Service
public class UserService {

}
```

Spring already created

```
UserService Bean
```

Now another class needs it.

Without Spring

```java
UserService service =
        new UserService();
```

With Spring

```java
@Autowired
private UserService userService;
```

Spring injects it automatically.

---

# What Does @Autowired Actually Do?

Conceptually

Spring performs

```java
Need UserService?

↓

Search IoC Container

↓

Found Bean?

↓

Inject Bean
```

---

# Example

Container

```
UserService Bean

↓

Object A
```

Controller

```java
@Autowired
UserService userService;
```

Spring injects

```
Object A
```

No new object created.

---

# How Does Spring Search?

Spring first searches by

```
Type
```

Suppose

```java
@Autowired
UserService service;
```

Spring asks

```
Container

↓

Any Bean of type UserService?

↓

Yes

↓

Inject
```

---

# What if Two Beans Exist?

Suppose

```java
@Service
public class EmailService {

}
```

and

```java
@Service
public class SmsService
        implements NotificationService {

}
```

Another class

```java
@Autowired
NotificationService service;
```

Spring finds

```
EmailService

SmsService
```

Two beans.

Now it doesn't know which one to inject.

Result

```
NoUniqueBeanDefinitionException
```

---

# How to Solve?

Using

```java
@Qualifier
```

Example

```java
@Autowired

@Qualifier("emailService")

NotificationService service;
```

Now Spring knows exactly which bean to inject.

---

# Constructor Injection

Recommended approach.

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){

        this.repository = repository;

    }

}
```

Spring sees

```
Need UserRepository

↓

Find Bean

↓

Call Constructor
```

No field injection required.

---

# Why Constructor Injection?

Advantages

- Immutable dependencies
- Easier testing
- Easier to understand
- Prevents NullPointerException
- Official Spring recommendation

---

# Field Injection

This is what we used initially.

```java
@Autowired
private UserRepository repository;
```

Works.

But not recommended.

Because dependency becomes hidden.

Constructor Injection clearly tells everyone

```
UserService cannot exist
without UserRepository.
```

---

# Setter Injection

Third option.

```java
@Autowired
public void setRepository(UserRepository repository){

    this.repository = repository;

}
```

Rarely used.

Mostly when dependency is optional.

---

# Injection Summary

Field

```java
@Autowired
private UserRepository repository;
```

Simple

But less preferred.

---

Constructor

```java
public UserService(UserRepository repository){

}
```

Recommended.

---

Setter

```java
setRepository(...)
```

Used for optional dependencies.

---

# Internal Flow

Application Starts

↓

Component Scan

↓

Create UserRepository

↓

Store Bean

↓

Create UserService

↓

Needs UserRepository

↓

Inject Repository

↓

Store UserService

↓

Application Ready

---

# Interview Questions

### Difference between @Component and @Bean?

@Component lets Spring create the object.

@Bean lets the developer create the object while Spring manages it.

---

### Why use @Configuration?

To declare methods that create Spring Beans.

---

### What does @Autowired do?

Automatically injects required dependencies from the IoC Container.

---

### How does Spring find dependency?

By type first.

If multiple beans exist,

Qualifier or Primary is required.

---

### Which Injection is recommended?

Constructor Injection.

Because it creates immutable, testable classes and clearly defines required dependencies.

# 📖 03 - Spring IoC & Beans (Part 2B)

> Project: **Library Management System**

---

# Bean Lifecycle

One of the biggest responsibilities of Spring is managing the entire lifecycle of a Bean.

Most developers think

```
Spring

↓

Creates Object

↓

Done
```

Not true.

Spring manages the bean from its creation until the application shuts down.

---

# Complete Bean Lifecycle

```
Application Starts

↓

Component Scan

↓

Create Bean

↓

Inject Dependencies

↓

Initialize Bean

↓

Bean Ready

↓

Application Running

↓

Destroy Bean

↓

Application Stops
```

Every Spring Bean goes through this lifecycle.

---

# Our UserService Example

Suppose

```java
@Service
public class UserService {

}
```

Spring performs something conceptually similar to

```
new UserService()

↓

Inject UserRepository

↓

Bean Ready

↓

Store inside IoC Container
```

Whenever another class asks for

```java
@Autowired
UserService userService;
```

Spring returns the same object.

---

# Singleton Scope

By default

Every Spring Bean is

```
Singleton
```

Question.

What does Singleton mean?

It means

```
Only One Object
```

for the entire Spring Application Context.

---

Example

```java
@Service
public class UserService {

}
```

Suppose

```
UserController

↓

@Autowired UserService
```

and

```
AdminController

↓

@Autowired UserService
```

Question.

How many UserService objects?

Answer

```
ONE
```

Both controllers receive the same object.

---

# Visual Representation

```
IoC Container

↓

UserService Object
```

Controller A

↓

Points here

Controller B

↓

Points here

Controller C

↓

Points here

Everyone shares the same bean.

---

# Why Singleton?

Imagine

100 Controllers

Without Singleton

```
100 UserService Objects
```

Each object consumes memory.

Instead

Spring creates

```
ONE
```

Object.

Advantages

- Less memory
- Faster startup
- Less garbage collection
- Easier dependency management

---

# Prototype Scope

Suppose we don't want Singleton.

We want

```
New Object Every Time
```

Spring provides

```java
@Scope("prototype")
```

Example

```java
@Component
@Scope("prototype")
public class ReportGenerator {

}
```

Now

```java
@Autowired
ReportGenerator report1;

@Autowired
ReportGenerator report2;
```

These are different objects.

---

# Singleton vs Prototype

| Singleton | Prototype |
|------------|-----------|
| One object | New object every request |
| Default scope | Explicitly configured |
| Shared | Not shared |
| Memory efficient | More memory |

---

# The Question You Asked

> If there is only one UserService object, how can two users use it at the same time?

This is one of the most important Spring concepts.

Let's understand carefully.

---

# Suppose Two Requests Arrive

Request A

```
Signup Rahul
```

Request B

```
Signup Santhosh
```

Both reach

```java
UserService
```

Question

Do we create another UserService?

No.

Same object.

---

# Doesn't One User Have To Wait?

No.

Because Java executes methods using

```
Threads
```

Every request gets its own thread.

Suppose

```java
public void createUser(String name){

    User user = new User();

    user.setName(name);

}
```

Request A

```
Thread A
```

Request B

```
Thread B
```

Both execute the same method simultaneously.

---

# JVM Memory

Understanding JVM memory makes everything clear.

```
JVM

↓

Heap

↓

Stack
```

---

# Heap Memory

Heap stores

```
Objects
```

Example

```
UserService

UserRepository

User

Book

Author
```

All Spring Beans live here.

```
Heap

↓

UserService
```

Only one object.

---

# Stack Memory

Every thread gets its own stack.

Thread A

```
createUser()

↓

name

↓

user
```

Thread B

```
createUser()

↓

name

↓

user
```

Notice

```
Different Stack
```

Local variables are **not shared**.

---

# Visualization

```
                 Heap
        -------------------
        | UserService     |
        -------------------

          ▲           ▲
          │           │
     Thread A     Thread B

       Stack         Stack

 createUser()   createUser()

 name="Rahul"   name="Santhosh"

 User Object    User Object
```

Both threads use the same UserService object.

But every thread has its own local variables.

Therefore

No conflict.

---

# Why Is This Safe?

Look at this method

```java
public void createUser(String name){

    User user = new User();

}
```

Question

Where is

```java
User user;
```

stored?

Answer

```
Stack
```

Each thread has its own stack.

No sharing.

Therefore

Safe.

---

# Dangerous Example

Suppose

```java
@Service
public class UserService {

    private User currentUser;

}
```

Now

```
currentUser
```

lives inside

```
Heap
```

Shared by everyone.

Thread A

```
currentUser = Rahul
```

Thread B

```
currentUser = Santhosh
```

Race Condition.

Both threads modify the same variable.

This is why Spring recommends

```
Stateless Beans
```

---

# Stateless Beans

Good

```java
public void createUser(String name){

    User user = new User();

}
```

Everything is local.

Safe.

---

Bad

```java
private User user;
```

Shared.

Unsafe.

---

# Golden Rule

A Spring Singleton Bean should never store request-specific data in instance variables.

Always keep request data in

- Method Parameters
- Local Variables
- Database
- Cache
- Session (when appropriate)

---

# Bean Lifecycle Callbacks

Spring also allows running code

Before Bean Ready

and

Before Bean Destruction.

Example

```java
@PostConstruct
public void init(){

    System.out.println("Bean Initialized");

}
```

Runs once after dependency injection.

---

Before application shutdown

```java
@PreDestroy
public void destroy(){

    System.out.println("Cleaning Resources");

}
```

Runs once before bean destruction.

---

# Complete Lifecycle Diagram

```
Spring Starts

↓

Component Scan

↓

Create Bean

↓

Inject Dependencies

↓

@PostConstruct

↓

Bean Ready

↓

Application Running

↓

@PreDestroy

↓

Destroy Bean

↓

Application Stops
```

---

# Summary

```
Spring Boot Starts

↓

Component Scan

↓

Create Singleton Beans

↓

Inject Dependencies

↓

Store in IoC Container

↓

Application Ready

↓

Multiple Threads

↓

Same Bean

↓

Different Stack Frames

↓

Thread Safe

↓

Application Stops

↓

Destroy Beans
```

---

# Interview Questions

## What is the default scope of a Spring Bean?

Singleton.

---

## How many UserService objects exist?

Only one.

---

## Why is Singleton memory efficient?

Because all classes share the same object instead of creating duplicate instances.

---

## How can one UserService handle thousands of users?

Because every HTTP request executes on a separate thread, and each thread has its own stack containing local variables.

---

## Where are Spring Beans stored?

Heap Memory.

---

## Where are method parameters and local variables stored?

Thread Stack.

---

## Why are local variables thread-safe?

Each thread has its own stack.

No sharing occurs.

---

## Why are instance variables dangerous?

They live in Heap Memory and are shared across all threads.

---

## What is a Stateless Bean?

A bean that does not store request-specific state in instance variables.

Stateless beans are naturally thread-safe.

---

## What is the difference between Heap and Stack?

| Heap | Stack |
|------|-------|
| Stores Objects | Stores Method Calls |
| Shared | Per Thread |
| Contains Spring Beans | Contains Local Variables |

---

# Final Summary

```
Spring IoC Container

↓

Creates Beans

↓

Injects Dependencies

↓

Stores Singleton Beans

↓

Multiple Threads

↓

Same Bean

↓

Different Stack Frames

↓

Safe Execution
```

At this point you should understand:

- Why Spring creates only one `UserServiceTesting`
- Why `@Autowired` returns the same object
- Why Singleton beans are safe
- How multiple users execute simultaneously
- Why local variables are thread-safe
- When Prototype scope should be used
- Complete Spring Bean lifecycle from creation to destruction