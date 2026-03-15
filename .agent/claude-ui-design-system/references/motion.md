# Motion & Animation Reference

Animation specs for Claude UI components.

## Core Easing Curves

```css
/* Primary — snappy with soft landing (most common) */
--ease-spring: cubic-bezier(0.16, 1, 0.3, 1);

/* Smooth in-out — for panels, drawers */
--ease-smooth: cubic-bezier(0.4, 0, 0.2, 1);

/* Quick exit — for dismissals */
--ease-out: cubic-bezier(0, 0, 0.2, 1);

/* Entrance */
--ease-in: cubic-bezier(0.4, 0, 1, 1);
```

## Duration Scale

```css
--duration-instant: 80ms;   /* focus rings, immediate feedback */
--duration-fast:    150ms;  /* hovers, color changes */
--duration-normal:  200ms;  /* micro-interactions, icon swaps */
--duration-medium:  250ms;  /* component entries */
--duration-slow:    350ms;  /* modals, panels, page transitions */
```

## Component-Specific Animations

### Buttons & Interactive Elements
```css
/* Color/background transition */
transition: background-color 150ms ease, 
            border-color 150ms ease,
            color 150ms ease,
            box-shadow 150ms ease;

/* Never animate transform on buttons */
```

### Input Focus Ring
```css
transition: box-shadow 150ms cubic-bezier(0.16, 1, 0.3, 1),
            border-color 150ms ease;
/* Focus: box-shadow appears */
/* Blur: box-shadow disappears */
```

### Toggle Switch
```css
/* Thumb movement */
transition: transform 200ms cubic-bezier(0.16, 1, 0.3, 1);
/* Track color */
transition: background-color 200ms ease;
```

### Modal / Dialog
```css
/* Backdrop */
@keyframes backdropIn {
  from { opacity: 0; }
  to   { opacity: 1; }
}
animation: backdropIn 200ms ease forwards;

/* Panel */
@keyframes modalIn {
  from { opacity: 0; transform: scale(0.96) translateY(4px); }
  to   { opacity: 1; transform: scale(1)    translateY(0); }
}
animation: modalIn 250ms cubic-bezier(0.16, 1, 0.3, 1) forwards;

/* Exit (reversed, faster) */
@keyframes modalOut {
  from { opacity: 1; transform: scale(1); }
  to   { opacity: 0; transform: scale(0.96); }
}
animation: modalOut 150ms ease forwards;
```

### Toast / Notification
```css
/* Entry — slides up from bottom */
@keyframes toastIn {
  from { opacity: 0; transform: translateY(8px) scale(0.97); }
  to   { opacity: 1; transform: translateY(0)   scale(1); }
}
animation: toastIn 200ms cubic-bezier(0.16, 1, 0.3, 1) forwards;

/* Exit */
@keyframes toastOut {
  from { opacity: 1; transform: translateY(0); }
  to   { opacity: 0; transform: translateY(-4px); }
}
animation: toastOut 150ms ease forwards;
```

### Dropdown / Menu
```css
@keyframes menuIn {
  from { opacity: 0; transform: translateY(-4px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0)    scale(1); }
}
animation: menuIn 150ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
transform-origin: top center; /* or top left/right depending on position */
```

### Checkbox / Radio Check
```css
/* SVG path draw animation */
@keyframes checkDraw {
  from { stroke-dashoffset: 20; }
  to   { stroke-dashoffset: 0; }
}
stroke-dasharray: 20;
animation: checkDraw 150ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
```

### Spinner / Loading
```css
@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}
animation: spin 700ms linear infinite;
/* Use a partial circle SVG (not full circle) for better visual */
```

### Sidebar / Drawer
```css
@keyframes slideIn {
  from { transform: translateX(-100%); }
  to   { transform: translateX(0); }
}
animation: slideIn 300ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
```

---

## Anti-Patterns (Never Do)

- ❌ `transform: scale()` on button hover/click
- ❌ Bounce easing (`cubic-bezier` with values > 1)
- ❌ Animation durations > 400ms for UI interactions
- ❌ Animating `width`, `height`, `top`, `left` (use `transform` instead)
- ❌ `transition: all` — always specify properties
- ❌ Motion for motion's sake — every animation must serve a purpose

## Accessibility

```css
/* Respect user's motion preferences */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```
