# User Service Metadata Reference

Pre-researched blueprint for a User Profile / Auth service.
Load this file when the user is building: User Service, Auth Service, Profile Service, Account Service, Member Service.

---

## Feature Overview

The User Service is the foundational identity layer of any application. It manages who users are (profile),
how they authenticate (credentials), what they're allowed to do (roles), and their lifecycle (signup → active → deactivated).
In a microservice architecture, this is typically a standalone service consumed by all others via JWT claims.

---

## Complete Metadata Blueprint

### Identity & Keys
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `id` | `UUID v4` | ✅ | Auto-generated | Primary key. Never expose sequential IDs publicly. |
| `username` | `VARCHAR(50)` | ⚪ | Alphanumeric + underscores, unique | Optional if email is primary identifier |
| `slug` | `VARCHAR(60)` | ⚪ | URL-safe, unique | For public profile URLs: `/u/john-doe` |
| `external_id` | `VARCHAR(255)` | ⚪ | — | For SSO/OAuth provider user IDs |
| `tenant_id` | `UUID` | ⚪ | FK → tenants | Required for multi-tenant apps |

### Core Profile Data
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `first_name` | `VARCHAR(100)` | ✅ | Non-empty, trim | 🔒 PII |
| `last_name` | `VARCHAR(100)` | ✅ | Non-empty, trim | 🔒 PII |
| `display_name` | `VARCHAR(150)` | ⚪ | Auto-generated from first+last if absent | Shown in UI |
| `bio` | `TEXT` | ⚪ | Max 500 chars | Optional self-description |
| `date_of_birth` | `DATE` | ⚪ | Must be in past, user ≥ 13 | 🔒 PII — needed for age gating |
| `gender` | `VARCHAR(50)` | ⚪ | Enum or free text | 🔒 PII — use inclusive options |
| `pronouns` | `VARCHAR(50)` | ⚪ | — | Increasingly expected in modern apps |

### Contact & Communication
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `email` | `VARCHAR(320)` | ✅ | Valid email format, unique | 🔒 PII — primary identifier in most apps |
| `email_verified` | `BOOLEAN` | ✅ | Default: false | Block login until verified if strict |
| `email_verified_at` | `TIMESTAMPTZ` | ⚪ | — | When verification happened |
| `phone_number` | `VARCHAR(20)` | ⚪ | E.164 format (+91XXXXXXXXXX) | 🔒 PII |
| `phone_verified` | `BOOLEAN` | ⚪ | Default: false | Required for OTP-based 2FA |
| `country_code` | `CHAR(2)` | ⚪ | ISO 3166-1 alpha-2 | Helps with phone/locale defaults |

### Authentication & Security
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `password_hash` | `TEXT` | ⚪ | bcrypt/argon2 — never store plaintext | 🔒 PII — nullable if OAuth-only |
| `password_changed_at` | `TIMESTAMPTZ` | ⚪ | — | Used to invalidate old JWTs |
| `mfa_enabled` | `BOOLEAN` | ✅ | Default: false | — |
| `mfa_secret` | `TEXT` | ⚪ | Encrypted at rest | 🔒 PII — TOTP secret |
| `mfa_backup_codes` | `TEXT[]` | ⚪ | Hashed, one-time-use | Store as hashed array |
| `failed_login_attempts` | `SMALLINT` | ✅ | Default: 0 | For account lockout logic |
| `locked_until` | `TIMESTAMPTZ` | ⚪ | — | Null = not locked |
| `last_password_reset_at` | `TIMESTAMPTZ` | ⚪ | — | Audit trail |

### Status & Lifecycle
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `status` | `ENUM` | ✅ | `pending`, `active`, `suspended`, `deactivated` | Never hard delete users |
| `is_active` | `BOOLEAN` | ✅ | Default: false until email verified | Derived from status, or stored separately |
| `deactivated_at` | `TIMESTAMPTZ` | ⚪ | — | When user deactivated their account |
| `deactivation_reason` | `TEXT` | ⚪ | — | User-provided or system reason |
| `suspension_reason` | `TEXT` | ⚪ | — | Admin-set reason for suspension |

### Roles & Permissions
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `role` | `ENUM / VARCHAR` | ✅ | `user`, `admin`, `moderator`, etc. | Keep roles in DB; permissions in code |
| `permissions` | `TEXT[]` | ⚪ | — | Only if fine-grained permission needed |
| `is_superadmin` | `BOOLEAN` | ⚪ | Default: false | Separate flag; do not derive from role |

### Preferences & Settings
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `notification_prefs` | `JSONB` | ⚪ | Structured JSON | Email/push/SMS toggles per event type |
| `privacy_settings` | `JSONB` | ⚪ | Structured JSON | Profile visibility, data sharing |
| `theme` | `VARCHAR(20)` | ⚪ | `light`, `dark`, `system` | UI preference |
| `language` | `CHAR(5)` | ⚪ | BCP 47: `en-US`, `hi-IN` | For i18n |
| `timezone` | `VARCHAR(60)` | ⚪ | IANA timezone: `Asia/Kolkata` | For time-sensitive features |
| `currency` | `CHAR(3)` | ⚪ | ISO 4217: `INR`, `USD` | For e-commerce |

### Media & Assets
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `avatar_url` | `TEXT` | ⚪ | Valid URL or storage path | 🔒 PII — may reveal identity |
| `cover_image_url` | `TEXT` | ⚪ | — | For profile pages |

### Analytics & Tracking
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `last_login_at` | `TIMESTAMPTZ` | ✅ | — | Critical for security audits |
| `last_active_at` | `TIMESTAMPTZ` | ⚪ | — | For "seen X minutes ago" |
| `login_count` | `INTEGER` | ⚪ | Default: 0 | Useful for churn analysis |
| `signup_source` | `VARCHAR(100)` | ⚪ | `organic`, `referral`, `google`, etc. | Marketing attribution |
| `referral_code` | `VARCHAR(50)` | ⚪ | Unique if referral program exists | — |
| `referred_by` | `UUID` | ⚪ | FK → users | Self-referential for referral chains |

### Compliance & Legal
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `tos_accepted_at` | `TIMESTAMPTZ` | ✅ | Must not be null for active users | Terms of Service acceptance |
| `tos_version` | `VARCHAR(10)` | ✅ | e.g., `v2.1` | Track which version was accepted |
| `privacy_accepted_at` | `TIMESTAMPTZ` | ✅ | — | GDPR / Privacy Policy |
| `marketing_consent` | `BOOLEAN` | ⚪ | Default: false | GDPR-required explicit consent |
| `data_deletion_requested_at` | `TIMESTAMPTZ` | ⚪ | — | GDPR right-to-erasure requests |

### Audit & Timestamps
| Field Name | Type | Required | Validation | Notes |
|---|---|---|---|---|
| `created_at` | `TIMESTAMPTZ` | ✅ | Auto-set, immutable | — |
| `updated_at` | `TIMESTAMPTZ` | ✅ | Auto-updated on any change | — |
| `deleted_at` | `TIMESTAMPTZ` | ⚪ | Soft delete marker | If null → not deleted |
| `created_by` | `UUID` | ⚪ | FK → users or system | For admin-created accounts |

---

## Recommended Indexes

| Field | Index Type | Reason |
|---|---|---|
| `email` | UNIQUE | Primary lookup key |
| `username` | UNIQUE | Profile URL resolution |
| `tenant_id` | B-Tree | Multi-tenant row filtering |
| `status` | B-Tree | Frequent status filtering |
| `last_login_at` | B-Tree | Churn queries, session management |
| `deleted_at` | Partial (WHERE NULL) | Soft-delete filtering |
| `phone_number` | B-Tree + UNIQUE | OTP lookups |

---

## Relationships

| Relationship | Entity | Type | Notes |
|---|---|---|---|
| Has many | Sessions / Tokens | 1:N | Refresh token store |
| Has many | Addresses | 1:N | Shipping/billing addresses |
| Has many | OAuth Connections | 1:N | Google, GitHub, etc. |
| Has one | Subscription | 1:1 | If billing is separate |
| Belongs to | Organization/Tenant | N:1 | For B2B apps |
| Has many | AuditLogs | 1:N | Admin action history |

---

## V1 vs V2 Split

### V1 — Ship with these
`id`, `email`, `email_verified`, `first_name`, `last_name`, `password_hash`,
`status`, `role`, `tos_accepted_at`, `tos_version`, `privacy_accepted_at`,
`failed_login_attempts`, `locked_until`, `created_at`, `updated_at`, `deleted_at`,
`last_login_at`, `mfa_enabled`

### V2 — Add when needed
Everything else: preferences, compliance expansions, analytics, referral, avatar, cover image, pronouns

---

## Common Pitfalls

- **Not versioning ToS acceptance** — users accept ToS v1; you update to v2; now you can't tell who accepted which version. Always store the version.
- **Storing plaintext passwords** — even in dev/test. Enforce bcrypt/argon2 from day one.
- **Hard-deleting users** — breaks referential integrity everywhere. Always soft-delete and anonymize PII on GDPR requests instead.
- **Missing `password_changed_at`** — without this, you can't invalidate all existing JWTs when a user resets their password.
- **Single `name` field** — splitting into `first_name` + `last_name` + `display_name` gives you full flexibility for localization, sorting, and personalization.
- **No `last_active_at`** — `last_login_at` only updates on login, not on activity. Both are needed for meaningful retention metrics.
