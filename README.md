# 🧩 Common Library

The Common Lib is **not a standalone microservice**. It is a shared dependency imported by other microservices in the CoderRide ecosystem.

## 🏗️ Architecture Role

```mermaid
graph TD
    Common[common-lib (.jar)]
    
    Auth[Auth Service] --> Common
    Member[Member Service] --> Common
    Role[Role Service] --> Common
    
    subgraph Contents
        DTO[Data Transfer Objects]
        EXC[Global Exceptions]
        UTIL[Utilities e.g., JwtUtil]
    end
    
    Common --- Contents
```

## 🔑 Key Responsibilities
- **Code Reusability**: Prevents duplicating standard classes (like `JwtUtil` or `ErrorResponse`) across 10 different microservices.
- **Consistency**: Ensures all services handle exceptions and API responses in the exact same format using `GlobalExceptionHandler`.
- **Inter-service Communication**: Houses the DTOs used when services talk to each other.

## ⚙️ Usage
Since this is a library, it doesn't run on a port. It is built using Maven and included in the `pom.xml` of other services like this:

```xml
<dependency>
    <groupId>com.codingclub</groupId>
    <artifactId>common-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot Dependencies** (Web, Security context)
