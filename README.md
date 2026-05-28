# BankingAppCRUD

A RESTful banking application built with Spring Boot 3, Hibernate 6, and PostgreSQL. The application follows Domain-Driven Design (DDD) principles and provides core banking functionality including user management, account creation, and transaction handling.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Security](#security)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3 |
| ORM | Hibernate 6 |
| Database | PostgreSQL |
| Reactive Client | Spring WebFlux (WebClient) |
| Mapping | ModelMapper with RecordModule |
| Documentation | SpringDoc OpenAPI (Swagger) |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |

---

## Architecture

The application follows a layered DDD architecture:

```
├── Application
│   ├── DTOs              # Data Transfer Objects (records)
│   └── Mappers           # Entity ↔ DTO mapping logic
│
├── Domain
│   ├── Entity            # JPA entities (User, Account, Transaction)
│   ├── ValueObject       # Immutable value objects (Money, Name, Rate, AccountInfo)
│   └── HibernateInstantiator  # Custom Hibernate instantiators for final field VOs
│
└── Infrastructure
    ├── Config            # Spring beans, ModelMapper, WebClient
    ├── Repository        # JPA repositories
    └── ExceptionHandler  # Global exception handling
```

---

## Domain Model

### User
Represents a bank customer. A user can hold multiple bank accounts and has an associated name, contact details, roles, and account status.

### Account
Abstract base entity using `TABLE_PER_CLASS` inheritance strategy. Each account type has its own dedicated table containing all shared and specific fields.

**CheckingAccount** — includes overdraft limit, daily transaction limit, monthly fee, and debit card information.

**SavingAccount** — includes interest rate, accrued interest, minimum balance, and compound frequency.

### Value Objects
All value objects are immutable (`final` fields) and use custom `EmbeddableInstantiator` implementations to satisfy Hibernate's instantiation requirements without compromising DDD design:

- `Name` — first name, last name
- `Money` — amount, currency
- `Rate` — country, rate info, last updated
- `AccountInfo` — account number, sort code
- `DebitInfo` — card number, PIN, issue date, expiry date

---

## Features

- User registration and management with soft delete support
- Checking and saving account creation per user
- Real-time UK interest rate fetching via API Ninjas
- Global exception handling with appropriate HTTP status codes
- Soft delete pattern across all entities via `@Where(clause = "deleted = false")`
- Correlation ID support for request tracing
- OpenAPI documentation via Swagger UI

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- Maven

### Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/BankingAppCRUD.git
cd BankingAppCRUD
```

2. Create a PostgreSQL database:
```sql
CREATE DATABASE banking_db;
```

3. Configure `application.properties` (see [Configuration](#configuration))

4. Run the application:
```bash
mvn spring-boot:run
```

5. Access Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### User

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users/register` | Register a new user |
| `GET` | `/api/users/{userId}` | Get user by ID |
| `DELETE` | `/api/users/{userId}` | Soft delete a user |

### Account

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/accounts/create-account/{userId}` | Create a checking or saving account for a user |
| `GET` | `/api/accounts/{accountId}` | Get account by ID |

### Request Headers

| Header | Required | Description |
|---|---|---|
| `X-Correlation-ID` | No | Optional request tracing ID |

---

## Configuration

Create an `application.properties` file with the following:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type=UUID

# API Ninjas (Interest Rate)
api.ninjas.key=${API_NINJAS_KEY}

# Logging
logging.level.org.hibernate.SQL=DEBUG
```

Set environment variables for sensitive values:

```bash
export API_NINJAS_KEY=your_api_ninjas_key_here
```

---

## Security

- Passwords are stored as hashed values — never in plain text
- Debit card numbers and PINs are stored as hashed values
- API keys are injected via environment variables and never hardcoded
- Role-based access control is implemented via Spring Security (`USER`, `ADMIN`)
- Soft delete ensures user data is never permanently removed without authorisation

---

## Known Limitations

- Transaction management is under active development
- Multi-currency support is planned but not yet implemented
- Account-to-account transfers are not yet available

---

## License

This project is for educational purposes.
