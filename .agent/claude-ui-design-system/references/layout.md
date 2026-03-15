# Layout Management Reference

Complete layout system for Claude-style UI. Covers page shells, grid systems,
spacing, responsive behavior, z-index, and UX flow patterns.

---

## Table of Contents
1. [Page Shells](#1-page-shells)
2. [Spacing System](#2-spacing-system)
3. [Grid & Column Layouts](#3-grid--column-layouts)
4. [Responsive Breakpoints](#4-responsive-breakpoints)
5. [Z-Index Stack](#5-z-index-stack)
6. [Content Regions](#6-content-regions)
7. [UX Flow Patterns](#7-ux-flow-patterns)
8. [Skeleton & Loading States](#8-skeleton--loading-states)
9. [Empty States](#9-empty-states)
10. [Scrolling Behavior](#10-scrolling-behavior)
11. [Tailwind Quick Reference](#11-tailwind-quick-reference)

---

## 1. Page Shells

### Chat App Shell (Claude-style)
```
┌──────────────────────────────────────────────────────┐
│  TOPBAR  56px fixed — logo | nav | user avatar       │
├────────────┬─────────────────────────────────────────┤
│            │                                         │
│  SIDEBAR   │         MAIN CONTENT                   │
│  260px     │         max-width: 768px               │
│  fixed     │         margin: 0 auto                 │
│  left: 0   │         overflow-y: scroll             │
│            │                                         │
│            ├─────────────────────────────────────────┤
│            │  INPUT BAR  sticky bottom               │
└────────────┴─────────────────────────────────────────┘
```

```css
/* App shell */
.app-shell {
  display: grid;
  grid-template-columns: 260px 1fr;
  grid-template-rows: 56px 1fr;
  height: 100dvh;
  overflow: hidden;
}

.topbar {
  grid-column: 1 / -1;
  position: sticky; top: 0; z-index: 10;
  height: 56px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
  display: flex; align-items: center;
  padding: 0 16px;
}

.sidebar {
  grid-row: 2;
  overflow-y: auto;
  border-right: 1px solid var(--border);
  background: var(--bg-base);
  padding: 8px;
}

.main {
  grid-row: 2;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.content-inner {
  max-width: 768px;
  margin: 0 auto;
}

.input-bar {
  position: sticky; bottom: 0;
  padding: 12px 16px 20px;
  background: linear-gradient(to top, var(--bg-base) 85%, transparent);
}
```

### Settings / Dashboard Shell
```
┌──────────────────────────────────────────────────────┐
│  TOPBAR                                              │
├────────────┬─────────────────────────────────────────┤
│  NAV       │  PAGE HEADER (title + actions)          │
│  SIDEBAR   ├─────────────────────────────────────────┤
│  200px     │  CONTENT                                │
│            │  max-width: 720px                       │
│            │  padding: 32px                          │
└────────────┴─────────────────────────────────────────┘
```

```css
.settings-shell {
  display: grid;
  grid-template-columns: 200px 1fr;
  min-height: 100dvh;
}

.settings-nav {
  padding: 24px 8px;
  border-right: 1px solid var(--border);
  position: sticky; top: 0; height: 100dvh;
  overflow-y: auto;
}

.settings-content {
  padding: 32px 40px;
  max-width: 760px;
}

.page-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border);
  display: flex; justify-content: space-between; align-items: flex-start;
}
```

### Full-Width / Landing Shell
```css
.landing-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero section */
.hero {
  text-align: center;
  padding: 80px 0 64px;
  max-width: 680px;
  margin: 0 auto;
}

/* Feature grid */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  padding: 48px 0;
}
```

---

## 2. Spacing System

Always use these token values. Never use arbitrary px values.

```css
:root {
  --space-1:  4px;   /* xs — icon gaps, tight internal padding */
  --space-2:  8px;   /* sm — between label and input, icon button padding */
  --space-3:  12px;  /* — nav item padding, compact card padding */
  --space-4:  16px;  /* md — card padding, list item gap */
  --space-5:  20px;  /* — section internal gap */
  --space-6:  24px;  /* lg — page edge padding, modal padding */
  --space-8:  32px;  /* — between sections */
  --space-10: 40px;  /* xl — major section gap */
  --space-12: 48px;  /* — hero/landing breathing room */
  --space-16: 64px;  /* 2xl — top-of-page, feature sections */
}
```

### Spacing Rules
- **Component internal padding:** `--space-3` to `--space-4`
- **Between sibling components:** `--space-4` to `--space-5`  
- **Between sections:** `--space-8` to `--space-10`
- **Page edge padding:** `--space-6` minimum (goes to `--space-10` on wide screens)
- **Form fields gap:** `--space-4` between fields, `--space-2` between label and input

### Density Modes
```css
/* Compact (dense UIs, tables, sidebar nav) */
--density-compact: 0.8;
padding: calc(var(--space-3) * var(--density-compact)); /* = 9.6px */

/* Comfortable (default) */
/* Use token values as-is */

/* Spacious (settings, onboarding, landing) */
--density-spacious: 1.25;
padding: calc(var(--space-6) * var(--density-spacious)); /* = 30px */
```

---

## 3. Grid & Column Layouts

### Content Grid (most common)
```css
/* Single column centered — use for all conversational/reading content */
.content-grid {
  display: flex;
  flex-direction: column;
  max-width: 768px;
  margin: 0 auto;
  gap: var(--space-4);
}
```

### Card Grid
```css
/* Auto-fill cards — adapts to container width */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

/* Fixed 2-col */
.two-col { grid-template-columns: 1fr 1fr; }

/* Fixed 3-col */
.three-col { grid-template-columns: repeat(3, 1fr); }

/* Asymmetric (main + aside) */
.main-aside { grid-template-columns: 1fr 320px; gap: 32px; }
```

### Form Layout
```css
/* Label + input rows (settings-style) */
.form-grid {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: var(--space-2) var(--space-6);
  align-items: start;
}

.form-label {
  padding-top: 9px; /* align with input text */
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

/* Stacked form (mobile / narrow) */
.form-stacked {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}
```

### Split Layout (two equal panels)
```css
.split {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  height: 100dvh;
}

/* e.g. auth page: illustration | form */
.split-left  { background: var(--bg-base); padding: 48px; }
.split-right { background: var(--bg-surface); padding: 48px; display: flex; align-items: center; }
```

---

## 4. Responsive Breakpoints

```css
/* Breakpoint tokens */
--bp-sm: 640px;
--bp-md: 768px;
--bp-lg: 1024px;
--bp-xl: 1280px;
```

### Behavior per breakpoint

**`< 640px` — Mobile**
- Sidebar: hidden by default, opens as full-screen overlay/drawer
- Content: single column, `padding: 0 16px`
- Cards: single column `minmax(100%, 1fr)`
- Topbar: hamburger menu replaces nav links
- Form: stacked layout (label above input)
- Modals: full-screen bottom sheet

**`640px–1024px` — Tablet**
- Sidebar: collapsed to icon-only rail (`56px`) or hidden with toggle
- Content: `max-width: 640px`, `padding: 0 24px`
- Cards: 2 columns
- Modals: centered, `max-width: 480px`, standard

**`> 1024px` — Desktop**
- Sidebar: full `260px`, always visible
- Content: `max-width: 768px`, `padding: 0 32px`
- Cards: 3 columns (up to 4 on xl)
- Settings: two-column form layout

```css
/* Mobile-first media queries */
@media (min-width: 640px) {
  .card-grid { grid-template-columns: repeat(2, 1fr); }
  .sidebar   { display: flex; } /* show rail */
}

@media (min-width: 1024px) {
  .app-shell { grid-template-columns: 260px 1fr; }
  .card-grid { grid-template-columns: repeat(3, 1fr); }
  .form-grid { grid-template-columns: 200px 1fr; }
}

/* Sidebar mobile overlay */
.sidebar-overlay {
  position: fixed; inset: 0; z-index: 200;
  background: rgba(0,0,0,0.5);
  display: none;
}
@media (max-width: 1023px) {
  .sidebar { position: fixed; left: 0; top: 0; height: 100dvh; z-index: 201; transform: translateX(-100%); transition: transform 300ms cubic-bezier(0.16,1,0.3,1); }
  .sidebar.open { transform: translateX(0); }
  .sidebar-overlay.open { display: block; }
}
```

---

## 5. Z-Index Stack

Never use arbitrary z-index values. Use this ladder only.

```css
:root {
  --z-base:       1;    /* normal stacking context */
  --z-raised:     5;    /* cards on hover, minor float */
  --z-sticky:     10;   /* sticky headers, input bar, tab bars */
  --z-dropdown:   100;  /* dropdowns, tooltips, popovers */
  --z-modal:      200;  /* dialogs, modals */
  --z-toast:      300;  /* toasts, snackbars */
  --z-overlay:    400;  /* fullscreen overlays, onboarding */
  --z-debug:      9999; /* dev tools only */
}
```

### Usage Guide
- Sidebar (mobile overlay): `var(--z-modal)` + 1 = `201`
- Backdrop behind modal: `var(--z-modal)` - 1 = `199`
- Sticky topbar: `var(--z-sticky)`
- Tooltips on top of modals: `var(--z-toast)` - 1 = `299`

---

## 6. Content Regions

### Message / Bubble Layout
```css
/* Assistant message */
.msg-assistant {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  max-width: 100%;
}

.msg-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 2px;
}

.msg-body {
  flex: 1;
  min-width: 0; /* prevent overflow */
  font-size: 15px;
  line-height: 1.7;
}

/* User message */
.msg-user {
  display: flex;
  justify-content: flex-end;
}

.msg-user .msg-body {
  background: var(--bg-elevated);
  border-radius: 18px 18px 4px 18px;
  padding: 10px 16px;
  max-width: 80%;
}
```

### Sidebar Nav Item
```css
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 10px;
  border-radius: 7px;
  font-size: 14px;
  color: var(--text-muted);
  cursor: pointer;
  transition: background 150ms ease, color 150ms ease;
  min-height: 34px;
  /* Truncate long titles */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-item:hover { background: rgba(0,0,0,0.05); color: var(--text-primary); }
.nav-item.active { background: var(--accent-subtle); color: var(--text-primary); font-weight: 500; }
```

### Section Header
```css
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.section-action {
  font-size: 13px;
  color: var(--accent);
  cursor: pointer;
}
```

### Divider
```css
.divider {
  height: 1px;
  background: var(--border);
  margin: var(--space-5) 0;
}

.divider-labeled {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
  font-size: 12px;
}
.divider-labeled::before,
.divider-labeled::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}
```

---

## 7. UX Flow Patterns

### Principle: One Primary Action Per View
Every screen should have **exactly one** primary CTA. Support actions are secondary or ghost.
```
✅  [Primary Action]   [Secondary]
❌  [Primary Action]   [Another Primary]
```

### Progressive Disclosure
Reveal complexity on demand, never upfront.
```
Level 1: Show summary / collapsed state
Level 2: User expands / clicks to reveal detail
Level 3: Opens modal/panel for full editing
```

### Conversational Flow (Chat UX)
```
1. Message list — scrolls to bottom on new message
2. Typing indicator — appears while assistant responds
3. Input bar — always accessible, never buried
4. Actions on hover — copy/edit/feedback on message hover only
5. Scroll-to-bottom FAB — appears when user scrolled up
```

```css
/* Scroll to bottom button */
.scroll-fab {
  position: fixed;
  bottom: 100px; /* above input bar */
  left: 50%;
  transform: translateX(-50%);
  z-index: var(--z-sticky);
  opacity: 0;
  pointer-events: none;
  transition: opacity 200ms ease;
}
.scroll-fab.visible {
  opacity: 1;
  pointer-events: auto;
}
```

### Confirmation Pattern (Destructive Actions)
Never delete/clear immediately. Use a 2-step confirm:
```
Step 1: Ghost "Delete" button
Step 2: Inline confirm → [Cancel] [Confirm Delete]  (timeout resets to step 1)
```

### Form Submission Flow
```
1. Idle      — all fields enabled, primary button enabled
2. Validating — inline errors appear on blur (not submit)
3. Submitting — button shows spinner, fields disabled
4. Success   — toast confirms, form clears or navigates
5. Error     — inline error, form re-enabled, user can retry
```

---

## 8. Skeleton & Loading States

Use skeleton screens for content areas > 2 lines. Spinners only for actions (buttons, inline).

```css
/* Skeleton base */
.skeleton {
  background: linear-gradient(
    90deg,
    var(--border) 25%,
    rgba(0,0,0,0.04) 50%,
    var(--border) 75%
  );
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
  border-radius: 4px;
}

@keyframes shimmer {
  0%   { background-position: 100% 50%; }
  100% { background-position: 0%   50%; }
}

/* Skeleton variants */
.skeleton-text  { height: 14px; border-radius: 4px; }
.skeleton-title { height: 20px; width: 60%; border-radius: 4px; }
.skeleton-avatar{ width: 32px; height: 32px; border-radius: 50%; }
.skeleton-card  { height: 120px; border-radius: 10px; }
```

### Message Skeleton
```css
/* Mimics the message layout while loading */
.skeleton-message {
  display: flex; gap: 12px; align-items: flex-start;
}
.skeleton-message .lines {
  flex: 1;
  display: flex; flex-direction: column; gap: 8px;
}
.skeleton-message .line-1 { width: 80%; }
.skeleton-message .line-2 { width: 60%; }
.skeleton-message .line-3 { width: 40%; }
```

---

## 9. Empty States

Every empty state needs: an icon, a message, and an **action**.

```css
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--space-16) var(--space-6);
  gap: var(--space-4);
  min-height: 300px;
}

.empty-icon {
  width: 48px; height: 48px;
  color: var(--text-muted);
  opacity: 0.5;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.empty-desc {
  font-size: 14px;
  color: var(--text-muted);
  max-width: 320px;
  line-height: 1.6;
}
/* Follow with a Primary or Secondary button CTA */
```

---

## 10. Scrolling Behavior

```css
/* Smooth scroll everywhere */
html { scroll-behavior: smooth; }

/* Scroll containers */
.scroll-area {
  overflow-y: auto;
  overscroll-behavior: contain; /* prevent scroll chaining */
  -webkit-overflow-scrolling: touch; /* iOS momentum */
}

/* Hide scrollbar but keep function */
.scroll-area::-webkit-scrollbar { width: 6px; }
.scroll-area::-webkit-scrollbar-track { background: transparent; }
.scroll-area::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.15);
  border-radius: 999px;
}
.scroll-area::-webkit-scrollbar-thumb:hover { background: rgba(0,0,0,0.25); }

/* Input bar fade — content fades into input area */
.input-fade {
  position: sticky; bottom: 0;
  background: linear-gradient(
    to top,
    var(--bg-base) 70%,
    transparent 100%
  );
  padding-top: 24px;
}

/* Sticky section headers inside scroll area */
.scroll-section-header {
  position: sticky; top: 0;
  background: var(--bg-base);
  padding: 8px 0 4px;
  z-index: var(--z-sticky);
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--text-muted);
}
```

---

## 11. Tailwind Quick Reference

```html
<!-- App shell -->
<div class="grid h-dvh overflow-hidden" style="grid-template-columns: 260px 1fr; grid-template-rows: 56px 1fr;">

<!-- Topbar -->
<header class="col-span-2 flex items-center px-4 border-b border-stone-200 bg-white sticky top-0 z-10 h-14">

<!-- Sidebar -->
<aside class="border-r border-stone-200 bg-stone-50 overflow-y-auto p-2">

<!-- Main content -->
<main class="flex flex-col overflow-hidden">
  <div class="flex-1 overflow-y-auto p-6">
    <div class="max-w-3xl mx-auto"> <!-- 768px -->

<!-- Input bar -->
<div class="sticky bottom-0 px-4 pb-5 pt-3 bg-gradient-to-t from-stone-50 to-transparent">

<!-- Card grid -->
<div class="grid gap-4" style="grid-template-columns: repeat(auto-fill, minmax(280px, 1fr))">

<!-- Form grid -->
<div class="grid gap-x-6 gap-y-3" style="grid-template-columns: 200px 1fr">

<!-- Nav item -->
<a class="flex items-center gap-2.5 px-2.5 py-1.5 rounded-lg text-sm text-stone-500 hover:bg-black/5 hover:text-stone-900 transition-colors">

<!-- Empty state -->
<div class="flex flex-col items-center justify-center text-center py-20 px-6 gap-4 min-h-[300px]">
```
