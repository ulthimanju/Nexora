# Color Tokens Reference

Complete CSS variable definitions for the Claude UI design system.

## CSS Setup

```css
:root {
  /* Backgrounds */
  --bg-base:     #F9F7F4;
  --bg-surface:  #FFFFFF;
  --bg-elevated: #FFFFFF;
  --bg-overlay:  rgba(0, 0, 0, 0.40);

  /* Borders */
  --border:        rgba(0, 0, 0, 0.10);
  --border-strong: rgba(0, 0, 0, 0.18);
  --border-focus:  rgba(217, 119, 6, 0.40);

  /* Text */
  --text-primary:   #1A1917;
  --text-secondary: #44403C;
  --text-muted:     #6B6863;
  --text-disabled:  #A8A29E;
  --text-inverse:   #FFFFFF;

  /* Accent (Amber/Orange) */
  --accent:         #D97706;
  --accent-hover:   #B45309;
  --accent-subtle:  rgba(217, 119, 6, 0.08);
  --accent-ring:    rgba(217, 119, 6, 0.20);

  /* Semantic Colors */
  --success:        #16A34A;
  --success-bg:     #DCFCE7;
  --success-text:   #15803D;

  --warning:        #D97706;
  --warning-bg:     #FEF3C7;
  --warning-text:   #92400E;

  --danger:         #DC2626;
  --danger-hover:   #B91C1C;
  --danger-bg:      #FEE2E2;
  --danger-text:    #B91C1C;

  --info:           #2563EB;
  --info-bg:        #DBEAFE;
  --info-text:      #1D4ED8;

  /* Shadows */
  --shadow-sm:  0 1px 3px rgba(0, 0, 0, 0.06);
  --shadow-md:  0 4px 16px rgba(0, 0, 0, 0.10);
  --shadow-lg:  0 20px 60px rgba(0, 0, 0, 0.15);
  --shadow-float: 0 4px 20px rgba(0, 0, 0, 0.12);
}

[data-theme="dark"] {
  /* Backgrounds */
  --bg-base:     #1A1917;
  --bg-surface:  #242320;
  --bg-elevated: #2E2C29;
  --bg-overlay:  rgba(0, 0, 0, 0.55);

  /* Borders */
  --border:        rgba(255, 255, 255, 0.10);
  --border-strong: rgba(255, 255, 255, 0.18);
  --border-focus:  rgba(245, 158, 11, 0.40);

  /* Text */
  --text-primary:   #F0EDE8;
  --text-secondary: #C7C2BB;
  --text-muted:     #8C8880;
  --text-disabled:  #57534E;
  --text-inverse:   #1A1917;

  /* Accent */
  --accent:         #F59E0B;
  --accent-hover:   #D97706;
  --accent-subtle:  rgba(245, 158, 11, 0.10);
  --accent-ring:    rgba(245, 158, 11, 0.25);

  /* Semantic */
  --success:        #22C55E;
  --success-bg:     rgba(34, 197, 94, 0.12);
  --success-text:   #4ADE80;

  --warning:        #F59E0B;
  --warning-bg:     rgba(245, 158, 11, 0.12);
  --warning-text:   #FCD34D;

  --danger:         #EF4444;
  --danger-hover:   #DC2626;
  --danger-bg:      rgba(239, 68, 68, 0.12);
  --danger-text:    #FCA5A5;

  --info:           #3B82F6;
  --info-bg:        rgba(59, 130, 246, 0.12);
  --info-text:      #93C5FD;

  /* Shadows (more pronounced in dark mode) */
  --shadow-sm:  0 1px 3px rgba(0, 0, 0, 0.20);
  --shadow-md:  0 4px 16px rgba(0, 0, 0, 0.30);
  --shadow-lg:  0 20px 60px rgba(0, 0, 0, 0.40);
  --shadow-float: 0 4px 20px rgba(0, 0, 0, 0.35);
}
```

## Spacing Scale

```css
/* Use consistently for padding, margins, gaps */
--space-1:  4px;
--space-2:  8px;
--space-3:  12px;
--space-4:  16px;
--space-5:  20px;
--space-6:  24px;
--space-8:  32px;
--space-10: 40px;
--space-12: 48px;
```

## Border Radius Scale

```css
--radius-sm:  4px;   /* small chips, tight UI */
--radius-md:  6px;   /* buttons, inputs */
--radius-lg:  8px;   /* cards, menus */
--radius-xl:  10px;  /* cards (larger) */
--radius-2xl: 12px;  /* modals, chat input */
--radius-full: 999px; /* pills, toggles, badges */
```

## Tailwind Mapping (if using Tailwind)

| Token | Tailwind class |
|-------|----------------|
| `--accent` | `amber-600` (light) / `amber-500` (dark) |
| `--bg-base` | `stone-50` |
| `--bg-surface` | `white` |
| `--text-primary` | `stone-900` |
| `--text-muted` | `stone-500` |
| `--border` | `border-stone-200` |
| `--danger` | `red-600` |
