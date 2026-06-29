# Order Service

A microservice that creates orders. Part of the **SWST 41062 – Enterprise
Application Development** group project.

## Overview

When an order is created, Order Service:

1. Calls **Product Service** (via WebClient) to fetch the product's name and price.
2. Calculates the total price (`unitPrice × quantity`).
3. Saves the order to its own PostgreSQL database.
4. Publishes an "order created" event to **RabbitMQ** (consumed by Notification Service).

```
POST /orders ──▶ Product Service (REST) ──▶ calculate total ──▶ save ──▶ RabbitMQ
```

## Tech Stack

- Java 21, Spring Boot 3.5
- Spring Web, Spring Data JPA (Hibernate)
- Spring WebFlux (WebClient) – inter-service calls
- Spring AMQP (RabbitMQ) – event publishing
- PostgreSQL (Dockerized), RabbitMQ (Dockerized)
- Swagger / OpenAPI, JUnit 5 + Mockito
- Maven, Docker

## API Endpoints

| Method | Endpoint   | Description     |
|--------|------------|-----------------|
| POST   | `/orders`  | Create an order |

Swagger UI: `http://localhost:8081/swagger-ui.html`

## Running Locally

> Requires **Product Service** to be running on port 8080.

```bash
# 1. Start PostgreSQL + RabbitMQ (Docker)
docker compose up -d

# 2. Run the service
./mvnw spring-boot:run
```

The service starts on **port 8081**. Database runs on host port **5434**;
RabbitMQ on **5672** (dashboard at `http://localhost:15672`, guest/guest).

DB and RabbitMQ credentials, plus the Product Service URL, are configured via
environment variables with safe local defaults.

## Running Tests

```bash
./mvnw test
```

Tests mock the Product client and RabbitMQ publisher and use in-memory H2 — no
external services required.

## Architecture

Layered: **Controller → Service → Repository → Entity**, plus a `ProductClient`
(WebClient) and an `OrderEventPublisher` (RabbitMQ), following SOLID principles.
