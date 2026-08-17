# Code explanation and production handoff

## Frontend shell

`client/src/App.tsx` owns route registration. `client/src/pages/Home.tsx` currently contains the working storefront slice so the project is easy to preview, but the long-term extraction path is feature-oriented modules under `client/src/features`. The shared header, footer, cart sheet, product card, and page-level views establish the visual language before backend wiring.

The local `products` array is deliberately a demo adapter. It models the fields the production catalog API must return: `id`, `slug`, `name`, `brand`, `category`, `price`, `compareAt`, `image`, `tone`, `tag`, and `detail`. The browser may display these values, but it must not be trusted for checkout totals or inventory.

## Cart state

The cart is client-visible state persisted under `luma-cart` for continuity during the demo. `addToCart`, `updateCart`, and `removeCart` are the places where a production adapter should call the server. The authoritative API should return the recalculated line price, stock validation result, subtotal, shipping, tax, discount, and grand total. Client state should be treated as an optimistic projection, never as the source of truth.

## AI assistant

The controlled catalog tools are grouped in the `catalogTools` object. They are intentionally narrow functions over catalog records, not arbitrary database access. A production implementation should move these functions to the API, validate every parameter, rate-limit the endpoint, constrain the context sent to the model, and return only allowed catalog data. The UI should continue to display the exact tool names so the contract remains legible to reviewers.

## Backend foundation

`services/api/src/main/java/com/luma/commerce` is a Spring Boot 3 modular-monolith starting point. The `api` package contains transport concerns: health responses and the consistent error shape. Future packages map to business capabilities: `auth`, `catalog`, `cart`, `order`, `payment`, `review`, `admin`, and `ai`. Each module should follow controller → service → repository → domain/entity, with DTOs at the API boundary.

The API uses `ddl-auto: validate`; schema changes belong in versioned Flyway migrations. PostgreSQL runs locally through Docker Compose and should remain close to the intended Neon production configuration. Secrets are environment-only. The current health endpoint is a foundation check, not evidence that the business modules are complete.

## Safe implementation order

Implement catalog read APIs before cart mutations. Implement authentication and ownership checks before order history. Implement inventory reservation before checkout. Implement Stripe session creation before webhook finalization. Implement review creation only after the API can prove a customer purchased the product. Implement AI tools after catalog retrieval is authoritative. This sequence keeps the storefront useful while preventing security-sensitive shortcuts.
