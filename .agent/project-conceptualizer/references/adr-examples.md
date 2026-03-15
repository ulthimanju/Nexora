# Architecture Decision Records (ADR) Examples

ADRs capture *why* a decision was made, not just what was decided. They are lightweight, living documents.

---

## ADR-001: Async AI Pipeline via Virtual Threads

**Context:**  
LLM API calls (OpenAI, Gemini) can take 2–10 seconds. If handled synchronously, this blocks HTTP threads and degrades throughput under load.

**Decision:**  
Use Spring Boot 3 virtual threads (Project Loom) with `CompletableFuture` for all LLM calls. Return a job ID immediately; push results via WebSocket or polling endpoint.

**Consequences:**  
- ✅ Main threads not blocked; better concurrency under load  
- ✅ Users get immediate acknowledgment (202 Accepted)  
- ⚠️ More complex error handling (timeouts, retries must be explicit)  
- ⚠️ Frontend must poll or maintain WebSocket connection

---

## ADR-002: MongoDB over PostgreSQL for Submission Data

**Context:**  
User submission data (code, essays, etc.) is variable in schema — different problem types have different metadata structures.

**Decision:**  
Use MongoDB for submission documents. Use PostgreSQL for structured relational data (users, scores, leaderboards).

**Consequences:**  
- ✅ Flexible schema for heterogeneous submission types  
- ✅ Easy to add new problem types without migrations  
- ⚠️ Two databases to manage and maintain  
- ⚠️ No cross-database joins; application must stitch data

---

## ADR-003: Microservices over Monolith

**Context:**  
Auth, AI evaluation, and core business logic have very different scaling profiles. Auth is lightweight; AI evaluation is CPU/IO heavy and slow.

**Decision:**  
Separate into at minimum three services: `auth-service`, `eval-service` (AI), `core-service`. Use an API Gateway for routing.

**Consequences:**  
- ✅ AI service can scale independently without scaling auth  
- ✅ Isolated failure domains  
- ⚠️ Distributed system complexity (network failures, distributed tracing)  
- ⚠️ Higher initial setup cost vs. monolith

---

## ADR-004: JWT with Redis Blocklist for Auth

**Context:**  
Pure stateless JWT cannot support instant token revocation (logout, ban). Full session storage loses statelessness benefits.

**Decision:**  
Use JWTs with short TTL (15 min access, 7 day refresh). Maintain a Redis blocklist for revoked tokens. Check Redis on each request.

**Consequences:**  
- ✅ Stateless by default; Redis only checked for revoked tokens  
- ✅ Near-instant revocation capability  
- ⚠️ Redis becomes a dependency on every authenticated request  
- ⚠️ Blocklist must be cleaned up (TTL-based expiry)

---

## ADR Template

```
## ADR-NNN: [Short Title]

**Context:**
[What is the situation forcing a decision?]

**Decision:**
[What was decided?]

**Consequences:**
- ✅ [Positive outcome]
- ⚠️ [Tradeoff or risk]
```
