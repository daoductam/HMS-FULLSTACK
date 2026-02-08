**Project Overview**

- **Type:** Create React App (TypeScript) frontend (CRA + react-scripts).
- **Primary UI libs:** Mantine, PrimeReact, Tailwind CSS.
- **High-level structure:** UI components in `src/Components`, page routes in `src/Pages`, Redux slices in `src/Slices`, API wrappers in `src/Service` and `src/Interceptor`.

**How to run (dev/build/test)**

- **Start dev server:** `npm start` (uses `react-scripts start`).
- **Build production bundle:** `npm run build` (output -> `build/`).
- **Run tests:** `npm test` (CRA default runner).

**Architecture & conventions (important for changes)**

- API calls are centralized through `src/Interceptor/AxiosInterceptor.tsx` — import the default `axiosInstance` from there in any service: `import axiosInstance from '../Interceptor/AxiosInterceptor'`.
- Backend base URL comes from env vars: `REACT_APP_API_URL` or `REACT_APP_LOCAL_BACKEND_URL`. When running locally, ensure `REACT_APP_LOCAL_BACKEND_URL` is set.
- Auth token storage: JWT is stored under localStorage key `token`. The axios interceptor reads that token and injects `Authorization: Bearer <token>` on requests. See [src/Interceptor/AxiosInterceptor.tsx](src/Interceptor/AxiosInterceptor.tsx#L1).
- Routing uses a role-based layout split: Admin / Patient / Doctor. Routes live in [src/Routes/AppRoutes.tsx](src/Routes/AppRoutes.tsx#L1). Note: nested routes sometimes use absolute paths inside children (e.g., `/admin/patient/edit/:id`) — be careful when refactoring route paths.
- State management: Redux Toolkit slices live in `src/Slices`. `src/Store.tsx` wires slices into the store and exports `RootState`/`AppDispatch` types for typed hooks. See [src/Store.tsx](src/Store.tsx#L1).

**Service & state patterns**

- Create thin service modules under `src/Service` for each domain (e.g., `PatientProfileService.tsx`) that call `axiosInstance`. Keep response parsing in services, not in components.
- For persistent auth state, the `JwtSlice` reads/writes `localStorage` directly and exposes `setJwt`/`removeJwt`. See [src/Slices/JwtSlice.tsx](src/Slices/JwtSlice.tsx#L1).

**Styling & assets**

- Tailwind is configured (see `tailwind.config.js`) and used alongside component libraries; be conservative when changing global CSS since both library and Tailwind classes are in use.
- Production assets are output to `build/static/*` (already present in repo).

**Common pitfalls & implementation notes for AI edits**

- When adding API endpoints, always import `axiosInstance` (do not create new axios instances unless intentional) so interceptors and base URL are reused.
- Watch for mixed route declarations (absolute vs nested) in `AppRoutes.tsx` — altering path strings can break nested route matching.
- The project relies on `localStorage` token; removing the token requires updating both the `JwtSlice` and any UI elements that depend on it (e.g., `ProtectedRoutes`).
- Use the exported `RootState`/`AppDispatch` types from `src/Store.tsx` when adding typed selectors or thunks.

**Key files (quick links)**

- Routing: [src/Routes/AppRoutes.tsx](src/Routes/AppRoutes.tsx#L1)
- Axios / API: [src/Interceptor/AxiosInterceptor.tsx](src/Interceptor/AxiosInterceptor.tsx#L1)
- Redux store: [src/Store.tsx](src/Store.tsx#L1)
- Auth slice: [src/Slices/JwtSlice.tsx](src/Slices/JwtSlice.tsx#L1)
- Services folder: `src/Service/` (per-domain service modules)

**If you need to make larger changes**

- Prefer small, incremental commits that add/modify a single route, slice, or service.
- Run `npm start` locally and confirm the app boots and the console shows the configured `BASE_URL` (the interceptor logs it). If the app cannot reach the backend, confirm the `REACT_APP_LOCAL_BACKEND_URL` env var.

Feedback: I drafted this file based on discoverable code patterns. Tell me which areas are unclear or missing (tests, CI, deployment) and I will iterate.
