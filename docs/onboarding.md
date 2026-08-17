# Luma Commerce onboarding guide

## Purpose

Luma Commerce is organized as a storefront application plus a modular-monolith API foundation. The frontend is the current working experience. The backend under `services/api` is the deliberate integration seam for catalog, authentication, cart, orders, payments, reviews, administration, and AI tools.

## Repository map

| Path | Responsibility |
|---|---|
| `client/src/pages/Home.tsx` | Route-aware storefront experience and local demo data boundary |
| `client/src/index.css` | Luma visual tokens, responsive layout, and component styling |
| `client/src/App.tsx` | Wouter route registration and application shell entry |
| `server/` | Existing managed template server and tRPC infrastructure |
| `services/api` | Java 21 Spring Boot modular-monolith foundation |
| `drizzle` | Existing managed template database layer; do not confuse with the future PostgreSQL API schema |
| `docs` | Architecture, security, AI, and onboarding decisions |
| `docker-compose.yml` | Local PostgreSQL service for the future API |
| `todo.md` | Implementation history and next work items |

## Local setup

Run the frontend with `pnpm install && pnpm dev`. Validate the existing project with `pnpm check && pnpm test`. To start the local PostgreSQL service, copy the root environment template when available and run `docker compose up -d postgres`. The API can then be started from `services/api` with `./mvnw spring-boot:run` once Maven Wrapper is added or with `mvn spring-boot:run` when Maven is installed.

The API defaults to `jdbc:postgresql://localhost:5432/luma`, user `luma`, and a placeholder password. Replace these values through environment variables. Never commit real database credentials, JWT secrets, Stripe secrets, or AI provider keys.

## Request flow

A browser interaction starts in a route component. Catalog and cart actions currently use typed local data so the storefront remains usable without external credentials. The production path should replace those local functions with calls to `/api/v1` procedures. The API controller should validate the request, pass a DTO to a domain service, use a repository for persistence, and return a response DTO. Controllers must not expose JPA entities directly.

Checkout is intentionally backend-authoritative: the API recalculates prices, discounts, shipping, tax, and inventory, creates a Stripe session, and waits for a verified webhook before finalizing the order. The browser must never be allowed to mark an order paid.

## Domain vocabulary

The minimum roles are `CUSTOMER` and `ADMIN`. The order state machine uses exactly `PENDING_PAYMENT`, `PAID`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, and `REFUNDED`. AI access is limited to `searchProducts`, `getProduct`, `compareProducts`, `getProductsByCategory`, and `getProductAvailability`. These names are contracts and should not drift casually.

## Contribution workflow

Start by adding a concrete unchecked item to `todo.md`. Keep a feature in one vertical slice: contract, DTO, service, persistence, UI wiring, and tests. Run type checks and tests before requesting review. If a change affects persistence, write a new Flyway migration rather than editing an applied migration. If a change affects payment or authorization, add a test for the failure path as well as the happy path.
