# Nexora Task Tracker

## Phase 1: Infra & Auth
- [ ] Initialize monorepo structure
- [ ] Create `docker-compose.yml` for infrastructure (PostgreSQL, Neo4j, Redis, RabbitMQ, Zipkin, Eureka)
- [ ] Scaffold Service Registry (`service-registry`)
- [ ] Scaffold API Gateway (`api-gateway`)
- [ ] Scaffold Auth Service (`auth-service`)
- [ ] Implement JWT configuration and validation in API Gateway
- [ ] Implement Auth Service logic (Register, Login, Refresh, Logout) with Flyway migrations
- [ ] Scaffold Frontend React shell (`frontend`) with Vite + Zustand auth store

## Phase 2: User & Course
- [ ] Scaffold User Service (profile, settings)
- [ ] Scaffold Course Service (Neo4j, JSON upload, graph traversal)
- [ ] Implement Course viewer UI (markdown rendering)
- [ ] Integrate TanStack Query in frontend

## Phase 3: Progress
- [ ] Scaffold Progress Service (completion, streaks)
- [ ] Implement RabbitMQ event wiring (`lesson-completed` -> Progress Service)
- [ ] Implement Learner Dashboard UI

## Phase 4: Submission & AI
- [ ] Scaffold Submission Service (Piston code execution)
- [ ] Scaffold AI Service (Spring AI + Gemini for problems, eval, diagrams)
- [ ] Implement Monaco Editor UI in frontend
- [ ] Wire Feign clients between AI and Course services

## Phase 5: Announcement & Admin
- [ ] Scaffold Announcement Service
- [ ] Implement Admin Dashboard
- [ ] Implement Educator Analytics

## Phase 6: Observability & Deploy
- [ ] Set up GitHub Actions pipelines per service
- [ ] Write Dockerfiles for all services
- [ ] Create Kubernetes manifests
- [ ] Verify Zipkin distributed tracing & Grafana metrics
