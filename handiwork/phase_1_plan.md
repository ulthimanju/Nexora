# Nexora - Phase 1 Implementation Plan

## Goal Description
Set up the initial monorepo structure, core infrastructure services via Docker Compose, API Gateway for routing and JWT validation, the Auth Service for user identity, the Service Registry, and the React frontend shell. This establishes the foundation upon which all other microservices will be built.

## Proposed Changes

### Monorepo & Infrastructure
- Initialize the root `nexora` monorepo.
- Create `docker-compose.yml` in the root to spin up:
  - PostgreSQL instances (for Auth, later for others)
  - Neo4j (for Course)
  - Redis (for Gateway rate limiting and Auth token cache)
  - RabbitMQ (for async events)
  - Zipkin (for distributed tracing)

### Service Registry
- Generate Spring Boot app `service-registry` (Java 21, Spring Cloud Netflix Eureka Server).
- Configure it to run on port 8761 and register services.

### API Gateway
- Generate Spring Boot app `api-gateway` (Java 21, Spring Cloud Gateway, Eureka Client, Redis Reactive, Spring Security).
- Port: 8080.
- Implement JWT Auth Filter to validate access tokens using the shared `JWT_SECRET`.
- Implement global CORS configuration allowing requests from the Vite frontend.
- Configure Redis-based rate limiting per IP/user.

### Auth Service
- Generate Spring Boot app `auth-service` (Java 21, Spring Web, Spring Security, Spring Data JPA, Eureka Client, Flyway, PostgreSQL Driver, JJWT, MapStruct).
- Port: 8081.
- Setup `flyway/db/migration` for `users` and `refresh_tokens` tables.
- Implement User Registration (passwords hashed via BCrypt, avatars generated via DiceBear API).
- Implement Login (issues short-lived JWT, sets HttpOnly cookie with refresh token).
- Implement Token Refresh endpoint.
- Implement Logout (revokes refresh token in Redis).
- Setup RabbitMQ publisher to emit `user-registered` event.

### Frontend Shell
- Initialize React 19 + Vite app in `frontend/`.
- Set up TailwindCSS.
- Create Zustand store for Auth (`useAuthStore`).
- Set up Axios interceptor to automatically attach access tokens and handle 401 retries by calling the refresh endpoint.
- Develop basic Login/Register screens and a protected shell layout.

## Verification Plan

### Automated Tests
- Unit/Integration tests for `auth-service` using Testcontainers (PostgreSQL).
- Write WebTestClient tests for `api-gateway` JWT filter.

### Manual Verification
- `docker-compose up` to verify all infra containers start.
- Start Service Registry, Gateway, and Auth Service. Verify they register in Eureka.
- Use Postman/cURL (or the React app) to register a user.
- Log in and verify the access token is returned and the `refresh_token` HttpOnly cookie is set.
- Access a dummy protected route via the Gateway and assert it allows access with token and returns 401 without it.
