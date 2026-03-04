# Local DevOps Production Platform

## 1. Project Overview

The **Local DevOps Production Platform** simulates a production-grade payment processing backend system built using modern DevOps and cloud-native practices.

The platform is designed to:

* Run entirely in a local development environment
* Reflect real-world production architecture patterns
* Demonstrate layered backend design
* Simulate infrastructure automation workflows

The system evolves progressively across development stages:

* H2 in-memory database for local validation
* PostgreSQL for containerized and Kubernetes deployment
* Docker-based containerization
* Multi-container orchestration using Docker Compose
* Kubernetes deployment using Kind
* CI/CD automation using GitHub Actions
* Security scanning using Trivy
* Monitoring and observability using Prometheus and Grafana

The objective of this project is to simulate production-level DevOps workflows, backend system design, lifecycle enforcement, and infrastructure management within a controlled local environment.

## 2. Architecture

The platform follows a layered architecture that mirrors a real-world payment processing system.

The design separates business logic, persistence, infrastructure, and observability concerns.

### 2.1 Core Components

#### Payment Processing API (Spring Boot)

* Handles payment creation and lifecycle management.
* Enforces controlled state transitions.
* Records audit history of all lifecycle changes.
* Exposes REST endpoints for external systems.
* Provides monitoring endpoints via Actuator.

### Database Layer

#### Local Development

* **H2 in-memory database**
* Used for fast iteration and local validation
* Enables JPA auto-configuration without external dependencies

#### Containerized & Kubernetes Environments

* **PostgreSQL**
* Provides ACID compliance
* Enforces relational integrity
* Supports production-grade persistence requirements

#### Docker

* Containerizes the Spring Boot application.
* Ensures consistent runtime environments.
* Eliminates “works on my machine” issues.

#### Docker Compose

* Orchestrates multi-container setup locally.
* Runs application and PostgreSQL together.
* Manages internal container networking.

#### Kubernetes (Kind)

* Simulates a production Kubernetes cluster locally.
* Manages deployments, services, scaling, and configuration.
* Enables infrastructure-as-code workflows.

#### CI/CD Pipeline (GitHub Actions)

* Automates build and test processes.
* Builds container images.
* Integrates vulnerability scanning.
* Simulates real DevOps pipeline workflows.

#### Monitoring Stack (Prometheus & Grafana)

* Collects application metrics.
* Visualizes operational data.
* Provides observability for performance and reliability.

### 2.2 High-Level System Flow

1. A merchant system sends a payment request to the API.
2. The API creates a payment with initial status `PENDING`.
3. The service transitions the payment to `PROCESSING`.
4. Business logic determines final status:

   * `SUCCESS` if validation passes
   * `FAILED` otherwise
5. Each transition is recorded in a history table.
6. The final payment status is returned to the caller.
7. Monitoring endpoints expose health and metrics data.

This flow demonstrates lifecycle enforcement, auditability, and clean separation of responsibilities.

## 3. Technology Stack

The platform leverages modern backend and DevOps technologies to simulate a production-grade system lifecycle.

### Backend

* **Spring Boot** – REST API development and application framework
* **Spring Data JPA** – ORM layer for relational persistence
* **H2 Database** – In-memory database for local development
* **PostgreSQL** – Production-grade relational database (Docker & Kubernetes stages)
* **Spring Boot Actuator** – Health and metrics endpoints
* **Lombok** – Boilerplate code reduction

### Containerization

* **Docker** – Application containerization
* **Docker Compose** – Multi-container orchestration for local environments

### Orchestration

* **Kubernetes (Kind)** – Local cluster simulation for deployment management

### CI/CD

* **GitHub Actions** – Automated build, test, and container workflows

### Security

* **Trivy** – Container vulnerability scanning

### Monitoring & Observability

* **Spring Boot Actuator** – Application metrics exposure
* **Prometheus** – Metrics collection
* **Grafana** – Metrics visualization

### Testing

* **JUnit** – Unit testing framework
* **Mockito** – Mocking framework for isolation testing

## 4. Repository Structure

The project is structured as a single monorepo containing application code, infrastructure configuration, and operational tooling.

```
Local-DevOps-Production-Platform/
│
├── app/                     # Spring Boot application source code
│
├── docker/                  # Docker and Docker Compose configuration
│
├── k8s/                     # Kubernetes manifests (Kind deployment)
│
├── monitoring/              # Prometheus and Grafana configuration
│
├── .github/
│   └── workflows/           # CI/CD pipelines (GitHub Actions)
│
├── docs/                    # Architecture diagrams and supporting documentation
│
└── README.md                # Project documentation
```

### Structure Philosophy

The repository structure reflects separation of concerns:

- Application logic is isolated under `app/`.
- Container and runtime configuration are separated from source code.
- Kubernetes manifests are maintained independently of application logic.
- CI/CD automation is version-controlled alongside the application.
- Monitoring configuration is modular and extensible.

## 5. Prerequisites

Before running this project, ensure the following tools are installed:

### Required Software

- Java 17 or later
- Maven 3.9+
- Docker
- Docker Compose
- Git
- kubectl (Kubernetes CLI)
- Kind (Kubernetes in Docker)

### Recommended Environment

- Ubuntu 22.04+ (or compatible Linux distribution)
- Minimum 8GB RAM for Kubernetes and monitoring stack

## 6. Application Design

This section describes the domain model, relational structure, lifecycle behavior, API surface, and architectural decisions that define the Payment Processing Simulation.

The design reflects production-grade backend principles including separation of concerns, auditability, lifecycle enforcement, and relational integrity.

### 6.1 Domain Model

The core domain entity of the platform is **Payment**.

A payment represents a financial transaction request initiated by a merchant system and processed by the payment service.

#### Payment Entity

| Field      | Type                 | Description                                       |
| ---------- | -------------------- | ------------------------------------------------- |
| id         | UUID                 | Unique identifier for the payment                 |
| amount     | BigDecimal           | Monetary value of the transaction                 |
| currency   | String               | ISO currency code (e.g., USD, EUR)                |
| reference  | String               | External merchant reference                       |
| customerId | String               | Identifier of the customer initiating the payment |
| status     | PaymentStatus (Enum) | Current lifecycle state                           |
| createdAt  | Timestamp            | Creation timestamp                                |
| updatedAt  | Timestamp            | Last modification timestamp                       |

#### PaymentStatus Enum

The payment lifecycle is controlled using a strongly typed enumeration:

* PENDING
* PROCESSING
* SUCCESS
* FAILED

Using an enum ensures lifecycle states remain constrained and predictable.

#### PaymentStatusHistory Entity

To preserve auditability and traceability, every status transition is recorded in a separate entity.

| Field     | Type          | Description                          |
| --------- | ------------- | ------------------------------------ |
| id        | UUID          | Unique identifier for history record |
| payment   | Payment       | Associated payment (Many-to-One)     |
| oldStatus | PaymentStatus | Previous lifecycle state             |
| newStatus | PaymentStatus | Updated lifecycle state              |
| changedAt | Timestamp     | Transition timestamp                 |

This structure enables a full audit trail of lifecycle transitions.

### 6.2 Database Schema

The relational schema enforces normalization and referential integrity.

#### payments Table

* id (UUID, Primary Key)
* amount (DECIMAL)
* currency (VARCHAR)
* reference (VARCHAR, UNIQUE)
* customer_id (VARCHAR)
* status (VARCHAR)
* created_at (TIMESTAMP)
* updated_at (TIMESTAMP)

#### payment_status_history Table

* id (UUID, Primary Key)
* payment_id (UUID, Foreign Key referencing payments.id)
* old_status (VARCHAR)
* new_status (VARCHAR)
* changed_at (TIMESTAMP)

#### Relationship Design

* One Payment can have many PaymentStatusHistory records.
* Foreign key constraints ensure referential integrity.
* Status transitions are stored as immutable records.
* Payment records themselves are not deleted.

### 6.3 Payment Lifecycle

The payment lifecycle represents controlled state transitions from creation to final resolution.

#### Lifecycle Flow

1. Merchant sends a payment request.
2. Payment is created with status `PENDING`.
3. Payment transitions to `PROCESSING`.
4. Business rule simulation determines outcome:

   * If amount > 0 → `SUCCESS`
   * Otherwise → `FAILED`
5. Each transition is recorded in `payment_status_history`.

#### State Transition Rules

* `PENDING` → `PROCESSING`
* `PROCESSING` → `SUCCESS`
* `PROCESSING` → `FAILED`

Invalid transitions are not permitted and are enforced in the Service layer.

### 6.4 API Contract

The payment service exposes RESTful endpoints for interaction with external systems.

The API follows standard JSON-based request and response patterns typical of production-grade backend services.

#### Create Payment

`POST /payments`

Creates a new payment and initiates lifecycle processing.

#### Request (JSON Body)

```http
POST /payments
Content-Type: application/json
```

```json
{
  "amount": 100,
  "currency": "USD",
  "reference": "ORDER-12345",
  "customerId": "CUST-001"
}
```

#### Request Fields

| Field      | Type       | Description                           |
| ---------- | ---------- | ------------------------------------- |
| amount     | BigDecimal | Transaction monetary value            |
| currency   | String     | ISO currency code                     |
| reference  | String     | Merchant-provided reference           |
| customerId | String     | Identifier of the initiating customer |

> The request payload is mapped to a dedicated DTO (`CreatePaymentRequest`) to decouple the API contract from the persistence entity.

#### Response

**200 OK**

```json
{
  "id": "UUID",
  "amount": 100,
  "currency": "USD",
  "reference": "ORDER-12345",
  "customerId": "CUST-001",
  "status": "SUCCESS",
  "createdAt": "2026-03-02T19:40:11.712588674",
  "updatedAt": "2026-03-02T19:40:11.805804354"
}
```

#### Lifecycle Behavior

Upon receiving a valid request:

1. A payment is created with status `PENDING`.
2. The payment transitions to `PROCESSING`.
3. Based on business rules, it transitions to either:

   * `SUCCESS`
   * `FAILED`
4. Each transition is recorded in `payment_status_history`.

#### Health Check

`GET /actuator/health`

Confirms application availability and monitoring readiness.

![Health Verification](./docs/Images/2.health_check.png)

## 6.5 Design Decisions

Several architectural decisions were made to simulate production-grade system behavior.

#### UUID as Primary Key

Ensures global uniqueness and avoids predictable sequential identifiers.

#### Separate Status History Table

Maintains:

* Auditability
* Traceability
* Normalized relational design
* Clear lifecycle transparency

#### Service Layer Lifecycle Enforcement

Business rules and state transitions are enforced within the Service layer to maintain separation of concerns and prevent invalid status changes.

#### Immutable Payment Records

Payments are not deleted.
Lifecycle changes are recorded via state transitions rather than record mutation or removal.

#### Database Strategy

* H2 is used for local development validation.
* PostgreSQL is the intended production database in containerized deployment.

This ensures development flexibility while preserving production realism.

## 7. Local Development Setup

This section describes how the application is built, executed, and validated locally before introducing containerized infrastructure.

The purpose of this stage is to validate:

* Application compilation
* Dependency resolution
* JPA auto-configuration
* Entity mapping
* Repository initialization
* Service-layer lifecycle enforcement
* Database persistence
* Health monitoring endpoints

This ensures the application is functionally stable before Dockerization.

### 7.1 Initialize Spring Boot Application

The Spring Boot application was generated using Spring Initializr and placed inside the `app/` directory.

#### Project Metadata

* Group: `com.localdevops`
* Artifact: `payment-service`
* Packaging: `jar`
* Java Version: 17

#### Core Dependencies

* Spring Boot Starter Web
* Spring Boot Starter Data JPA
* H2 Database (runtime)
* Spring Boot Actuator
* Lombok
* Spring Boot Starter Test

> H2 is used for local development validation. PostgreSQL will be introduced in the Docker Compose stage to simulate a production-grade database environment.

### 7.2 H2 Local Database Configuration

To enable local persistence without requiring external infrastructure, an in-memory H2 database is configured.

#### application.properties

```properties
spring.application.name=payment-service

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update

spring.h2.console.enabled=true
```

#### Why H2 Is Used

* Enables automatic JPA configuration
* Allows repository beans to initialize
* Automatically generates schema from entities
* Removes dependency on external database setup
* Speeds up local development cycles

This follows incremental development principles.

#### 7.3 Build the Application

Navigate to the application directory:

```bash
cd app/payment-service
```

Build the project:

```bash
./mvnw clean install
```

This step:

* Compiles source code
* Validates entity mappings
* Ensures dependency resolution
* Produces an executable JAR

Expected output:

```
BUILD SUCCESS
```

#### 7.4 Run the Application

Start the application:

```bash
./mvnw spring-boot:run
```

Successful startup logs should include:

```
Tomcat started on port 8080
Started PaymentServiceApplication
```

This confirms:

* Embedded Tomcat initialized
* Application context loaded successfully
* JPA repositories registered
* H2 datasource configured properly

![spring-boot](./docs/Images/1.Spring_boot_run.png)

### 7.5 Actuator Health Verification

Spring Boot Actuator is enabled for runtime monitoring.

Verify application health:

```
http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

This confirms:

* The application is responsive
* Monitoring endpoints are active
* Application context is fully initialized

![Health Verification](./docs/Images/2.health_check.png)

### 7.6 Payment Lifecycle Validation

To validate the service-layer business logic and lifecycle enforcement, a test payment is created using a JSON request body.

#### Create Payment (JSON Request)

```bash
curl -X POST http://localhost:8080/payments \
-H "Content-Type: application/json" \
-d '{"amount":100,"currency":"USD","reference":"ORDER-LOCAL-1","customerId":"CUST-LOCAL-1"}'
```

#### Expected Response

```json
{
  "id": "UUID",
  "amount": 100,
  "currency": "USD",
  "reference": "ORDER-LOCAL-1",
  "customerId": "CUST-LOCAL-1",
  "status": "SUCCESS",
  "createdAt": "...",
  "updatedAt": "..."
}
```

#### Lifecycle Behavior

When the request is processed:

1. A Payment entity is created with status `PENDING`
2. The service transitions the payment to `PROCESSING`
3. Based on validation rules:

   * If amount > 0 → status becomes `SUCCESS`
   * Otherwise → status becomes `FAILED`
4. Each transition is recorded in `payment_status_history`

This confirms correct service-layer lifecycle enforcement.

### 7.7 Database Verification Using H2 Console

Access the H2 console:

```
http://localhost:8080/h2-console
```

Connection details:

* JDBC URL: `jdbc:h2:mem:testdb`
* Username: `sa`
* Password: (empty)

![Access H2 console](./docs/Images/4.Access%20H2%20console.png)

#### Verify Payment Record

```sql
SELECT * FROM PAYMENTS;
```

#### Verify Status History

```sql
SELECT old_status, new_status, changed_at
FROM PAYMENT_STATUS_HISTORY;
```

Expected result:

* One record in `PAYMENTS`
* Two records in `PAYMENT_STATUS_HISTORY`

  * PENDING → PROCESSING
  * PROCESSING → SUCCESS

![PAYMENT\_STATUS\_HISTORY](./docs/Images/5.PAYMENT_STATUS_HISTORY.png)

This confirms:

* One-to-many relationship integrity
* UUID primary key generation
* Proper foreign key mapping
* Service transition logic execution
* Audit trail preservation

### 7.8 Development Validation Summary

At the conclusion of local validation, the system supports:

* REST API interaction via JSON request body
* Layered architecture (Controller → Service → Repository)
* Automatic schema generation via JPA
* In-memory database persistence
* Lifecycle transition enforcement
* Audit history tracking
* Runtime health monitoring

This completes functional validation prior to containerization.

## 8. Dockerization and Containerized Database

After validating the application locally using an in-memory H2 database, the next step is to package the service into containers and introduce a production-style database environment.

This stage introduces:

- Application containerization
- PostgreSQL containerized database
- Docker networking
- Persistent database storage
- Environment-based configuration
- Multi-container orchestration using Docker Compose

This simulates how modern backend systems are deployed in production environments.

### 8.1 Application Containerization

The Spring Boot application is containerized using Docker to ensure consistent execution across different environments.

A **multi-stage build** is used to optimize the final image size.

#### Dockerfile Location

```
app/payment-service/Dockerfile
```

#### Dockerfile

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /build/target/payment-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

#### Why Multi-Stage Build Is Used

Multi-stage builds separate the **build environment** from the **runtime environment**.

Benefits include:

- Smaller final image size
- Improved security
- Reduced attack surface
- Faster container startup

### 8.2 Docker Compose Architecture

Docker Compose is used to orchestrate multiple containers required for the platform.

The system consists of two services:

1. **payment-service** — Spring Boot application
2. **postgres-db** — PostgreSQL database

#### docker-compose.yml Location

```
docker-compose.yml
```

#### docker-compose.yml

```yaml
version: '3.9'

services:

  postgres-db:
    image: postgres:15
    container_name: postgres-db
    environment:
      POSTGRES_DB: paymentdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  payment-service:
    build:
      context: ./app/payment-service
    container_name: payment-service
    ports:
      - "8080:8080"
    depends_on:
      postgres-db:
        condition: service_healthy
    environment:
      DB_URL: jdbc:postgresql://postgres-db:5432/paymentdb
      DB_USERNAME: postgres
      DB_PASSWORD: postgres

volumes:
  postgres_data:
```

### 8.3 Container Networking

Docker Compose automatically creates an isolated network for all services.

This allows containers to communicate using **service names as hostnames**.

Example:

```
jdbc:postgresql://postgres-db:5432/paymentdb
```

Here:

- `postgres-db` is the service name
- Docker resolves it to the correct container IP address

This removes the need for manual network configuration.

### 8.4 Database Persistence with Volumes

PostgreSQL stores database files inside the container path:

```
/var/lib/postgresql/data
```

A Docker **named volume** is mounted to preserve this data.

```
volumes:
  postgres_data:
```

This ensures:

- Data survives container restarts
- Database state is preserved
- Application data is not lost

Without volumes, containers would lose all data when restarted.

### 8.5 Environment-Based Configuration

The application uses environment variables for database configuration.

Inside `application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Docker Compose injects these variables at runtime.

This approach follows **12-Factor App principles** and enables:

- Environment portability
- Secure configuration management
- Separation of config from code

### 8.6 Running the Multi-Container System

From the project root directory:

```bash
docker compose up --build
```

This command will:

1. Build the Spring Boot container image
2. Start the PostgreSQL container
3. Wait for database readiness
4. Start the application container
5. Establish container networking

Successful startup logs will show:

```
Tomcat started on port 8080
Started PaymentServiceApplication
```

### 8.7 API Validation in Containerized Environment

Once containers are running, the API can be tested from the host machine.

#### Create Payment

```bash
curl -X POST http://localhost:8080/payments \
-H "Content-Type: application/json" \
-d '{"amount":200,"currency":"USD","reference":"DOCKER-1","customerId":"CUST-DOCKER"}'
```

![CREATE PAYMENT](./docs/Images/6.Create_Payment_json.png)

Response:

```json
{
  "id": "6bf524cc-6d9f-4153-8bc8-d1aea3b2fa20",
  "amount": 200,
  "currency": "USD",
  "reference": "DOCKER-1",
  "customerId": "CUST-DOCKER",
  "status": "SUCCESS",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### 8.8 PostgreSQL Data Verification

To verify that data is stored inside PostgreSQL:

Enter the database container:

```bash
docker exec -it postgres-db psql -U postgres -d paymentdb
```

Query the payments table:

```sql
SELECT * FROM payments;
```

![PAYMENT STATUS](./docs/Images/7.payments_json.png)


Query the status history:

```sql
SELECT * FROM payment_status_history;
```

![PAYMENT_STATUS_HISTORY](./docs/Images/8.payment_status_history.json.png)

This confirms:

- PostgreSQL persistence works
- Application successfully writes to the containerized database
- Payment lifecycle history is correctly stored

### 8.9 Containerization Validation Summary

At the end of this stage, the system supports:

- Containerized Spring Boot application
- Containerized PostgreSQL database
- Docker Compose orchestration
- Service networking
- Health-based startup ordering
- Persistent database storage
- Environment-based configuration

This establishes a production-style foundation before introducing Kubernetes deployment.