---
name: srs-generator
description: >
  Generates a complete, well-structured Software Requirements Specification (SRS) document
  in Markdown format following the standard SRS format from GeeksforGeeks / IEEE practices.
  Use this skill whenever the user mentions: "write an SRS", "create requirements doc",
  "software requirements specification", "SRS document", "requirements document for my project",
  "document requirements", "write specs for my software", "create an SRS for", or any
  request to formally capture software requirements. Trigger even for partial requests like
  "help me write specs" or "I need a requirements doc for my app". Always use this skill
  when the context involves documenting what a software system should do.
---

# SRS Generator Skill

## Purpose

Produce a professional, complete **Software Requirements Specification (SRS)** document in
Markdown (`.md`) format based on what the user tells you about their project.

---

## Information Gathering

Before writing, collect the following. Ask in a single, friendly message — do not pepper
the user with separate questions:

| Info Needed | Why |
|---|---|
| Project / product name | Document title and scope |
| One-line purpose | Introduction / purpose section |
| Target users / stakeholders | User characteristics |
| Core features (functional requirements) | Functional Requirements section |
| Tech stack / platform constraints | Design Constraints |
| Performance expectations | Performance Requirements |
| Any known non-functional needs (security, scalability…) | Non-Functional Attributes |
| Rough timeline / budget (if known) | Preliminary Schedule and Budget |

If the user has already provided most of this in their request, proceed directly to
document generation and make reasonable inferences for any gaps. Mark inferred items
with `> ⚠️ *Assumed — please verify.*`

---

## Document Structure

Generate **exactly** these sections in order. Each section maps to the GfG SRS format.

### 1. Title Block

```
# Software Requirements Specification (SRS)
## <Project Name>

| Field | Value |
|---|---|
| Version | 1.0 |
| Date | <today's date> |
| Status | Draft |
| Author | <user's name if known, else "[Author]"> |
```

---

### 2. Table of Contents

Auto-generated Markdown TOC linking to all sections below.

---

### 3. Introduction

- **3.1 Purpose of this Document** — Why this document exists; who the intended readers are.
- **3.2 Scope of this Document** — What the software does and does not do; business value; estimated development cost and time if known.
- **3.3 Overview** — Brief summary / elevator pitch of the product.

---

### 4. General Description

Cover all of the following:
- Product objective and main goal
- User community characteristics (who uses the system, their technical level)
- Key product features and benefits
- Assumptions and dependencies

---

### 5. Functional Requirements

For **each** functional requirement:

```markdown
#### FR-<N>: <Short Title>
- **Description:** What the system shall do.
- **Inputs:** Data inputs, their source, units of measure, and valid range.
- **Processing:** Logic or calculation the system performs.
- **Outputs:** Expected output and destination.
- **Priority:** High / Medium / Low
```

List requirements in ranked order (highest priority first). Include at minimum 5 FRs.

---

### 6. Interface Requirements

Cover:
- **User Interfaces** — UI type (web, mobile, CLI, GUI), accessibility needs.
- **Hardware Interfaces** — Devices, sensors, peripherals.
- **Software Interfaces** — APIs, third-party services, shared memory, data streams.
- **Communication Interfaces** — Protocols, network types.

---

### 7. Performance Requirements

Specify both:
- **Static Requirements** — Concurrent users, data volume, storage limits (constraints that do not affect runtime execution).
- **Dynamic Requirements** — Response times, throughput, max error rates, latency (runtime execution constraints).

---

### 8. Design Constraints

List restrictions imposed on the design team:
- Regulatory / compliance standards to follow
- Required algorithms or patterns
- Hardware / software limitations
- Specific frameworks, languages, or tools that must be used

---

### 9. Non-Functional Attributes

For each attribute, provide a one-line specification:

| Attribute | Requirement |
|---|---|
| Security | |
| Portability | |
| Reliability | |
| Reusability | |
| Application Compatibility | |
| Data Integrity | |
| Scalability | |
| Maintainability | |

Fill in each row based on the project. Mark unknowns with `TBD`.

---

### 10. Preliminary Schedule and Budget

- **Timeline** — Phase breakdown (e.g., Requirements → Design → Implementation → Testing → Deployment) with estimated durations.
- **Budget** — High-level cost estimate or `TBD`.

Use a Markdown table if timeline details are available.

---

### 11. Appendices

- **Appendix A: Glossary** — Define domain-specific terms, acronyms, abbreviations.
- **Appendix B: References** — Any standards, docs, or sources referenced.
- **Appendix C: Revision History** — Table tracking document changes.

---

## Output Rules

1. **Format**: Pure Markdown (`.md`). No HTML tags. No LaTeX. Use GFM tables and code fences.
2. **Completeness**: Every section must be present. Use `TBD` or `> ⚠️ *Assumed*` for gaps — never skip a section.
3. **Tone**: Precise, professional, third-person ("The system shall…", "Users will be able to…").
4. **File**: Save the output to `/mnt/user-data/outputs/<project-name>-SRS.md` and call `present_files` to share it.
5. **Length**: A good SRS is thorough. Aim for 300–800 lines depending on project complexity.
6. **Inferred content**: When filling in gaps from context, wrap the paragraph/item with a blockquote prefix: `> ⚠️ *Assumed — please verify.*`

---

## Quality Checklist (self-verify before saving)

- [ ] All 9 numbered sections present
- [ ] At least 5 Functional Requirements with FR-N IDs
- [ ] Every FR has: description, inputs, outputs, priority
- [ ] Performance section separates static vs dynamic requirements
- [ ] Non-functional attributes table fully filled (no empty rows)
- [ ] Glossary has at least 3 entries
- [ ] Revision History table present in appendices
- [ ] File saved to outputs and presented to user

---

## Example Opener (for collecting info)

> "I'd love to help you create a professional SRS document! To write it well, could you
> briefly describe: **(1)** What does your software do? **(2)** Who are the main users?
> **(3)** What are the 3–5 most important features? **(4)** Any tech stack preferences or
> constraints? **(5)** Any timeline or budget in mind? Even rough answers work — I'll
> fill in reasonable defaults for anything missing."
