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

Checkout is intentionally backend-authoritative: the API recalculates prices, discounts, shipping, tax, and inventory, creates a Razorpay order, and waits for a verified webhook before finalizing the order. The browser must never be allowed to mark an order paid.

## Domain vocabulary

The minimum roles are `CUSTOMER` and `ADMIN`. The order state machine uses exactly `PENDING_PAYMENT`, `PAID`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, and `REFUNDED`. AI access is limited to `searchProducts`, `getProduct`, `compareProducts`, `getProductsByCategory`, and `getProductAvailability`. These names are contracts and should not drift casually.

## Contribution workflow

Start by adding a concrete unchecked item to `todo.md`. Keep a feature in one vertical slice: contract, DTO, service, persistence, UI wiring, and tests. Run type checks and tests before requesting review. If a change affects persistence, write a new Flyway migration rather than editing an applied migration. If a change affects payment or authorization, add a test for the failure path as well as the happy path.

## Production integration checklist

The Java API is configured to accept `LUMA_API_DATABASE_URL`, `LUMA_API_DB_USERNAME`, `LUMA_API_DB_PASSWORD`, and `LUMA_API_JWT_SECRET`. The supplied Neon database values are stored as project secrets; do not place them in source files or commit them. Deploy the API with these same environment variables and Flyway migrations enabled. Confirm that `/actuator/health` and `/api/v1/health` respond successfully before connecting the storefront.

The frontend order client expects `VITE_ORDER_API_BASE_URL` to point to the deployed Java API origin. It forwards the existing authenticated preview session bridge as a bearer token when available and also includes cookies for a same-origin proxy deployment. The Java authentication flow must issue and refresh JWTs containing the `roles` claim with `CUSTOMER` or `ADMIN`, and the frontend session bridge must remove the token on logout or refresh-token revocation.

For payments, enter the Razorpay Test Mode `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, and `RAZORPAY_WEBHOOK_SECRET` through the secure project settings. Configure the Java API webhook endpoint `/api/v1/payments/razorpay/webhook` in the Razorpay Dashboard → Account & Settings → Webhooks. Select the `payment.captured`, `order.paid`, and `payment.authorized` events. Use Razorpay’s documented test payment methods only after the order-creation endpoint and webhook URL are reachable. Live payments require completing Razorpay KYC and replacing test configuration through the secure project settings.

No separate AI provider credential is currently required for the existing controlled assistant because the project has built-in Manus AI integration variables. A production live-catalog assistant still needs the frontend or backend tool adapters connected to the authoritative catalog API.

## Deployment schema repair: checkout currency

If the Java API fails at startup with `checkout_drafts.currency` found as PostgreSQL `bpchar`/`CHAR` but Hibernate expects `VARCHAR`, deploy the version containing `services/api/src/main/resources/db/migration/V6__currency_columns_varchar.sql`. Flyway will apply the forward migration on startup before Hibernate validation. The migration converts `checkout_drafts.currency` and `orders.currency` to `VARCHAR(3)` and trims PostgreSQL fixed-width padding.

For an existing Neon database, the equivalent SQL is:

```sql
ALTER TABLE checkout_drafts ALTER COLUMN currency TYPE VARCHAR(3) USING BTRIM(currency);
ALTER TABLE orders ALTER COLUMN currency TYPE VARCHAR(3) USING BTRIM(currency);
```

Run the following in the Neon SQL Editor only if the deployed artifact does not yet include V6 or V7, then redeploy the Java API:

```sql
ALTER TABLE checkout_drafts ALTER COLUMN currency TYPE VARCHAR(3) USING BTRIM(currency);
ALTER TABLE orders ALTER COLUMN currency TYPE VARCHAR(3) USING BTRIM(currency);
ALTER TABLE checkout_drafts ALTER COLUMN shipping_country TYPE VARCHAR(2) USING BTRIM(shipping_country);
ALTER TABLE orders ALTER COLUMN shipping_country TYPE VARCHAR(2) USING BTRIM(shipping_country);
```

Do not change `spring.jpa.hibernate.ddl-auto` to `update` or `create`; production remains schema-validated and migration-controlled.
