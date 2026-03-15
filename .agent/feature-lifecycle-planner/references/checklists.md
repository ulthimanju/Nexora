# Checklists Reference

## Security Review Checklist

### Input & Validation
- [ ] All user inputs are validated server-side (not just client-side)
- [ ] SQL queries use parameterized statements / ORM (no raw string concat)
- [ ] File uploads: type, size, and content validated; stored outside webroot
- [ ] JSON schemas validated at API boundary
- [ ] HTML output is escaped to prevent XSS

### Authentication & Authorization
- [ ] Every endpoint requires appropriate auth (JWT, session, API key)
- [ ] Permissions checked per resource, not just per route
- [ ] Sensitive endpoints rate-limited and brute-force protected
- [ ] Tokens have appropriate expiry; refresh token rotation implemented
- [ ] Role-based access control (RBAC) reviewed for new roles/permissions

### Data & Privacy
- [ ] PII is not logged in plain text
- [ ] Sensitive fields (passwords, tokens) are never returned in API responses
- [ ] Data encrypted at rest (DB-level or field-level for PII)
- [ ] Data encrypted in transit (TLS 1.2+)
- [ ] GDPR/CCPA: right to delete, data minimization reviewed
- [ ] Third-party data sharing: reviewed and documented

### Infrastructure
- [ ] Secrets stored in vault / environment variables (not hardcoded)
- [ ] Feature does not introduce new open ports or public endpoints unnecessarily
- [ ] Dependencies scanned for known CVEs (npm audit, Dependabot, Snyk)
- [ ] CORS policy reviewed and restricted appropriately

---

## Launch Readiness Checklist

### Code & Quality
- [ ] All PR reviews completed and approved
- [ ] CI pipeline green (lint, test, build)
- [ ] No open P1/P2 bugs from testing phase
- [ ] Code coverage meets project standard
- [ ] Performance benchmarks within acceptable thresholds

### Infrastructure & Config
- [ ] Environment variables set in production
- [ ] Feature flag configured and default state verified
- [ ] DB migrations tested on staging with production-size data snapshot
- [ ] CDN cache rules updated if applicable
- [ ] DNS / routing changes staged (if applicable)

### Observability
- [ ] New metrics instrumented and visible in dashboard
- [ ] Alerts configured with appropriate thresholds
- [ ] Logs flowing to centralized log system
- [ ] Error tracking (Sentry / Datadog) capturing new errors
- [ ] Runbook written and accessible

### Communication
- [ ] Internal stakeholders notified (PM, design, support, sales)
- [ ] Customer support briefed on new feature behavior and FAQ
- [ ] Release notes / changelog prepared
- [ ] Documentation (user-facing and internal) updated
- [ ] Marketing / announcement prepared (if user-facing launch)

### Rollback
- [ ] Rollback procedure documented and tested on staging
- [ ] On-call engineer briefed and available during launch window
- [ ] Rollback decision criteria explicitly agreed upon (e.g., "if error rate > 2% in first 30 min")

---

## Definition of Done (DoD) — Standard

### Feature Level
- [ ] All user stories accepted by product owner
- [ ] Acceptance criteria verified in staging environment
- [ ] Edge cases and error states handled gracefully
- [ ] No regressions introduced in existing functionality

### Code Level
- [ ] Code reviewed by ≥ 1 peer
- [ ] No TODO/FIXME left unresolved (or tracked in backlog)
- [ ] Dead code removed
- [ ] Dependencies up to date

### Test Level
- [ ] Unit tests written for new logic
- [ ] Integration tests cover key API contracts
- [ ] E2E tests cover critical user flows
- [ ] All tests passing in CI

### Ops Level
- [ ] Deployed to staging and smoke-tested
- [ ] Feature flag in place
- [ ] Monitoring and alerting configured
- [ ] Runbook exists

### Documentation Level
- [ ] README / internal wiki updated
- [ ] API docs updated (Swagger / Postman / Notion)
- [ ] Onboarding docs updated if new setup steps exist

---

## Post-Mortem Template

```markdown
# Post-Mortem: [Feature Name] — [Incident title if applicable]

**Date**: YYYY-MM-DD  
**Severity**: P1 | P2 | P3  
**Duration**: [Start time] → [End time]  
**Author(s)**: [Names]

## Summary
[2–3 sentences: what happened, impact, resolution]

## Timeline
| Time (UTC) | Event |
|------------|-------|
| HH:MM | First alert fired |
| HH:MM | Engineer paged |
| HH:MM | Root cause identified |
| HH:MM | Mitigation applied |
| HH:MM | Fully resolved |

## Root Cause
[Technical explanation of what caused this]

## Impact
- Users affected: [N users / X% of traffic]
- Duration: [N minutes]
- Data loss: [Yes/No — details]
- Revenue impact: [$X or "none"]

## What Went Well
- [...]

## What Went Poorly
- [...]

## Action Items
| Action | Owner | Due Date |
|--------|-------|----------|
| [Fix X] | @name | YYYY-MM-DD |
| [Add alert Y] | @name | YYYY-MM-DD |

## Prevention
[How do we make sure this doesn't happen again?]
```
