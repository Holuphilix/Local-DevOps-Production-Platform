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
