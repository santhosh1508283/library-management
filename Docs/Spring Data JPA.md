# Spring Data JPA Internals (How It Really Works)

## Why Spring Data JPA?

Without Spring Data JPA, we write a lot of boilerplate code.

Example:

```java
entityManager.persist(user);

entityManager.find(User.class, id);

entityManager.createQuery(...);

entityManager.remove(user);
```

Spring Data JPA reduces all of this into simple repository methods.

```java
repository.save(user);

repository.findById(id);

repository.findByEmail(email);

repository.delete(user);
```

The question is:

**How does this work when we never implement UserRepository?**

---

# Step 1: Repository Interface

```java
public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
```

Notice:

- We only create an interface.
- We never create `UserRepositoryImpl`.

---

# Step 2: Spring Starts

When Spring Boot starts, it scans the project.

When Spring finds

```java
interface UserRepository extends JpaRepository
```

it understands:

- This is a Spring Data JPA repository.
- There is no implementation.
- I must create one dynamically.

---

# Step 3: Dynamic Proxy

Spring uses Java Dynamic Proxy.

Conceptually Java generates something like:

```java
class UserRepositoryProxy implements UserRepository {

    @Override
    public User findByEmail(String email){

        return handler.invoke(
                this,
                method,
                new Object[]{email}
        );

    }

}
```

Notice:

The proxy implements our interface.

We never write this class.

Java creates it at runtime.

---

# Step 4: IoC Container

Spring stores

```
Key
-----------------------------
UserRepository.class

↓

Value

Generated Proxy Object
```

So when we write

```java
@Autowired
UserRepository repository;
```

Spring injects

```
Generated Proxy
```

NOT a real implementation.

---

# Step 5: Calling a Method

When we write

```java
repository.findByEmail(email);
```

Flow:

```
Our Code

↓

Generated Proxy

↓

Spring InvocationHandler.invoke()

↓

Spring decides what to do
```

Notice:

The interface method itself has no implementation.

The proxy intercepts the call.

---

# Step 6: What invoke() Does

Spring's InvocationHandler receives

```
Method = findByEmail

Arguments = [email]
```

Now Spring checks

```
Is this CRUD?

↓

No

↓

Parse Method Name

↓

Generate JPQL

↓

EntityManager.createQuery()

↓

Hibernate

↓

JDBC

↓

Database
```

---

# CRUD Methods

Suppose we call

```java
repository.save(user);
```

Flow:

```
save()

↓

Generated Proxy

↓

Spring InvocationHandler

↓

Is this CRUD?

↓

Yes

↓

SimpleJpaRepository.save(user)

↓

EntityManager.persist() / merge()

↓

Hibernate

↓

JDBC

↓

Database
```

---

# Why Do We Need SimpleJpaRepository?

Question:

Why doesn't the proxy generate everything?

Answer:

CRUD is much more than generating SQL.

Example:

save()

requires

- persist() or merge()
- dirty checking
- managed vs detached entity
- flushing
- cascading
- optimistic locking
- lifecycle callbacks

All of this logic already exists inside Hibernate.

Spring simply delegates CRUD to SimpleJpaRepository.

---

# Why Proxy Exists

Proxy has one responsibility:

```
Receive Method Call

↓

Understand Method

↓

Choose Strategy

↓

Forward
```

Example:

```
save()

↓

SimpleJpaRepository
```

```
findByEmail()

↓

Generate JPQL
```

```
@Query

↓

Execute Custom Query
```

---

# Reflection

Reflection is NOT Dynamic Proxy.

Dynamic Proxy USES Reflection.

Reflection allows Java to inspect classes, methods, fields and annotations at runtime.

---

## Reflection in Hibernate

Hibernate uses Reflection during application startup.

Example:

```java
@Entity
public class User {

    @Id
    Long id;

    String name;

}
```

Hibernate asks

```
Fields?

↓

id

name
```

Then

```
Annotations?

↓

@Entity

@Id
```

Then Hibernate generates

```
CREATE TABLE users(...)
```

Reflection here is only reading metadata.

---

## Reflection in Dynamic Proxy

When we call

```java
repository.findByEmail(email);
```

Java passes a Reflection object called

```java
Method
```

This object contains

```
Method Name

↓

findByEmail

Parameters

↓

String

Return Type

↓

User
```

Spring reads

```java
method.getName()
```

and decides what to do.

---

# invoke()

Suppose

```java
Calculator calculator = new Calculator();
```

Normally

```java
calculator.add(10,20);
```

Reflection allows

```java
method.invoke(calculator, 10, 20);
```

Read it as

> Execute this method on this object using these arguments.

---

# Why Not Always Use Reflection?

If we already know

```java
calculator.add();
```

Reflection is unnecessary.

Normal Java is faster.

Reflection exists because frameworks do NOT know our methods when they are written.

Example:

Spring developers never knew we would create

```java
findByEmail()

findByName()

findByRole()
```

Reflection allows Spring to work with methods discovered at runtime.

---

# EntityManager vs Hibernate

Many developers think

```
EntityManager = Hibernate
```

This is incorrect.

EntityManager is a JPA interface.

Hibernate provides the implementation.

Exactly like

```
List

↓

ArrayList
```

Similarly

```
EntityManager

↓

Hibernate Session / EntityManager Implementation
```

---

# Complete Flow

```
Application Starts

↓

Spring scans repository interface

↓

Spring creates Dynamic Proxy

↓

Proxy stored in IoC Container

↓

@Autowired injects Proxy

↓

repository.findByEmail()

↓

Proxy

↓

InvocationHandler.invoke()

↓

Parse Method

↓

Generate JPQL

↓

EntityManager

↓

Hibernate

↓

JDBC

↓

Database
```

---

# Important Takeaways

- We never implement UserRepository.
- Spring creates a Dynamic Proxy.
- Proxy intercepts every repository method.
- CRUD methods go to SimpleJpaRepository.
- Derived query methods generate JPQL.
- Reflection allows Spring to inspect methods.
- EntityManager is an interface.
- Hibernate implements EntityManager.
- Hibernate converts JPQL into SQL.
- JDBC executes SQL on the database.