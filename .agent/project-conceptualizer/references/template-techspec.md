# Technical Specification Document Template

---

## Tech Spec: [Project Name]

**Version:** 1.0  
**Author:** [Name]  
**Date:** [Date]  
**Related PRD:** [Link]

---

### 1. Technology Stack

| Layer | Technology | Version | Rationale |
|-------|------------|---------|-----------|
| Frontend | React / Next.js | 18.x | SSR, ecosystem |
| Backend | Spring Boot | 3.x | Microservices, virtual threads |
| Database (primary) | PostgreSQL / MongoDB | ... | Relational / document model |
| Cache | Redis | 7.x | Session, rate limiting, temp data |
| AI/LLM | OpenAI / Gemini / local | ... | UVP feature |
| Message Queue | Kafka / RabbitMQ | ... | Async processing |
| Auth | JWT + OAuth2 | ... | Stateless, standard |
| Infra | Docker + Kubernetes | ... | Container orchestration |
| CI/CD | GitHub Actions | ... | Automated pipelines |
| Monitoring | Prometheus + Grafana | ... | Observability |

---

### 2. Architecture Pattern

**Chosen Pattern:** Microservices / Monolith / Serverless

**Rationale:** [Why this pattern fits the project's scale and UVP]

**Services:**
| Service | Responsibility | Tech |
|---------|----------------|------|
| auth-service | JWT issuance, OAuth2 | Spring Boot |
| core-service | Business logic | Spring Boot |
| ai-service | LLM integration | Python / Spring Boot |
| frontend | UI | React/Next.js |
| gateway | Routing, auth filter | Spring Cloud Gateway |

---

### 3. Key Technical Decisions

**Decision 1: [Topic]**
- Choice: ...
- Alternatives considered: ...
- Rationale: ...

**Decision 2: [Topic]**
- Choice: ...
- Alternatives considered: ...
- Rationale: ...

---

### 4. Non-Functional Requirements

| Category | Requirement | Target |
|----------|-------------|--------|
| Performance | API response time (p99) | < 200ms |
| Availability | Uptime SLA | 99.9% |
| Scalability | Peak concurrent users | 10,000 |
| Security | Auth standard | OAuth2 + JWT |
| Data Retention | User data | 2 years |

---

### 5. Database Schema (High-Level)

```sql
-- Example
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE [entity] (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  ...
);
```

---

### 6. Environment Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | Database connection string | `jdbc:postgresql://...` |
| `REDIS_HOST` | Redis host | `localhost` |
| `LLM_API_KEY` | AI service API key | `sk-...` |
| `JWT_SECRET` | JWT signing secret | `[strong random]` |
