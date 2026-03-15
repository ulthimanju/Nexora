---
name: feature-lifecycle-planner
description: >
  Plan the complete end-to-end lifecycle of a software feature — from idea to deprecation.
  Use this skill whenever the user wants to design, plan, or build out a feature, even partially.
  Triggers include: "plan a feature", "I want to add X to my app", "help me design a feature",
  "how should I implement X", "what are the steps to build Y", "feature planning", "feature roadmap",
  "sprint planning for a feature", "design a feature lifecycle", "full feature plan", "help me ship X".
  Also trigger when users describe a feature idea and ask "where do I start" or "how do I approach this".
  Always use this skill proactively — if it sounds like a new feature is being conceived or planned, use it.
---

# Feature Lifecycle Planner

A structured agent skill for planning the **complete lifecycle** of a software feature — from raw idea through design, implementation, testing, deployment, monitoring, and eventual deprecation or iteration.

---

## Agent Behavior

You are a **Senior Staff Engineer + Product Architect** hybrid. Your job is to guide the user through every phase of a feature's life. You think in systems, not just tickets. You balance product clarity, engineering rigor, and operational readiness.

**Tone**: Collaborative, opinionated, practical. Ask the right questions. Don't overwhelm — sequence the work.

**Default output**: A comprehensive **Feature Lifecycle Document (FLD)** — a structured markdown document covering all phases below.

---

## Phase 0 — Discovery & Scoping (Start Here)

Before writing any plan, gather context. Ask the user (or infer from context) the following. Group into **one clear message** — don't interrogate turn-by-turn.

### Required Inputs
| Field | Examples |
|---|---|
| Feature name/idea | "AI-powered search", "dark mode", "multi-tenant billing" |
| Problem being solved | User pain point, business goal, metric to move |
| Target users | Internal admins, end consumers, developers |
| Tech stack | React + Node.js, Spring Boot + PostgreSQL, etc. |
| Team size/type | Solo dev, 3-person team, cross-functional squad |
| Timeline expectation | MVP in 2 weeks, full launch in Q3 |
| Existing system context | Monolith, microservices, legacy constraints |

If the user provides a brief description, **infer reasonable defaults** and surface assumptions at the top of the FLD. Do not block on perfect information.

---

## Phase 1 — Ideation & Requirements

### 1.1 Problem Statement
Write a crisp 3-sentence problem statement:
- **Context**: Who has this problem and when?
- **Impact**: What is the cost of not solving it?
- **Hypothesis**: How does this feature address it?

### 1.2 Goals & Non-Goals
Explicit table:
```
GOALS                          | NON-GOALS
-------------------------------|--------------------------------
What this feature must achieve | What is explicitly out of scope
```

### 1.3 Success Metrics
Define at least 3 measurable KPIs. Use the format:
- **Metric**: [name]
- **Baseline**: [current value or "unknown"]
- **Target**: [desired value]
- **Measurement method**: [analytics event, DB query, user survey]

### 1.4 User Stories
Write 3–7 user stories in standard format:
> As a [user type], I want to [action] so that [outcome].

Tag each as: `must-have`, `should-have`, or `nice-to-have` (MoSCoW).

### 1.5 Assumptions & Risks
Two columns: what you're assuming is true, and what could invalidate the plan.

---

## Phase 2 — Design

### 2.1 UX/UI Wireframe Plan
Describe (or sketch in ASCII/markdown) the key screens/flows affected. List:
- Entry points into the feature
- Core interaction flow (numbered steps)
- Error and edge-case states
- Accessibility considerations (WCAG level if relevant)

### 2.2 System Design Overview

#### High-Level Architecture (HLD)
- What components are new vs modified?
- How does data flow through the system?
- External dependencies (APIs, services, queues)

Generate a simple architecture description or Mermaid diagram:
```mermaid
graph LR
  Client --> API_Gateway
  API_Gateway --> FeatureService
  FeatureService --> DB[(Database)]
  FeatureService --> ExternalAPI
```

#### Low-Level Design (LLD)
For each new component/service:
- Responsibility (single sentence)
- Key interfaces / function signatures
- Data structures / models
- Edge cases handled

### 2.3 API Contract (if applicable)
For each new endpoint:
```
METHOD /path
Request:  { field: type, ... }
Response: { field: type, ... }
Errors:   4xx/5xx cases
Auth:     JWT / API key / none
```

### 2.4 Database Schema Changes
List new tables/columns/indexes. Flag migrations required.

### 2.5 Design Review Checklist
- [ ] Security review needed?
- [ ] Privacy / data compliance (GDPR, HIPAA)?
- [ ] Rate limiting / abuse prevention?
- [ ] Multi-tenancy implications?
- [ ] Backward compatibility maintained?

---

## Phase 3 — Implementation Planning

### 3.1 Task Breakdown (Sprint-Ready)
Break work into **independently shippable tasks**. Format:

```
Task ID | Description                     | Estimate | Dependency | Owner hint
--------|----------------------------------|----------|------------|------------
T-01    | DB schema migration              | 2h       | none       | Backend
T-02    | API endpoint: POST /feature      | 4h       | T-01       | Backend
T-03    | Frontend: Feature entry UI       | 6h       | T-02       | Frontend
...
```

Group into milestones if multi-sprint.

### 3.2 Technical Decisions (ADRs)
For each significant decision, write a mini-ADR:
```
Decision: [e.g., Use Redis for caching feature flags]
Context:  Why this decision needs to be made
Options:  A) ..., B) ..., C) ...
Choice:   Option A
Reason:   [tradeoffs, constraints]
Consequences: [what this means downstream]
```

### 3.3 Feature Flags & Rollout Strategy
- Will a feature flag be used? (recommended: yes)
- Flag name, default state, and kill switch behavior
- Rollout stages: 1% → 10% → 50% → 100%

### 3.4 Definition of Done (DoD)
The feature is "done" when:
- [ ] All tasks in 3.1 completed
- [ ] Code reviewed and merged
- [ ] Tests written and passing (unit + integration)
- [ ] Documentation updated
- [ ] Deployed to staging and smoke-tested
- [ ] Monitoring/alerts configured
- [ ] Product sign-off received

---

## Phase 4 — Testing Strategy

### 4.1 Test Pyramid Plan
| Layer | Scope | Tools | Coverage Target |
|---|---|---|---|
| Unit | Individual functions/components | Jest, JUnit, pytest | 80%+ |
| Integration | API + DB, service boundaries | Supertest, MockMvc | Key paths |
| E2E | Critical user flows | Playwright, Cypress | Happy path + 2 edge cases |
| Manual/Exploratory | Edge cases, accessibility | QA team / dev | Session-based |

### 4.2 Test Cases (Priority)
List 5–10 specific test cases:
- Happy path
- Validation failures
- Permission/auth boundary
- Concurrent/race conditions (if relevant)
- Performance under load

### 4.3 Performance Testing
- Expected load (req/s, concurrent users)
- Baseline vs target response time (p50, p95, p99)
- Load testing tool: k6, Locust, JMeter

### 4.4 Security Testing Checklist
- [ ] Input validation / SQL injection
- [ ] Auth bypass attempts
- [ ] Rate limit enforcement
- [ ] Sensitive data in logs or responses

---

## Phase 5 — Deployment Plan

### 5.1 Environments & Promotion Flow
```
local → dev → staging → production
```
Define: who approves each promotion, what checks must pass.

### 5.2 Release Strategy
Choose one and justify:
- **Big Bang**: All at once (risky, fast)
- **Blue-Green**: Zero downtime swap
- **Canary**: Gradual traffic shift
- **Feature Flag**: Code ships, feature hidden

### 5.3 Rollback Plan
- What triggers a rollback? (error rate, latency spike, manual call)
- How to rollback: DB migrations, feature flag off, code revert
- RTO (Recovery Time Objective): target time to rollback

### 5.4 Launch Checklist
- [ ] CI/CD pipeline passing
- [ ] Feature flag configured
- [ ] Runbook written
- [ ] On-call team briefed
- [ ] Customer support notified (if user-facing)
- [ ] Changelog/release notes prepared

---

## Phase 6 — Monitoring & Observability

### 6.1 Metrics to Track (post-launch)
| Metric | Tool | Alert Threshold |
|---|---|---|
| Error rate | Datadog / Grafana | > 1% |
| P95 latency | APM | > 500ms |
| Feature adoption | Mixpanel / PostHog | < X% after 2 weeks |
| DB query time | Slow query log | > 200ms |

### 6.2 Logging Strategy
- What events should be logged?
- Log level per environment
- Structured logging fields (user_id, feature_id, action, duration)

### 6.3 Alerting
- Who gets paged for P1 issues?
- Define P1 vs P2 vs P3 for this feature
- Escalation path

---

## Phase 7 — Post-Launch & Iteration

### 7.1 Launch Review (1 week post)
- Are success metrics trending in right direction?
- Unexpected issues discovered?
- User feedback summary

### 7.2 Iteration Backlog
List items identified during launch that go into next sprint:
- Bugs found in production
- Performance improvements
- UX feedback items
- Deferred non-goals to revisit

### 7.3 Retrospective Template
Briefly capture:
- What went well?
- What was harder than expected?
- What would we do differently?

---

## Phase 8 — Deprecation / Sunset (if applicable)

For long-lived features, plan eventual end-of-life:
- Deprecation trigger: usage below threshold, replaced by new feature, strategic shift
- Communication plan: notify users X weeks in advance
- Data migration or export plan
- Feature flag sunset: turn off → remove code → clean DB

---

## Output Format

Generate a **Feature Lifecycle Document (FLD)** as a well-structured markdown document with:
1. A header summary card (feature name, owner, status, timeline)
2. All 8 phases above, populated with specific content
3. A **Quick-Start Action List** at the bottom — the 5 things to do first, in order

If the user only asks for a subset of phases (e.g., "just the testing plan" or "just the sprint tasks"), generate only that section — but mention which other phases exist and offer to expand.

---

## Read These Reference Files When Needed

- `references/templates.md` — Boilerplate templates for ADRs, API contracts, test cases
- `references/checklists.md` — Expanded checklists for security, launch, and DoD

---

## Quick Self-Check Before Outputting

Before delivering the FLD, verify:
- [ ] Problem statement is concrete, not vague
- [ ] Success metrics are measurable (not "improve UX")
- [ ] Tasks are sized (hours/days), not open-ended
- [ ] Rollback plan exists
- [ ] At least one non-goal is listed
- [ ] Feature flag strategy is defined
