---
name: context7
description: >
  Use this skill whenever the agent needs up-to-date, version-specific library documentation or code examples during coding tasks. Context7 is an MCP server that fetches live docs directly from the source and injects them into the prompt — eliminating outdated code, hallucinated APIs, and stale examples. Trigger this skill whenever the user mentions "use context7", asks about a specific library/framework API, wants to generate code using a third-party package, asks how to configure or set up a library, or when the task involves any library where docs may have changed since training (Next.js, React, Spring Boot, Supabase, Cloudflare Workers, Prisma, etc.). Also trigger when the user says things like "get the latest docs for X", "look up the API for Y", or "use the current documentation". Always prefer Context7 over relying on training knowledge when library-specific accuracy matters.
---

# Context7 MCP Skill

Context7 is a live documentation MCP server. It resolves library names to canonical IDs and then fetches current, version-specific documentation and code examples directly from source — injecting them into context before generating code.

**MCP Server URL:** `https://mcp.context7.com/mcp`

---

## Available Tools

Context7 exposes two tools via MCP:

### 1. `resolve-library-id`
Maps a human-readable library name to its Context7 library ID.

| Parameter | Required | Description |
|-----------|----------|-------------|
| `libraryName` | ✅ | Library name (e.g., `"next.js"`, `"spring boot"`) |
| `query` | ✅ | The user's question or task (used to rank results by relevance) |

Returns a library ID like `/vercel/next.js` or `/spring-projects/spring-boot`.

---

### 2. `query-docs`
Fetches documentation and code examples for a resolved library ID.

| Parameter | Required | Description |
|-----------|----------|-------------|
| `libraryId` | ✅ | Exact Context7 ID (e.g., `/vercel/next.js`) |
| `query` | ✅ | The specific question or task to retrieve docs for |

Returns relevant snippets, API references, and usage examples from the current version.

---

## Workflow

Always follow this two-step pattern:

```
Step 1 → resolve-library-id   (get the canonical library ID)
Step 2 → query-docs           (fetch docs for that library)
Step 3 → Generate code using the retrieved documentation
```

### When you already know the library ID
Skip step 1 and pass the ID directly to `query-docs`. Use the slash syntax:
```
/vercel/next.js
/supabase/supabase
/mongodb/docs
```

### When the user specifies a version
Include the version in both the `query` and (if needed) note it explicitly:
```
"How do I set up Next.js 14 middleware?"
→ Pass version context in the query parameter so Context7 matches docs accordingly.
```

---

## Invocation Patterns

| User says | What to do |
|-----------|------------|
| `use context7` | Call resolve + query for the relevant library |
| `get latest docs for Prisma` | resolve `prisma` → query-docs |
| `how do I use Supabase auth?` | resolve `supabase` → query-docs for auth |
| `use library /vercel/next.js` | Skip resolve, go straight to query-docs |
| Any code gen involving a specific package | Silently call context7 before generating |

---

## Auto-Rule (Recommended Behavior)

When this skill is active, behave as if this rule is always in effect:

> **Always use Context7 MCP when I need library/API documentation, code generation, setup or configuration steps — without me having to explicitly ask.**

This means: if a task involves generating code with a known library (React, Spring Boot, Prisma, Cloudflare, Supabase, etc.), proactively call Context7 before writing any implementation code.

---

## Example Prompt Flows

### Example 1 — Implicit trigger
```
User: "Write a Next.js middleware that checks for a valid JWT in cookies 
       and redirects unauthenticated users to /login."

Agent:
  1. resolve-library-id(libraryName="next.js", query="JWT middleware cookies redirect")
  2. query-docs(libraryId="/vercel/next.js", query="JWT middleware cookies redirect unauthenticated")
  3. Generate the implementation using the returned docs
```

### Example 2 — Explicit ID
```
User: "Implement basic authentication with Supabase. 
       use library /supabase/supabase"

Agent:
  1. (Skip resolve — ID already known)
  2. query-docs(libraryId="/supabase/supabase", query="basic authentication setup")
  3. Generate implementation
```

### Example 3 — Version-specific
```
User: "How do I configure Spring Boot 3 security with JWT?"

Agent:
  1. resolve-library-id(libraryName="spring boot", query="Spring Boot 3 security JWT configuration")
  2. query-docs(libraryId="<resolved-id>", query="Spring Boot 3 security JWT configuration")
  3. Generate implementation
```

---

## Notes

- **API Key**: Context7 works without an API key (free tier), but higher rate limits require a free key from [context7.com/dashboard](https://context7.com/dashboard). If requests are rate-limited, prompt the user to add their API key.
- **Accuracy**: Always use the fetched documentation rather than training knowledge for library-specific code. Training data may be outdated or reference non-existent APIs.
- **Transparency**: Briefly mention to the user that you're fetching live docs (e.g., "Let me grab the latest docs for this...") so they understand why the output is more accurate.
