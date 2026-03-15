# React Architecture Reference

## Standard Folder Structure

```
src/
├── features/                        # Feature-based modules (primary org unit)
│   └── {feature}/
│       ├── components/              # UI components for this feature
│       │   ├── {Feature}List.jsx
│       │   ├── {Feature}Card.jsx
│       │   └── {Feature}Form.jsx
│       ├── hooks/                   # Custom hooks for this feature
│       │   ├── use{Feature}List.js
│       │   └── use{Feature}Form.js
│       ├── services/                # API calls for this feature
│       │   └── {feature}Service.js
│       ├── store/                   # Feature-level state (if needed)
│       │   └── {feature}Store.js
│       └── index.js                 # Public exports only
│
├── components/                      # Globally shared atomic components
│   ├── ui/
│   │   ├── Button/
│   │   │   ├── Button.jsx
│   │   │   └── index.js
│   │   ├── Input/
│   │   └── Modal/
│   └── layout/
│       ├── Navbar.jsx
│       └── PageWrapper.jsx
│
├── hooks/                           # Global shared hooks
│   ├── useDebounce.js
│   └── useLocalStorage.js
│
├── services/                        # Global API config
│   └── apiClient.js                 # Axios instance with interceptors
│
├── store/                           # Global state (Zustand / Context)
│   ├── authStore.js
│   └── uiStore.js
│
├── utils/                           # Pure utility functions
│   ├── formatters.js
│   └── validators.js
│
├── constants/
│   └── appConstants.js
│
└── types/                           # PropTypes or TS interfaces
    └── {feature}.types.js
```

---

## Component Checklist Per Feature

Every new feature MUST include:

- [ ] At least one container-style component that uses the custom hook
- [ ] Presentational sub-components that accept props only (no hooks other than `useState` for pure UI)
- [ ] A custom hook (`use{Feature}`) that owns all data-fetching and state
- [ ] A service file (`{feature}Service.js`) with all API calls
- [ ] A barrel `index.js` that controls what is publicly exported from the feature

---

## Component Rules

### Container Component (smart)
```jsx
// ✅ CORRECT — no fetch() here, delegates to hook
import { useProductList } from '../hooks/useProductList';
import { ProductCard } from './ProductCard';
import { Spinner } from '@/components/ui/Spinner';

export function ProductList() {
  const { products, isLoading, error } = useProductList();

  if (isLoading) return <Spinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="product-grid">
      {products.map(p => <ProductCard key={p.id} product={p} />)}
    </div>
  );
}
```

### Presentational Component (dumb)
```jsx
// ✅ CORRECT — pure props, no side effects
export function ProductCard({ product, onSelect }) {
  return (
    <div className="card" onClick={() => onSelect(product.id)}>
      <h3>{product.name}</h3>
      <p>{formatCurrency(product.price)}</p>
    </div>
  );
}

ProductCard.defaultProps = {
  onSelect: () => {},
};
```

**Rules:**
- Presentational components: NO data fetching, NO global state access
- Container components: delegate all data concerns to a hook
- Max JSX depth: 4 levels — extract sub-components if deeper
- No inline functions in JSX if they contain more than a single expression
- No component longer than 100 lines — split it

---

## Custom Hook Rules

```js
// hooks/useProductList.js
import { useState, useEffect } from 'react';
import { productService } from '../services/productService';

export function useProductList(filters = {}) {
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;

    async function fetchProducts() {
      try {
        setIsLoading(true);
        setError(null);
        const data = await productService.getAll(filters);
        if (!cancelled) setProducts(data);
      } catch (err) {
        if (!cancelled) setError(err.message);
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    }

    fetchProducts();
    return () => { cancelled = true; };
  }, [JSON.stringify(filters)]);

  return { products, isLoading, error };
}
```

**Rules:**
- Always return `{ data, isLoading, error }` shape for consistency
- Always handle cleanup (cancelled flag or AbortController) in useEffect
- Never put fetch logic directly in a component — ALWAYS extract to a hook
- One hook per data domain (`useProductList`, not `useEverything`)
- Hooks that mutate data should return `{ mutate, isLoading, error }` pattern

---

## Service Layer Rules

```js
// services/productService.js
import { apiClient } from '@/services/apiClient';

export const productService = {
  getAll: (params) => apiClient.get('/products', { params }).then(r => r.data),
  getById: (id) => apiClient.get(`/products/${id}`).then(r => r.data),
  create: (payload) => apiClient.post('/products', payload).then(r => r.data),
  update: (id, payload) => apiClient.put(`/products/${id}`, payload).then(r => r.data),
  delete: (id) => apiClient.delete(`/products/${id}`).then(r => r.data),
};
```

**Rules:**
- All API calls live in service files — never in hooks, never in components
- Service functions return promises of unwrapped data (`.then(r => r.data)`)
- Services are plain objects — no class required
- Never import service directly in a component — import through a hook

---

## API Client Setup

```js
// services/apiClient.js
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor — attach auth token
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Response interceptor — normalize errors
apiClient.interceptors.response.use(
  res => res,
  err => {
    const message = err.response?.data?.message || 'An error occurred';
    return Promise.reject(new Error(message));
  }
);
```

**Rules:**
- One `apiClient.js` — never create multiple Axios instances
- Auth token attachment lives in the interceptor, not in individual service calls
- Error normalization lives in the interceptor — services get clean error messages

---

## State Management Rules

| State Type | Solution |
|---|---|
| Local UI state (open/closed, tab) | `useState` in component |
| Server/async state | Custom hook with `useEffect` (or React Query) |
| Feature-level shared state | Zustand store in `features/{feature}/store/` |
| Global app state (auth, theme) | Zustand store in `store/` |
| Form state | `react-hook-form` or controlled inputs in a hook |

**Rules:**
- Do NOT use `useContext` + `useState` for server state — that's what hooks are for
- Global Zustand stores must be small and focused — one concern per store
- Context is reserved for: theme, locale, auth session — not data fetching

---

## Form Rules

```jsx
// hooks/useProductForm.js
import { useState } from 'react';
import { productService } from '../services/productService';

export function useProductForm(onSuccess) {
  const [values, setValues] = useState({ name: '', price: '' });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validate(vals) {
    const errs = {};
    if (!vals.name.trim()) errs.name = 'Name is required';
    if (!vals.price || vals.price <= 0) errs.price = 'Price must be positive';
    return errs;
  }

  async function handleSubmit() {
    const errs = validate(values);
    if (Object.keys(errs).length) { setErrors(errs); return; }
    
    setIsSubmitting(true);
    try {
      await productService.create(values);
      onSuccess?.();
    } catch (err) {
      setErrors({ form: err.message });
    } finally {
      setIsSubmitting(false);
    }
  }

  return { values, setValues, errors, isSubmitting, handleSubmit };
}
```

**Rules:**
- Form state + validation + submission logic lives in a custom hook
- Component only renders fields and calls `handleSubmit`
- Validation runs client-side before API call
- Always disable submit button when `isSubmitting` is true

---

## Barrel Export Pattern

```js
// features/product/index.js — ONLY export what other features need
export { ProductList } from './components/ProductList';
export { ProductCard } from './components/ProductCard';
// Do NOT export hooks, services, or internals — those stay private to the feature
```

**Rules:**
- Other features import from `@/features/product`, not from deep paths
- Internal implementation details (hooks, services) are NOT exported from barrel
- This enforces feature encapsulation and makes refactoring safe
