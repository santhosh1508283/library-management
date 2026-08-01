# 📖 01 - JDBC (Java Database Connectivity)

> Project: **Library Management System**
>
> Learning Goal: Understand how Java applications communicate with a database **without Hibernate or Spring Data JPA**.

---

# Why does JDBC exist?

Imagine Java wants to talk to MySQL.

Java doesn't know:

* How MySQL understands SQL
* How to open a TCP connection
* How to send packets
* How to receive results

Every database has its own protocol.

For example:

* MySQL
* PostgreSQL
* Oracle
* SQL Server

If Java had to learn every protocol separately, every application would become database-specific.

JDBC solves this problem.

It provides **one standard API**.

Java applications only learn JDBC.

Database vendors provide JDBC Drivers that understand their own database.

---

# JDBC Architecture

```
Application
      │
      ▼
JDBC API
      │
      ▼
DriverManager
      │
      ▼
MySQL JDBC Driver
      │
      ▼
TCP/IP
      │
      ▼
MySQL Server
```

---

# Components

## 1. JDBC API

JDBC is a collection of Java interfaces and classes.

Examples:

* Connection
* Statement
* PreparedStatement
* ResultSet
* DriverManager

These are **not** specific to MySQL.

They define a common contract.

---

## 2. DriverManager

`DriverManager` is part of JDBC.

Its job is:

* Find the correct database driver
* Ask that driver to create a database connection

Example:

```java
Connection connection =
    DriverManager.getConnection(url, username, password);
```

It does **not** connect to MySQL itself.

It delegates the work to the registered MySQL Driver.

---

## 3. MySQL JDBC Driver

This is the actual implementation.

It knows:

* MySQL protocol
* Authentication
* Packet format
* SQL execution
* Reading responses

Without this driver, Java cannot communicate with MySQL.

---

# Connection

```
Connection connection =
    DriverManager.getConnection(...);
```

A Connection represents a **live session** with the database.

Think of it as a phone call.

```
Java -------------------- MySQL
        Connection
```

Using this connection we can:

* Execute SQL
* Commit transactions
* Rollback transactions

Once finished:

```java
connection.close();
```

The connection must be returned.

---

# Why close the Connection?

A connection is an expensive resource.

It consumes:

* Database memory
* Network socket
* Database thread

If not closed:

Eventually:

```
Maximum Connections Reached
```

The database refuses new users.

---

# PreparedStatement

Example:

```java
String sql = """
INSERT INTO users(name,email)
VALUES(?,?)
""";

PreparedStatement statement =
    connection.prepareStatement(sql);
```

PreparedStatement represents a **precompiled SQL template**.

Notice:

```
VALUES(?,?)
```

The SQL structure is fixed.

Only values change.

---

# Why not concatenate Strings?

Unsafe:

```java
String sql =
"SELECT * FROM users WHERE email='"
+ email + "'";
```

If a user enters:

```
abc@gmail.com' OR '1'='1
```

The final SQL becomes:

```sql
SELECT * FROM users
WHERE email='abc@gmail.com'
OR '1'='1';
```

Everything matches.

This is SQL Injection.

---

# How PreparedStatement prevents SQL Injection

Template:

```sql
SELECT *
FROM users
WHERE email = ?
```

Later:

```java
statement.setString(1, email);
```

The driver sends:

```
SQL Template

+

Parameter List
```

Not one combined SQL string.

The database already knows:

```
Parameter #1

↓

String
```

Even if the value is:

```
abc@gmail.com' OR '1'='1
```

It is treated as plain text.

Not SQL.

---

# Executing Queries

For SELECT:

```java
ResultSet rs =
    statement.executeQuery();
```

Returns rows.

---

For INSERT / UPDATE / DELETE:

```java
statement.executeUpdate();
```

Returns affected row count.

Example:

```
1
```

means one row inserted or updated.

---

# ResultSet

ResultSet stores rows returned by the database.

Example:

```
id  name
---------------
1   Santhosh
2   Rahul
```

Initially:

Cursor:

```
Before First Row
```

Move cursor:

```java
rs.next();
```

Now:

```
Row 1
```

Read:

```java
rs.getString("name");
```

---

Multiple rows:

```java
while(rs.next()){

}
```

Each `next()` moves to the next row.

When rows finish:

```
false
```

is returned.

---

# Closing Resources

Correct order:

```java
rs.close();

statement.close();

connection.close();
```

Why?

ResultSet depends on Statement.

Statement depends on Connection.

Always close children first.

---

# Try-With-Resources

Instead of manually writing:

```java
close();
```

Java provides:

```java
try(
    Connection connection = ...;
    PreparedStatement statement = ...;
    ResultSet rs = ...
){

}
```

Resources are automatically closed.

Even if an exception occurs.

---

# JDBC Request Lifecycle

```
Application

↓

DriverManager.getConnection()

↓

MySQL Driver

↓

TCP Connection

↓

Connection Created

↓

prepareStatement()

↓

setString()

↓

executeQuery()

↓

ResultSet

↓

Read Data

↓

close(ResultSet)

↓

close(PreparedStatement)

↓

close(Connection)
```

---

# Limitations of JDBC

JDBC works well.

But we manually write:

* SQL
* Mapping
* Connections
* Closing resources

Example:

```java
User user = new User();

user.setName(rs.getString("name"));
user.setEmail(rs.getString("email"));
```

Imagine doing this for 100 tables.

It becomes repetitive.

This is exactly why **Hibernate** was created.

Hibernate sits **on top of JDBC** and automates most of this work.

---

# Key Takeaways

* JDBC is Java's standard database API.
* DriverManager chooses the correct JDBC Driver.
* The JDBC Driver talks to the database using its native protocol.
* Connection represents a database session.
* PreparedStatement prevents SQL Injection by separating SQL structure from values.
* ResultSet stores rows returned from the database.
* Always close ResultSet → Statement → Connection.
* Hibernate still uses JDBC internally—it simply hides most of this code from us.

---

# Interview Questions

### Q1. Why does JDBC exist?

To provide a standard Java API for communicating with different databases.

---

### Q2. Is DriverManager part of JDBC?

Yes. DriverManager belongs to the JDBC API. It delegates connection creation to the appropriate database driver.

---

### Q3. Why is PreparedStatement preferred over Statement?

* Prevents SQL Injection
* Allows query precompilation
* Better performance for repeated execution

---

### Q4. Does executeUpdate() create a row automatically?

No. It executes whatever SQL statement you wrote (INSERT, UPDATE, DELETE). The SQL determines the operation.

---

### Q5. Why must we close a Connection?

Connections are expensive resources. Closing them releases or returns them (to the pool in modern applications), preventing resource leaks.
