# Reflection in Java - Part 1
*Library Management Project Notes*

---

# Table of Contents

1. What is Reflection?
2. Why was Reflection introduced?
3. Reflection vs Normal Java
4. The Class Object
5. Ways to Get Class Object
6. Reflection Classes
7. Field Reflection
8. Reading Field Metadata
9. Reading Field Values
10. Writing Field Values
11. Why Hibernate Uses Reflection
12. Summary

---

# What is Reflection?

Reflection is a Java feature that allows a program to inspect and manipulate classes, methods, fields, constructors, and annotations **at runtime**.

Normally Java executes code that is known at compile time.

Reflection allows Java to discover information while the application is already running.

Example:

Instead of writing

```java
user.getName();
```

Reflection can execute

```java
method.invoke(user);
```

Even if the method was discovered only at runtime.

---

# Why was Reflection introduced?

Imagine you are writing the Spring Framework.

When Spring was developed, the framework developers did **not** know:

- UserService
- BookService
- UserRepository
- BookRepository

These classes did not exist.

So Spring cannot write

```java
new UserService();
```

because it has no idea that such a class will exist in the future.

Instead Spring discovers classes at runtime using Reflection.

---

# Reflection vs Normal Java

## Normal Java

```java
User user = new User();

user.getName();
```

The compiler already knows

- Object type
- Method name
- Parameter types

Everything is fixed.

---

## Reflection

```java
Class<?> clazz = User.class;

Method method = clazz.getDeclaredMethod("getName");

method.invoke(user);
```

The method is discovered while the program is running.

---

# Class Object

Every class loaded by the JVM has exactly **one Class object**.

Example

```java
public class User {

}
```

Even if we create

```java
User u1 = new User();

User u2 = new User();

User u3 = new User();
```

Memory looks like

```
                JVM

          +----------------+
          | Class<User>    |
          +----------------+
             ▲      ▲
             │      │
     User.class   u1.getClass()
                    ▲
                    │
               u2.getClass()
                    ▲
                    │
               u3.getClass()
```

There is only **one** Class object.

---

# What does Class object contain?

The Class object stores metadata about the class.

Example

```java
public class User {

    private Long id;

    private String name;

}
```

The Class object contains

- Class Name
- Fields
- Methods
- Constructors
- Parent Class
- Interfaces
- Annotations

Notice

It **does NOT contain object values**.

It contains only information about the class.

---

# Ways to Get Class Object

## Method 1

```java
Class<User> clazz = User.class;
```

Most common.

---

## Method 2

```java
User user = new User();

Class<?> clazz = user.getClass();
```

Returns the same Class object.

---

## Method 3

```java
Class<?> clazz =
        Class.forName(
            "com.santhosh.library.entity.User"
        );
```

Used by frameworks.

---

# Reflection Classes

Reflection provides several important classes.

| Reflection Class | Purpose |
|------------------|---------|
| Class | Information about a class |
| Field | Information about a field |
| Method | Information about a method |
| Constructor | Information about constructors |

---

# Field Reflection

Suppose

```java
public class User {

    private Long id;

    private String name;

    private String email;

}
```

Reflection gets all fields

```java
Class<User> clazz = User.class;

Field[] fields =
        clazz.getDeclaredFields();
```

Now Reflection knows

```
id

name

email
```

Notice

These are **Field objects**.

Not field values.

---

# Reading Field Metadata

A Field object contains information like

```
Field

↓

Field Name

↓

Field Type

↓

Modifiers

↓

Annotations
```

Example

```java
Field field =
        clazz.getDeclaredField("name");
```

Reflection can ask

```java
field.getName();
```

Returns

```
name
```

---

```java
field.getType();
```

Returns

```
String
```

---

```java
field.getModifiers();
```

Returns

```
private
```

---

# Field Metadata vs Field Value

Suppose

```java
public class User {

    private String name;

}
```

Reflection gets

```java
Field field =
        clazz.getDeclaredField("name");
```

This represents

```
Field Name

↓

name

Field Type

↓

String
```

It does NOT contain

```
Santhosh
```

because that belongs to an object.

---

# Reading Field Values

Suppose

```java
User user = new User();

user.setName("Santhosh");
```

Reflection already knows

```java
Field field =
        clazz.getDeclaredField("name");
```

Normally Java blocks access because

```
private
```

To bypass this

```java
field.setAccessible(true);
```

Now Reflection can read

```java
Object value =
        field.get(user);
```

Result

```
Santhosh
```

Notice

Reflection needs

- Field
- Object

because the value belongs to the object.

---

# Writing Field Values

Reflection can also modify private fields.

Example

```java
field.setAccessible(true);

field.set(user, "Rahul");
```

Equivalent to

```java
user.name = "Rahul";
```

even though the field is private.

---

# Why Hibernate Uses Reflection

Suppose

```java
@Entity
public class User {

    @Id
    private Long id;

    private String name;

    private String email;

}
```

Hibernate performs the following steps.

## Step 1

Read metadata

```java
Class<User> clazz = User.class;
```

---

## Step 2

Read fields

```java
clazz.getDeclaredFields();
```

Returns

```
id

name

email
```

---

## Step 3

Read annotations

```java
field.getAnnotations();
```

Returns

```
@Id

@Column
```

---

## Step 4

Read object values

```java
field.setAccessible(true);

field.get(user);
```

Returns

```
1

Santhosh

abc@gmail.com
```

---

## Step 5

Generate SQL

```sql
INSERT INTO users(id,name,email)
VALUES(1,'Santhosh','abc@gmail.com');
```

Notice

Hibernate never needs getters.

It directly accesses fields using Reflection.

---

# Important Notes

Reflection is slower than normal Java because Java has to inspect metadata during runtime.

Use Reflection only when classes or methods are not known during compilation.

Frameworks like Spring and Hibernate use Reflection extensively.

Business applications rarely use Reflection directly.

---

# Summary

Reflection allows Java to inspect classes while the application is running.

Key points

- Every class has exactly one Class object.
- Class object stores metadata.
- Field objects represent fields.
- Reflection can read field metadata.
- Reflection can read private field values.
- Reflection can modify private field values.
- Hibernate uses Reflection to map Java objects to database tables.
- Spring uses Reflection to discover beans and annotations.

---

# Next Part

Reflection Part 2 covers

- Method Reflection
- invoke()
- Constructor Reflection
- newInstance()
- How Spring creates Beans
```
# Reflection in Java - Part 2
*Library Management Project Notes*

---

# Table of Contents

1. Method Reflection
2. Method Metadata
3. invoke()
4. Why invoke() needs an Object
5. Static Methods
6. Constructor Reflection
7. newInstance()
8. How Spring Creates Beans
9. Complete Bean Creation Flow
10. Summary

---

# Method Reflection

Just like Reflection provides a `Field` object for fields, it also provides a
`Method` object for methods.

Suppose we have

```java
public class User {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void printHello() {
        System.out.println("Hello");
    }
}
```

Reflection gets all methods using

```java
Class<User> clazz = User.class;

Method[] methods = clazz.getDeclaredMethods();
```

Now Reflection knows

```text
getName()

setName()

printHello()
```

Notice

Reflection is **not executing** these methods.

It is only discovering that they exist.

---

# Getting a Particular Method

Suppose we want only

```java
getName()
```

Reflection can get it using

```java
Method method =
        clazz.getDeclaredMethod("getName");
```

Now the Method object represents

```text
getName()
```

only.

---

# Reading Method Metadata

Reflection can inspect the Method object.

### Method Name

```java
method.getName();
```

Result

```text
getName
```

---

### Return Type

```java
method.getReturnType();
```

Result

```text
String
```

---

### Parameter Types

```java
method.getParameterTypes();
```

Result

```text
No Parameters
```

or

```text
String
```

depending on the method.

---

# Method Metadata

A Method object stores

```text
Method

↓

Method Name

↓

Return Type

↓

Parameters

↓

Modifiers

↓

Annotations
```

Just like a Field object stores metadata about fields.

---

# invoke()

The most important method in Reflection is

```java
invoke()
```

Suppose

```java
User user = new User();

user.setName("Santhosh");
```

Reflection gets

```java
Method method =
        clazz.getDeclaredMethod("getName");
```

Now execute

```java
Object result =
        method.invoke(user);
```

Internally Java executes

```java
user.getName();
```

Result

```text
Santhosh
```

---

# Another Example

Suppose

```java
public void printHello() {

    System.out.println("Hello");

}
```

Reflection

```java
Method method =
        clazz.getDeclaredMethod("printHello");

method.invoke(user);
```

Output

```text
Hello
```

Reflection executed the method.

---

# Methods With Parameters

Suppose

```java
public void setName(String name){

    this.name = name;

}
```

Reflection

```java
Method method =
        clazz.getDeclaredMethod(
                "setName",
                String.class
        );
```

Execution

```java
method.invoke(user, "Santhosh");
```

Internally Java executes

```java
user.setName("Santhosh");
```

---

# Why invoke() Needs an Object

Suppose

```java
User user1 = new User();

user1.setName("Santhosh");

User user2 = new User();

user2.setName("Rahul");
```

Reflection has

```java
Method method =
        clazz.getDeclaredMethod("getName");
```

Question

If Reflection simply executes

```java
method.invoke();
```

Which object should Java execute?

```text
user1 ?

user2 ?
```

Java cannot know.

Therefore Reflection asks us to provide the target object.

```java
method.invoke(user1);
```

Equivalent to

```java
user1.getName();
```

Result

```text
Santhosh
```

---

```java
method.invoke(user2);
```

Equivalent to

```java
user2.getName();
```

Result

```text
Rahul
```

---

# Static Methods

Suppose

```java
public class MathUtil {

    public static int add(int a,int b){

        return a+b;

    }

}
```

Static methods belong to the class.

No object is required.

Reflection

```java
Method method =
        MathUtil.class.getDeclaredMethod(
                "add",
                int.class,
                int.class
        );

Object result =
        method.invoke(null,10,20);
```

Result

```text
30
```

Notice

```java
null
```

because static methods do not need an object.

---

# Constructor Reflection

Reflection can also inspect constructors.

Suppose

```java
public class User {

    public User(){

    }

}
```

Reflection gets the constructor

```java
Constructor<User> constructor =
        User.class.getDeclaredConstructor();
```

Now Reflection has a Constructor object.

---

# newInstance()

Normally Java creates an object using

```java
User user = new User();
```

Reflection creates the same object using

```java
User user =
        constructor.newInstance();
```

Both are equivalent.

---

# Constructor With Parameters

Suppose

```java
public class User {

    public User(String name){

    }

}
```

Reflection

```java
Constructor<User> constructor =
        User.class.getDeclaredConstructor(
                String.class
        );
```

Execution

```java
User user =
        constructor.newInstance("Santhosh");
```

Equivalent to

```java
new User("Santhosh");
```

---

# Why Spring Uses Constructor Reflection

Suppose Spring discovers

```java
@Service
public class UserService {

}
```

Spring cannot write

```java
new UserService();
```

because it has never seen your class before.

Instead Spring does conceptually

```java
Class<?> clazz =
        UserService.class;

Constructor<?> constructor =
        clazz.getDeclaredConstructor();

Object bean =
        constructor.newInstance();
```

Now Spring has created the bean dynamically.

---

# Constructor Injection

Suppose

```java
@Service
public class UserService {

    public UserService(
            UserRepository repository){

    }

}
```

Spring first creates

```text
UserRepository Bean
```

Then Reflection executes

```java
constructor.newInstance(repositoryBean);
```

Equivalent to

```java
new UserService(repositoryBean);
```

This is Constructor Injection.

---

# Complete Bean Creation Flow

Suppose

```java
@Service
public class UserService {

}
```

Conceptually Spring performs

```text
Application Starts

↓

Component Scan

↓

Find @Service

↓

Get Class Object

↓

Get Constructor

↓

constructor.newInstance()

↓

Bean Created

↓

Store Bean in IoC Container
```

Notice

No code like

```java
new UserService();
```

exists inside Spring.

Everything is done through Reflection.

---

# Reflection Objects

Reflection mainly revolves around four important objects.

```text
Class

↓

Field

↓

Method

↓

Constructor
```

Each object has a specific purpose.

| Reflection Object | Purpose |
|-------------------|---------|
| Class | Information about the class |
| Field | Read/Write fields |
| Method | Inspect and invoke methods |
| Constructor | Create objects dynamically |

---

# Summary

Method Reflection allows Java to inspect methods at runtime.

Important points

- Method objects represent methods.
- Method objects store metadata.
- `invoke()` executes a method dynamically.
- `invoke()` needs an object for instance methods.
- Static methods use `null`.
- Constructor Reflection creates objects dynamically.
- `newInstance()` is equivalent to using `new`.
- Spring creates beans using Constructor Reflection.

---

# Next Part

Reflection Part 3 covers

- Annotations
- Reading Annotations using Reflection
- How Spring finds `@Component`, `@Service`, `@Repository`
- How Hibernate finds `@Entity`
- Complete Spring Startup Flow
- Interview Questions
```
# Reflection in Java - Part 3
*Library Management Project Notes*

---

# Table of Contents

1. Reflection and Annotations
2. Reading Annotations
3. How Spring Finds Beans
4. How Dependency Injection Works
5. How Hibernate Uses Reflection
6. Reflection in Spring Data JPA
7. Reflection in AOP
8. Complete Spring Boot Startup Flow
9. Advantages & Disadvantages
10. Interview Questions
11. Final Summary

---

# Reflection and Annotations

Annotations are metadata added to classes, methods, fields and constructors.

Example

```java
@Service
public class UserService {

}
```

Question

How does Spring know that this class contains

```java
@Service
```

The answer is Reflection.

---

# Reading Annotations

Suppose

```java
@Service
public class UserService {

}
```

Reflection gets the Class object

```java
Class<?> clazz = UserService.class;
```

Now Reflection asks

```java
clazz.isAnnotationPresent(Service.class);
```

Result

```text
true
```

Now Spring knows

```text
This is a Service Bean.
```

---

Reflection can also get the annotation object

```java
Service service =
        clazz.getAnnotation(Service.class);
```

Spring now knows everything about the annotation.

---

# Reading All Annotations

Reflection

```java
Annotation[] annotations =
        clazz.getAnnotations();
```

Suppose

```java
@Service
@Transactional
public class UserService {

}
```

Reflection returns

```text
@Service

@Transactional
```

---

# Reading Field Annotations

Suppose

```java
@Entity
public class User {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String name;

}
```

Reflection

```java
Field[] fields =
        User.class.getDeclaredFields();
```

For every field

```java
field.getAnnotations();
```

Result

```text
@Id

@Column
```

Hibernate now understands

- Primary Key
- Column Mapping
- Database Metadata

---

# Reading Method Annotations

Suppose

```java
@Transactional
public void saveUser(){

}
```

Reflection

```java
Method method =
        clazz.getDeclaredMethod("saveUser");
```

Now

```java
method.isAnnotationPresent(
        Transactional.class
);
```

Result

```text
true
```

Spring now knows

```text
This method should run inside a transaction.
```

---

# How Spring Finds Beans

Suppose

```java
@Service
public class UserService {

}
```

Spring performs

## Step 1

Component Scan

Find every class.

↓

## Step 2

Reflection gets

```java
UserService.class
```

↓

## Step 3

Reflection checks

```java
clazz.isAnnotationPresent(
        Service.class
);
```

↓

Returns

```text
true
```

↓

## Step 4

Reflection gets constructor

```java
clazz.getDeclaredConstructor();
```

↓

## Step 5

Reflection creates object

```java
constructor.newInstance();
```

↓

Bean Created

↓

Stored inside IoC Container.

---

# How Dependency Injection Works

Suppose

```java
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

}
```

Spring already created

```text
UserRepository Bean
```

Now Reflection does

```java
Field[] fields =
        clazz.getDeclaredFields();
```

For each field

```java
field.isAnnotationPresent(
        Autowired.class
);
```

If true

↓

Reflection

```java
field.setAccessible(true);

field.set(userServiceBean,
          repositoryBean);
```

Equivalent to

```java
userService.repository =
        repositoryBean;
```

Dependency Injection completed.

---

# How Hibernate Uses Reflection

Suppose

```java
@Entity
public class User {

    @Id
    private Long id;

    private String name;

    private String email;

}
```

Hibernate Startup

## Step 1

Read

```java
User.class
```

↓

## Step 2

Read fields

```java
clazz.getDeclaredFields();
```

↓

Returns

```text
id

name

email
```

↓

## Step 3

Read annotations

```java
field.getAnnotations();
```

↓

Returns

```text
@Id

@Column
```

↓

## Step 4

Generate table metadata

```sql
users

id

name

email
```

No object exists yet.

Hibernate is only reading metadata.

---

# Saving an Entity

Suppose

```java
User user = new User();

user.setName("Santhosh");

user.setEmail("abc@gmail.com");
```

Application calls

```java
entityManager.persist(user);
```

Hibernate

↓

Reflection

```java
field.get(user);
```

Returns

```text
Santhosh
```

Reflection

```java
field.get(user);
```

Returns

```text
abc@gmail.com
```

Hibernate generates

```sql
INSERT INTO users(name,email)
VALUES('Santhosh',
       'abc@gmail.com');
```

---

# Loading an Entity

Database returns

```text
id = 1

name = Santhosh

email = abc@gmail.com
```

Hibernate

↓

Creates object

```java
constructor.newInstance();
```

↓

Uses Reflection

```java
field.set(user,1);

field.set(user,"Santhosh");

field.set(user,"abc@gmail.com");
```

Entity is ready.

No setters required.

---

# Reflection in Spring Data JPA

Suppose

```java
findByEmail(String email)
```

Spring Data JPA Proxy receives

```java
Method method
```

Reflection

```java
method.getName();
```

Returns

```text
findByEmail
```

Spring parses

```text
find

↓

By

↓

Email
```

Generates

```jpql
SELECT u
FROM User u
WHERE u.email = :email
```

Executes using EntityManager.

---

# Reflection in AOP

Suppose

```java
@Transactional
public void save(){

}
```

Spring Proxy intercepts

↓

Gets

```java
Method method
```

Reflection checks

```java
method.isAnnotationPresent(
        Transactional.class
);
```

↓

If true

```text
Start Transaction

↓

method.invoke(realObject)

↓

Commit

↓

Return
```

Reflection is the foundation of Spring AOP.

---

# Complete Spring Boot Startup Flow

```text
Application Starts

↓

Component Scan

↓

Reflection Finds Classes

↓

Check @Component

↓

Check @Service

↓

Check @Repository

↓

Check @Controller

↓

Constructor Reflection

↓

Create Objects

↓

Check Fields

↓

Find @Autowired

↓

Inject Dependencies

↓

Store Beans inside IoC Container

↓

Application Ready
```

---

# Where Reflection is Used

| Feature | Reflection Usage |
|----------|------------------|
| @Component | Bean Discovery |
| @Service | Bean Discovery |
| @Repository | Bean Discovery |
| @Controller | Bean Discovery |
| @Configuration | Configuration Processing |
| @Bean | Bean Creation |
| @Autowired | Dependency Injection |
| @Entity | Entity Mapping |
| @Id | Primary Key Detection |
| @Column | Column Mapping |
| Spring Data JPA | Repository Method Parsing |
| @Transactional | AOP Proxy |

---

# Advantages of Reflection

- Runtime inspection of classes.
- Dynamic object creation.
- Dynamic method execution.
- Read annotations.
- Read and modify private fields.
- Framework development becomes possible.
- Generic libraries can work with unknown classes.

---

# Disadvantages of Reflection

- Slower than normal Java.
- Breaks encapsulation using `setAccessible(true)`.
- Harder to debug.
- Less type safety.
- Should not be overused in business logic.

Frameworks use Reflection heavily.

Application code usually should not.

---

# Interview Questions

### Q1. What is Reflection?

Reflection is a Java feature that allows inspection and manipulation of classes, methods, fields, constructors and annotations during runtime.

---

### Q2. Why does Spring use Reflection?

Spring does not know application classes during development.

Reflection allows Spring to discover classes and create beans dynamically.

---

### Q3. Why does Hibernate use Reflection?

Hibernate uses Reflection to

- Read entity metadata.
- Read private fields.
- Write private fields.
- Map Java objects to database tables.

---

### Q4. Difference between Field and Field Value?

Field

```text
Metadata

↓

name

type

modifier
```

Field Value

```text
Actual Data

↓

Santhosh

abc@gmail.com
```

---

### Q5. Difference between Method object and invoke()?

Method object

```text
Metadata
```

invoke()

```text
Executes the method
```

---

### Q6. Why does invoke() need an object?

Instance methods belong to objects.

Reflection must know **which object's** method should execute.

Static methods use

```java
method.invoke(null);
```

---

### Q7. Difference between

```java
new User();
```

and

```java
constructor.newInstance();
```

Both create an object.

Reflection creates it dynamically during runtime.

---

### Q8. Does Reflection create tables?

No.

Reflection only provides metadata.

Hibernate uses that metadata to generate SQL.

---

# Final Summary

Reflection consists of four major objects

```text
Class

↓

Field

↓

Method

↓

Constructor
```

Reflection capabilities

- Read class metadata.
- Read field metadata.
- Read field values.
- Modify field values.
- Read method metadata.
- Execute methods using `invoke()`.
- Create objects using constructors.
- Read annotations.

Spring uses Reflection for

- Bean creation.
- Dependency Injection.
- AOP.
- Configuration processing.

Hibernate uses Reflection for

- Entity mapping.
- Reading annotations.
- Reading field values.
- Writing field values.
- Object creation.

Spring Data JPA uses Reflection for

- Parsing repository methods.
- Dynamic proxy implementation.
- JPQL generation.

---

# Reflection Complete ✅

You now understand enough Reflection to confidently explain how Spring Boot, Hibernate, Spring Data JPA, Dependency Injection, Dynamic Proxies and AOP work internally.