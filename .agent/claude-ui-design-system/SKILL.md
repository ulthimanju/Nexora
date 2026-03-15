---
name: claude-ui-design-system
description: >
  A complete reference skill for designing and building UI components and layouts
  that match Claude's design language — including buttons, inputs, form controls,
  cards, modals, toasts, badges, tabs, menus, icons, typography, color tokens,
  motion/animation specs, AND layout management (page shells, sidebars, content
  areas, grid systems, spacing, responsive breakpoints, stacking order, and UX
  flow patterns). Use this skill whenever the user asks to build, replicate, or
  design UI components or layouts that look like Claude, asks about Claude's design
  system, wants components or pages styled like Claude.ai, wants a design system,
  component library, or layout scaffold. Also trigger for terms like "Claude-style",
  "Anthropic UI", "warm neutral design", "sidebar layout", "app shell", "content
  layout", "responsive layout", "spacing system", "grid", or "page structure".
  Even for casual requests like "make it look like Claude", "build a clean layout",
  "how should I structure this page", or "make the UX better", use this skill.
---

# Claude UI Design System

A complete guide for building UI components that match Claude's visual language.
Claude's design philosophy is **functional warmth** — calm, readable, nothing
fights for attention. Components recede so *content* is the focus. The aesthetic
leans editorial/publishing rather than typical SaaS.

Before building any component, read the relevant section below. For large builds
(full component kits, design systems), also read:
- `references/tokens.md` — complete CSS variable token definitions
- `references/motion.md` — animation specs per component
- `references/layout.md` — page shells, grid systems, responsive breakpoints, spacing system, z-index, and UX flow patterns

---

## Core Design Principles

- **Warm neutral palette** — avoid cold blue-gray. Use off-white, warm dark tones.
- **Understated interactions** — hover states are gentle tints, not dramatic shifts.
- **No gradients on interactive elements** — flat fills only.
- **Rounded but not pill** — `border-radius: 6–8px` for most components, `12px` for inputs/modals.
- **Borders are whispers** — `1px` at `rgba(0,0,0,0.10)` or lower.
- **Typography drives hierarchy** — weight + size, not color chaos.
- **Icons are line-style** — stroke-based, `1.5px` weight, rounded caps/joins.

---

## Buttons

### Hierarchy (use in this order of visual weight)

**Primary**
```css
background: #D97706; /* amber-600 */
color: #ffffff;
border-radius: 6px;
padding: 10px 18px;
font-weight: 500;
border: none;
/* Hover: darken bg by ~10% */
/* Active: inset shadow */
```

**Secondary**
```css
background: transparent;
border: 1px solid rgba(0,0,0,0.20); /* light mode */
border: 1px solid rgba(255,255,255,0.15); /* dark mode */
color: inherit;
border-radius: 6px;
padding: 10px 18px;
/* Hover: low-opacity bg tint */
```

**Ghost / Tertiary**
```css
background: transparent;
border: none;
color: inherit;
padding: 10px 18px;
/* Hover: background chip appears */
```

**Danger**
```css
background: #DC2626; /* red-600 */
color: #ffffff;
/* Same geometry as Primary */
/* Use only for destructive actions */
```

**Icon Button**
```css
width: 32px; height: 32px; /* or 36x36 */
background: transparent;
border: none;
border-radius: 6px;
display: flex; align-items: center; justify-content: center;
/* Hover: gray chip background */
```

**Loading State**
- Replace text/icon with spinner (same width, no layout shift)
- `opacity: 0.6`, `pointer-events: none`

---

## Inputs & Form Controls

### Text Input
```css
border: 1px solid rgba(0,0,0,0.15);      /* light */
border: 1px solid rgba(255,255,255,0.12); /* dark */
border-radius: 6px;
padding: 8px 12px;
background: /* slightly tinted from page bg */
font-size: 14px;
/* Focus ring: */
box-shadow: 0 0 0 3px rgba(217, 119, 6, 0.20);
outline: none;
```
- Placeholder: `color: rgba(0,0,0,0.35)` / `rgba(255,255,255,0.30)`
- No floating labels — placeholder only
- States: Default → Focused (ring) → Disabled (`opacity: 0.5`, `cursor: not-allowed`)

### Textarea (Chat-style)
```css
border-radius: 12px;
resize: none; /* auto-grow with JS */
padding: 12px 48px 12px 16px; /* right pad for send button */
box-shadow: 0 1px 4px rgba(0,0,0,0.08);
/* Send button absolutely positioned bottom-right */
```

### Select / Dropdown
- Visually identical to text input
- Custom chevron icon via `appearance: none` + background SVG
- Dropdown panel: `border-radius: 8px`, `box-shadow: 0 4px 20px rgba(0,0,0,0.12)`

### Checkbox
```css
/* Custom square */
width: 16px; height: 16px;
border-radius: 3px;
border: 1.5px solid rgba(0,0,0,0.25);
/* Checked: bg #D97706, white checkmark icon */
transition: all 150ms ease;
```

### Toggle / Switch
```css
/* Track */
width: 36px; height: 20px;
border-radius: 999px;
background: #9CA3AF; /* off */
background: #D97706; /* on */
/* Thumb: white circle, slides with spring ease */
transition: transform 200ms cubic-bezier(0.16, 1, 0.3, 1);
```

### Radio Button
- Circle variant of checkbox
- Selected: accent fill dot inside ring
- Group gap: `8–12px`

---

## Cards & Surfaces

### Base Card
```css
background: #ffffff; /* light */ / #242320; /* dark */
border-radius: 10px;
border: 1px solid rgba(0,0,0,0.08);
box-shadow: 0 1px 3px rgba(0,0,0,0.06);
padding: 16px; /* or 24px for larger cards */
```

### Interactive / Hover Card
```css
/* On hover: */
border-color: rgba(0,0,0,0.14);
box-shadow: 0 2px 8px rgba(0,0,0,0.09);
transition: all 150ms ease;
/* No scale — very subtle */
```

### Conversation / Sidebar Item
```css
border-radius: 8px;
padding: 8px 12px;
/* Hover: background tint */
/* Active/Selected: rgba(217,119,6,0.10) background, bolder text */
```

---

## Modals & Dialogs

```css
/* Backdrop */
background: rgba(0,0,0,0.40);
backdrop-filter: blur(4px);

/* Panel */
border-radius: 12px;
max-width: 480px; /* or 560px */
padding: 24px; /* or 32px */
box-shadow: 0 20px 60px rgba(0,0,0,0.15);
```

### Anatomy
1. **Header** — title (`font-size: 18px`, `font-weight: 600`) + optional subtitle + `×` close button top-right
2. **Body** — scrollable if needed, `padding-top: 16px`
3. **Footer** — right-aligned, Primary + Secondary buttons side by side, `gap: 8px`

### Entry/Exit Animation
```css
/* Entry */
animation: modalIn 200ms cubic-bezier(0.16, 1, 0.3, 1);
@keyframes modalIn {
  from { opacity: 0; transform: scale(0.96); }
  to   { opacity: 1; transform: scale(1); }
}
/* Exit: reverse, ~150ms */
```

---

## Toasts & Notifications

### Toast
```css
border-radius: 8px;
padding: 10px 16px;
box-shadow: 0 4px 16px rgba(0,0,0,0.12);
display: flex; align-items: center; gap: 10px;
/* Position: bottom-center or top-right */
/* Auto-dismiss: 3–5 seconds */
```
- Icon left (✓ / ✗ / ℹ) + text + optional action link
- Stacks if multiple appear (translate Y per item)

### Inline Alert / Banner
```css
border-radius: 6px;
border-left: 3px solid <type-color>;
background: <type-color at 0.06 opacity>;
padding: 10px 14px;
display: flex; gap: 10px;
```
- Types: success `#16A34A`, warning `#D97706`, error `#DC2626`, info `#2563EB`

---

## Badges & Tags

### Badge (Status)
```css
border-radius: 999px;
padding: 2px 8px;
font-size: 11px;
font-weight: 500;
/* Type colors — light bg tint + darker text */
/* success: bg #DCFCE7, text #15803D */
/* warning: bg #FEF3C7, text #92400E */
/* error:   bg #FEE2E2, text #B91C1C */
/* neutral: bg #F3F4F6, text #374151 */
```

### Tag (Content)
```css
border-radius: 999px;
padding: 4px 10px;
font-size: 12px;
background: rgba(0,0,0,0.06);
/* Dismissible: × icon on right */
```

---

## Tabs

```css
/* Tab list: flex row, border-bottom: 1px solid <border-color> */
/* Tab item */
padding: 8px 0;
margin-right: 20px;
font-size: 14px;
font-weight: 400;
color: var(--text-muted);
border-bottom: 2px solid transparent;
cursor: pointer;

/* Active tab */
font-weight: 500;
color: var(--text-primary);
border-bottom-color: #D97706;

/* Hover */
color: var(--text-primary);
```
- No background fills on tabs — underline only
- Transition: `border-color 150ms ease`, `color 150ms ease`

---

## Dropdown Menus & Context Menus

```css
/* Panel */
border-radius: 8px;
border: 1px solid var(--border);
box-shadow: 0 4px 20px rgba(0,0,0,0.12);
padding: 4px;
min-width: 160px;

/* Item */
border-radius: 5px;
padding: 6px 10px;
font-size: 14px;
height: 34px;
display: flex; align-items: center; gap: 8px;
/* Hover: rgba(0,0,0,0.05) background */

/* Destructive item */
color: #DC2626;

/* Divider */
height: 1px;
background: var(--border);
margin: 4px 0;
```

---

## Icons

### Spec
- **Style:** Line / stroke-based (not filled)
- **Stroke:** `1.5px`, `stroke-linecap: round`, `stroke-linejoin: round`
- **Grid:** `24×24px` (default), `20×20px` (compact), `16×16px` (dense)
- **Color:** `currentColor` — inherits text color automatically

### Icon Categories
| Category | Icons |
|----------|-------|
| Actions | Copy, Edit, Regenerate, Delete, Share, Download, Pin |
| Navigation | Menu/Sidebar, Settings, Home, Back arrow |
| Communication | Send, Attach, Microphone |
| Feedback | Thumbs up, Thumbs down, Flag |
| Status | Check, X, Warning triangle, Info circle, Spinner |
| UI | Chevron (×4), Expand, Collapse, Search, Close |

### Usage Rules
- Always wrap interactive icons in **Icon Button** component
- Never scale with CSS — use correct size variant
- Use `aria-label` on icon buttons for accessibility

---

## Typography Scale

| Role | Size | Weight | Usage |
|------|------|--------|-------|
| Display | 24–28px | 600 | Page/modal titles |
| Heading | 18–20px | 600 | Section headers |
| Subheading | 15–16px | 500 | Card titles, labels |
| Body | 14–15px | 400 | Main content |
| Caption | 12–13px | 400 | Meta, timestamps |
| Code | 13px | 400 | Monospace (JetBrains Mono / Fira Code) |

**Font stack:** Use a warm humanist sans-serif. Avoid Inter, Roboto, Arial.
Good choices: `Geist`, `DM Sans`, `Instrument Sans`, `Plus Jakarta Sans`.

---

## Color Tokens

See `references/tokens.md` for the complete token list. Quick reference:

| Token | Light | Dark |
|-------|-------|------|
| `--bg-base` | `#F9F7F4` | `#1A1917` |
| `--bg-surface` | `#FFFFFF` | `#242320` |
| `--bg-elevated` | `#FFFFFF` | `#2E2C29` |
| `--border` | `rgba(0,0,0,0.10)` | `rgba(255,255,255,0.10)` |
| `--text-primary` | `#1A1917` | `#F0EDE8` |
| `--text-muted` | `#6B6863` | `#8C8880` |
| `--accent` | `#D97706` | `#F59E0B` |
| `--danger` | `#DC2626` | `#EF4444` |

---

## Motion & Transitions

See `references/motion.md` for full animation specs. Quick reference:

- **Default easing:** `cubic-bezier(0.16, 1, 0.3, 1)` — snappy with soft landing
- **Micro-interactions:** `120–200ms`
- **Panels / Modals:** `250–350ms`
- **Always:** animate `opacity` + `transform` together
- **Never:** bounce physics, dramatic scale, attention-grabbing effects

---

## Layout Management

> For the full layout reference, see `references/layout.md`. Use the summary below
> for quick decisions — load the full file for page shells, responsive grids, or
> complex multi-panel layouts.

### Page Shell (App-level)
```
┌─────────────────────────────────────────┐
│  Topbar (fixed, 56px)                   │
├──────────┬──────────────────────────────┤
│ Sidebar  │  Main Content Area           │
│ (260px)  │  max-width: 768px centered   │
│ fixed    │  padding: 24–48px            │
│          ├──────────────────────────────┤
│          │  Input Bar (sticky bottom)   │
└──────────┴──────────────────────────────┘
```
- Sidebar: `width: 260px`, collapsible to `0` on mobile
- Content: `max-width: 768px`, `margin: 0 auto`, `padding: 0 24px`
- Input bar: `position: sticky; bottom: 0` with top blur fade

### Spacing System
| Name | Value | Use |
|------|-------|-----|
| `--space-xs` | `4px` | Icon gaps, tight chips |
| `--space-sm` | `8px` | Between label + input |
| `--space-md` | `16px` | Card padding, section gaps |
| `--space-lg` | `24px` | Page padding, modal padding |
| `--space-xl` | `40px` | Between major sections |
| `--space-2xl`| `64px` | Top-of-page breathing room |

### Grid System
- **Content:** Single-column, `max-width: 768px`, centered
- **Cards:** CSS Grid, `repeat(auto-fill, minmax(280px, 1fr))`, `gap: 16px`
- **Settings/Forms:** Two-column `label | input`, `grid-template-columns: 200px 1fr`
- **Never** use more than 3 columns in main content areas

### Responsive Breakpoints
| Name | Width | Behavior |
|------|-------|----------|
| `sm` | `< 640px` | Sidebar hidden, single column, compact padding |
| `md` | `640–1024px` | Sidebar as overlay/drawer |
| `lg` | `> 1024px` | Full sidebar visible, standard layout |

### Z-Index Stack
```
1    — Base content
10   — Sticky elements (input bar, tab bars)
100  — Dropdowns, tooltips
200  — Modals, dialogs
300  — Toasts, notifications
400  — Fullscreen overlays
```

### UX Flow Rules
- **One primary action** per view — never two competing CTAs
- **Sticky input** at bottom for conversational flows
- **Progressive disclosure** — show details on demand, not upfront
- **Empty states** must have an action — never a blank void
- **Loading states** use skeleton screens, not spinners for large areas

---



### React / Tailwind
- Use Tailwind's `amber-600` for accent, `stone-*` for neutrals
- `rounded-md` = `6px`, `rounded-lg` = `8px`, `rounded-xl` = `12px`
- For focus rings: `focus:ring-2 focus:ring-amber-500/20 focus:outline-none`

### CSS Variables Setup
Always define tokens as CSS variables on `:root` and `[data-theme="dark"]`.
Full token definitions are in `references/tokens.md`.

### Accessibility
- All interactive elements need `:focus-visible` styles
- Icon buttons require `aria-label`
- Form inputs require associated `<label>` elements
- Color contrast: text on backgrounds must meet WCAG AA (4.5:1 minimum)
