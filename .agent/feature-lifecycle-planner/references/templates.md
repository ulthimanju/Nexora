# Templates Reference

## ADR Template (Architecture Decision Record)

```markdown
## ADR-[N]: [Short Title]

**Date**: YYYY-MM-DD  
**Status**: Proposed | Accepted | Deprecated | Superseded by ADR-[N]

### Context
[Why does this decision need to be made? What forces are at play?]

### Decision
[What is the change we're making?]

### Options Considered
- **Option A**: [Description] — Pros: ... Cons: ...
- **Option B**: [Description] — Pros: ... Cons: ...

### Chosen Option
Option [X] because [reasoning based on constraints and goals].

### Consequences
- Positive: [...]
- Negative: [...]
- Neutral: [...]
```

---

## API Contract Template

```markdown
### POST /api/v1/[resource]

**Description**: [What this endpoint does]  
**Auth**: Bearer JWT | API Key | None  
**Rate limit**: [N] req/min per user

**Request Body**
```json
{
  "field_name": "string (required)",
  "optional_field": "number (optional, default: 0)"
}
```

**Success Response** `200 OK`
```json
{
  "id": "uuid",
  "status": "created",
  "data": { ... }
}
```

**Error Responses**
| Code | Reason |
|------|--------|
| 400  | Validation failed — `{ "error": "field X is required" }` |
| 401  | Unauthorized |
| 403  | Forbidden — user lacks permission |
| 409  | Conflict — resource already exists |
| 500  | Internal server error |
```

---

## User Story Template

```
Story: [Short title]
As a [role],
I want to [action/capability],
So that [benefit/outcome].

Acceptance Criteria:
  - Given [context], when [action], then [result]
  - Given [context], when [action], then [result]

Priority: must-have | should-have | nice-to-have
Estimate: [S/M/L or story points or hours]
```

---

## Test Case Template

```
Test ID: TC-[N]
Title: [Short description]
Category: unit | integration | e2e | manual
Priority: P1 | P2 | P3

Preconditions:
  - [Setup state or data required]

Steps:
  1. [Action]
  2. [Action]

Expected Result:
  - [What should happen]

Edge Cases Covered:
  - [If applicable]
```

---

## Runbook Template

```markdown
# Runbook: [Feature Name] Incident Response

## Overview
[What this feature does in one sentence]

## On-Call Contact
Primary: @[name] | Backup: @[name]

## Common Failure Modes

### Symptom: [High error rate]
1. Check: [Datadog dashboard URL]
2. If error rate > 5%: flip feature flag OFF immediately
3. Notify: #[slack-channel]
4. Root cause: check logs for `[log_pattern]`

### Symptom: [Slow queries]
1. Check: [DB monitoring link]
2. Kill long-running queries if > 30s
3. Escalate to DB team if persistent

## Rollback Procedure
1. Set feature flag `[flag_name]` to `false` in [tool]
2. Verify error rate drops within 2 minutes
3. If DB migration involved: run `[rollback command]`
4. Open incident ticket and notify stakeholders

## Recovery Checklist
- [ ] Feature flag disabled
- [ ] Error rate below baseline
- [ ] Stakeholders notified
- [ ] Post-mortem scheduled
```
