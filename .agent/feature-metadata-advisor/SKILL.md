---
name: feature-metadata-advisor
description: >
  Generates comprehensive metadata blueprints for any software feature or service the user wants to build.
  Use this skill whenever the user says things like "I want to build X service", "what fields do I need for Y",
  "help me design the data model for Z", "what metadata is required for a [feature]", "I'm building a [module/service/entity]
  and don't know what data to include", or any variation of planning what data a feature should hold.
  Trigger even for vague requests like "user profiles", "product listings", "order management", "notifications",
  "payments", "authentication", "messaging" — any feature concept should trigger this skill.
  This skill should be used proactively; if the user is designing, planning, or scoping any feature, use it.
---

# Feature Metadata Advisor

A skill that produces a **complete metadata blueprint** for any feature or service — covering all data fields,
their types, validations, relationships, and design rationale. Designed to give developers a clear, opinionated
starting point so they never have to guess what data they need.

---

## Your Job

When a user says they want to build something (a service, feature, entity, module), your job is to:

1. **Identify the feature domain** — understand what they're building
2. **Clarify scope** (if needed) — ask 1–2 targeted questions if the feature is ambiguous
3. **Generate the full metadata blueprint** — structured, categorized, with all fields
4. **Explain design decisions** — brief rationale for non-obvious fields
5. **Flag optional vs required fields** — and note what can be deferred to v2

---

## Output Format

Always structure the output as follows:

### 1. Feature Overview
- One-paragraph description of what the feature/service does
- Core responsibilities of this service/entity

### 2. Metadata Blueprint (the main output)

Organize fields into **logical categories**. For each field, provide:

| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `field_name` | `String / UUID / etc` | ✅ / ⚪ | Rules | Design note |

Categories to always consider (include relevant ones only):
- **Identity & Keys** — IDs, slugs, external references
- **Core Profile Data** — the primary descriptive fields
- **Contact & Communication** — emails, phones, addresses
- **Authentication & Security** — passwords, tokens, MFA, sessions
- **Status & Lifecycle** — active/inactive, verification, soft deletes
- **Preferences & Settings** — user-controlled config
- **Relationships & Associations** — foreign keys, linked entities
- **Audit & Timestamps** — created_at, updated_at, deleted_at, created_by
- **Analytics & Tracking** — last_login, usage counts, source tracking
- **Compliance & Legal** — GDPR consent, ToS acceptance, data retention
- **Media & Assets** — profile pictures, documents, attachments
- **Localization** — timezone, locale, language, currency

### 3. Recommended Indexes
List fields that should be indexed and why (unique, search, foreign key).

### 4. Relationships
List entities this feature relates to (one-to-one, one-to-many, many-to-many).

### 5. V1 vs V2 Split
- **V1 (Must-have)**: Fields you cannot ship without
- **V2 (Nice-to-have)**: Fields that add value but can wait

### 6. Common Pitfalls
2–4 bullet points on what developers typically miss when building this feature.

---

## Common Feature Reference

Load the relevant reference file(s) from `references/` when the user's feature matches:

- User / Profile / Auth → `references/user-service.md`
- Product / Catalog / Inventory → `references/product-service.md`
- Order / Cart / Checkout → `references/order-service.md`
- Payment / Billing / Subscription → `references/payment-service.md`
- Notification / Messaging → `references/notification-service.md`

If a reference file doesn't exist for the requested feature, generate the blueprint from first principles using the output format above and the common categories listed.

---

## Behavior Rules

- **Never skip categories silently** — if a category doesn't apply, briefly say why
- **Always include audit fields** — `created_at`, `updated_at`, `deleted_at` are non-negotiable for any entity
- **Flag PII fields explicitly** — mark any personally identifiable information as `🔒 PII`
- **Suggest data types concretely** — use real types (UUID, VARCHAR(255), BOOLEAN, TIMESTAMP WITH TIME ZONE, JSONB, etc.)
- **Don't be vague** — "some metadata" is not useful; always give concrete field names
- **Prefer asking one scoped question** over generating a wrong blueprint — e.g., "Is this B2C or B2B? That changes the org/company fields."

---

## Clarifying Questions (use sparingly — only if truly ambiguous)

Ask at most **2 questions** before generating. Good clarifying questions:
- "Is this a B2C user or does it support organizational/team accounts?"
- "Is authentication handled in this service or a separate auth service?"
- "Are you building for multi-tenancy (one DB for multiple clients)?"
- "Is this an e-commerce product or a digital/SaaS product?"

---

## Example Trigger → Response

**User**: "I want to build a user service that contains all user profile data"

**You**: Generate a full metadata blueprint with ~30–40 fields across all relevant categories
(Identity, Profile, Contact, Auth, Status, Preferences, Audit, Compliance, etc.)
with types, validations, PII flags, index recommendations, and a V1/V2 split.
