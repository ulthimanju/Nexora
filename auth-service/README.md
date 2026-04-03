# Nexora Auth Service

A production-ready authentication and authorization microservice built with Spring Boot 3.4 and Java 21.

## Features

- **JWT Authentication**: RS256 asymmetric signing for secure token verification across microservices
- **Role-Based Access Control (RBAC)**: Dynamic role management with Spring Security
- **Refresh Token System**: Secure multi-device session management with Redis caching
- **BCrypt Password Hashing**: Cost factor 12 for optimal security/performance balance
- **JWKS Endpoint**: Public key exposure for token verification by other services
- **PostgreSQL Database**: ACID compliance with optimized indexes for 1M+ users
- **Redis Caching**: Fast token validation and rate limiting support
- **Docker Support**: Multi-stage builds for lean production images
- **Health Checks**: Spring Actuator for monitoring and observability

## Tech Stack

- **Framework**: Spring Boot 3.4 (Java 21 with Virtual Threads)
- **Security**: Spring Security 6.x with JWT
- **Database**: PostgreSQL 16
- **Cache**: Redis 7.2
- **Build Tool**: Maven
- **Container**: Docker with Alpine Linux

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker and Docker Compose (for containerized setup)
- PostgreSQL 16 (if running locally without Docker)
- Redis 7.2 (if running locally without Docker)

### Running with Docker Compose

1. Set environment variables (optional):
```bash
export DB_USER=nexora_user
export DB_PASS=nexora_pass
export JWT_SECRET=your-secret-key
```

2. Start all services:
```bash
cd auth-service
docker-compose up -d
```

The auth service will be available at `http://localhost:8081`

### Running Locally

1. Start PostgreSQL and Redis:
```bash
# PostgreSQL
docker run -d -p 5432:5432 -e POSTGRES_DB=auth_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:16-alpine

# Redis
docker run -d -p 6379:6379 redis:7.2-alpine
```

2. Build and run the application:
```bash
cd auth-service
mvn clean package
java -jar target/auth-service-1.0.0.jar
```

## API Endpoints

### Public Endpoints

#### Register User
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

#### Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "SecurePass123"
}
```

#### Refresh Token
```bash
POST /api/v1/auth/refresh
Authorization: Bearer <refresh_token>
```

#### Logout
```bash
POST /api/v1/auth/logout
Authorization: Bearer <refresh_token>
```

#### Get Public Key (JWKS)
```bash
GET /.well-known/jwks.json
```

### Protected Endpoints

#### Admin Dashboard (Requires ADMIN role)
```bash
GET /api/v1/admin/dashboard
Authorization: Bearer <access_token>
```

#### Health Check
```bash
GET /actuator/health
```

## Database Schema

The service uses the following tables:

- **users**: User accounts with UUID primary keys
- **roles**: Predefined roles (ADMIN, INSTRUCTOR, STUDENT, GUEST)
- **user_roles**: Many-to-many relationship between users and roles
- **refresh_tokens**: Refresh token storage with expiry and revocation support

See `init-db.sql` for the complete schema.

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (dev/prod) | dev |
| `DB_URL` | PostgreSQL connection URL | jdbc:postgresql://localhost:5432/auth_db |
| `DB_USER` | Database username | postgres |
| `DB_PASS` | Database password | postgres |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `REDIS_PASS` | Redis password | (empty) |
| `JWT_SECRET` | JWT secret for development | (generated) |
| `JWT_PRIVATE_KEY` | RSA private key for production | (generated) |

### Profiles

- **dev**: Development profile with verbose logging and auto-DDL
- **prod**: Production profile with connection pooling and validation

## Security Features

### JWT Tokens

- **Access Token**: Short-lived (15 minutes), used for API authentication
- **Refresh Token**: Long-lived (7 days), stored in Redis with hash
- **Algorithm**: RS256 (RSA with SHA-256)
- **Key Size**: 2048 bits

### Password Security

- **Hashing**: BCrypt with cost factor 12
- **Validation**: Minimum 8 characters required

### Token Validation

1. Extract JWT from Authorization header
2. Verify signature using public key
3. Check expiration
4. Load user details and authorities
5. Set SecurityContext

## Scaling Considerations

The service is designed to scale to 1M+ users:

- **Stateless Design**: JWTs allow horizontal scaling without session storage
- **Connection Pooling**: HikariCP with 20 connections max
- **Redis Caching**: O(1) token lookups with TTL-based expiry
- **Virtual Threads**: Java 21 virtual threads for high concurrency
- **Database Indexes**: Optimized indexes on email and token fields
- **Read Replicas**: PostgreSQL supports read replicas for load distribution

## Development

### Building

```bash
mvn clean package
```

### Running Tests

```bash
mvn test
```

### Building Docker Image

```bash
docker build -t nexora-auth:latest .
```

## Monitoring

The service exposes the following Actuator endpoints:

- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics
- `/actuator/prometheus` - Prometheus metrics

## License

MIT License - See LICENSE file for details
