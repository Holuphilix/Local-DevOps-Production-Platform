# Local DevOps Production Platform

## 1. Project Overview

This project simulates a production-grade payment processing backend system built using modern DevOps practices. The platform is designed to run entirely in a local environment while reflecting real-world production architecture patterns.

The system includes:

- A Spring Boot payment processing API
- PostgreSQL database for persistence
- Containerization using Docker
- Multi-container orchestration using Docker Compose
- Kubernetes deployment using Kind
- CI/CD automation with GitHub Actions
- Security scanning with Trivy
- Monitoring and observability using Prometheus and Grafana

The objective of this project is to simulate production-level DevOps workflows, system design practices, and infrastructure management in a controlled local environment.

## 2. Architecture

The platform follows a layered architecture designed to simulate a production payment processing system.

### Core Components

1. **Payment Processing API (Spring Boot)**
   - Handles payment creation and lifecycle management.
   - Implements business validation and state transitions.
   - Exposes REST endpoints for external systems.

2. **PostgreSQL Database**
   - Stores payment records.
   - Stores payment status transition history.
   - Enforces referential integrity and transactional consistency.

3. **Docker**
   - Containerizes the Spring Boot application.
   - Ensures consistent runtime environment across systems.

4. **Docker Compose**
   - Orchestrates the application container and PostgreSQL container locally.
   - Manages internal container networking.

5. **Kubernetes (Kind)**
   - Simulates a production Kubernetes cluster locally.
   - Manages deployments, services, configuration, and scaling.

6. **CI/CD Pipeline (GitHub Actions)**
   - Automates build, test, and containerization process.
   - Integrates security scanning.

7. **Monitoring Stack (Prometheus & Grafana)**
   - Collects and visualizes application and container metrics.
   - Provides operational visibility.

### High-Level System Flow

1. A merchant system sends a payment request to the API.
2. The API validates and stores the payment with an initial status.
3. Business logic processes the payment and updates its status.
4. Each status transition is recorded in a history table.
5. The final payment status is returned to the caller.

## 3. Technology Stack

The platform leverages modern backend and DevOps technologies to simulate a production-grade system.

### Backend
- **Spring Boot** – REST API development and business logic implementation.
- **Spring Data JPA** – ORM layer for database interaction.
- **PostgreSQL** – Relational database for payment persistence and audit history.

### Containerization
- **Docker** – Containerizes the Spring Boot application.
- **Docker Compose** – Orchestrates multi-container setup for local development.

### Orchestration
- **Kubernetes (Kind)** – Local Kubernetes cluster for deployment simulation.

### CI/CD
- **GitHub Actions** – Automates build, test, containerization, and security scanning workflows.

### Security
- **Trivy** – Container vulnerability scanning.

### Monitoring & Observability
- **Spring Boot Actuator** – Exposes application metrics.
- **Prometheus** – Metrics collection.
- **Grafana** – Metrics visualization dashboard.

### Testing
- **JUnit & Mockito** – Unit and integration testing.

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

### 6.1 Domain Model

The core domain entity of the platform is the **Payment**.

A payment represents a financial transaction request initiated by a merchant system and processed by the payment service.

#### Payment Entity

| Field | Type | Description |
|-------|------|------------|
| id | UUID | Unique identifier for the payment |
| amount | BigDecimal | Monetary value of the transaction |
| currency | String | ISO currency code (e.g., USD, EUR) |
| reference | String | External reference provided by merchant |
| customerId | String | Identifier of the customer initiating the payment |
| status | PaymentStatus (Enum) | Current lifecycle state of the payment |
| createdAt | Timestamp | Time the payment was created |
| updatedAt | Timestamp | Time the payment was last updated |

#### PaymentStatus Enum

The payment lifecycle is controlled using a strongly typed enumeration:

- PENDING  
- PROCESSING  
- SUCCESS  
- FAILED  

#### PaymentStatusHistory Entity

To ensure traceability and auditability, every status transition is recorded in a separate entity.

| Field | Type | Description |
|-------|------|------------|
| id | UUID | Unique identifier for the history record |
| paymentId | UUID | Foreign key referencing Payment |
| oldStatus | PaymentStatus | Previous status |
| newStatus | PaymentStatus | Updated status |
| changedAt | Timestamp | Time of status transition |

### 6.2 Database Schema

The relational schema is designed to enforce data integrity and maintain normalized structure.

#### payments Table

- id (UUID, Primary Key)
- amount (DECIMAL)
- currency (VARCHAR)
- reference (VARCHAR)
- customer_id (VARCHAR)
- status (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

#### payment_status_history Table

- id (UUID, Primary Key)
- payment_id (UUID, Foreign Key referencing payments.id)
- old_status (VARCHAR)
- new_status (VARCHAR)
- changed_at (TIMESTAMP)
.
### Relationship Design

- One Payment can have many PaymentStatusHistory records.
- Foreign key constraints ensure referential integrity.
- Status transitions are stored as immutable historical records.

### 6.3 Payment Lifecycle

The payment lifecycle represents the state transitions a payment undergoes from creation to final outcome.

#### Lifecycle Flow

1. A merchant system sends a payment request.
2. The payment is created with status `PENDING`.
3. Business validation is performed.
4. If valid, status transitions to `PROCESSING`.
5. Payment processing is simulated.
6. Final status is set to either `SUCCESS` or `FAILED`.
7. Each transition is recorded in the `payment_status_history` table.

### State Transition Rules

- `PENDING` → `PROCESSING`
- `PROCESSING` → `SUCCESS`
- `PROCESSING` → `FAILED`

Invalid transitions are not permitted.