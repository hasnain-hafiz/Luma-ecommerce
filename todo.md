# Project TODO

- [x] Establish Luma Commerce visual system: warm ivory canvas, charcoal typography, cobalt accent, editorial product photography, restrained motion, and responsive layouts.
- [x] Build global storefront shell with announcement bar, responsive navigation, search entry, account/wishlist/cart affordances, and footer.
- [x] Build home page with hero, featured categories, curated product sections, editorial story, and grounded AI assistant CTA.
- [x] Build catalog browsing with category/brand filters, price/rating/availability controls, sorting, pagination affordance, loading state, empty state, and error state.
- [x] Build product detail page with gallery, pricing/discount, rating, stock state, quantity controls, add-to-cart, wishlist, specs, reviews summary, and related products.
- [x] Build persistent client cart experience with quantity updates, remove/clear actions, calculated totals, and authoritative-validation messaging.
- [x] Build wishlist experience and authenticated customer profile, address, and navigation states.
- [x] Build auth-facing login/register/reset-password screens and role labels CUSTOMER / ADMIN without exposing credentials.
- [x] Build checkout sequence UI: cart review, validation, payment session handoff, webhook-confirmed order messaging, and confirmation page.
- [x] Build customer orders list/detail with immutable item snapshots and exact status labels: PENDING_PAYMENT, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED.
- [x] Build product review submission and moderation states without fabricating customer reviews or testimonials.
- [x] Build admin dashboard with analytics cards, recent orders, low-stock view, product/category/inventory/order/review/customer management surfaces.
- [x] Build AI shopping assistant using the exact controlled tool names: searchProducts, getProduct, compareProducts, getProductsByCategory, getProductAvailability; responses must be grounded in real catalog data.
- [x] Add typed data models and local demo catalog data for the storefront UI, with clear integration boundaries for the existing backend.
- [x] Add/update Vitest coverage for core UI data helpers and important interaction logic.
- [x] Run type check, tests, dev-server verification, responsive screenshots, and polish fixes.
- [x] Update README/docs with architecture, integration boundaries, security notes, and local development guidance.

## Follow-up validation gaps

- [x] Add a dedicated global wishlist affordance and a functional wishlist view.
- [x] Make catalog filtering, sorting, pagination, loading, and error states interactive.
- [x] Add cart persistence, clear-cart action, and explicit validation states.
- [x] Add dedicated login, register, reset-password, checkout, confirmation, order-detail, and review flows.
- [x] Add customer address management and explicit CUSTOMER / ADMIN role presentation.
- [x] Add admin management navigation for products, categories, inventory, orders, reviews, and customers.
- [x] Implement local controlled AI catalog tools and use them to ground assistant responses.
- [x] Add focused Vitest coverage for catalog tools and cart calculations.
- [x] Document the demo UI boundary and production integration plan in README/docs.

## Backend foundation and onboarding

- [x] Add a modular backend foundation under services/api with Java 21 and Spring Boot conventions documented for future implementation.
- [x] Define versioned API/domain contracts for auth, catalog, cart, orders, payments, reviews, admin, and AI tools.
- [x] Add PostgreSQL/Flyway/Docker environment templates without committing secrets or pretending live integrations are configured.
- [x] Add backend health/readiness and error-response contract documentation.
- [x] Add onboarding guide explaining repository structure, local setup, code flow, domain vocabulary, and contribution workflow.
- [x] Add code-explanation documentation for the frontend shell, catalog tools, cart state, route handling, and backend integration boundary.
- [x] Add validation checks for the new contracts and documentation references.

## Catalog backend slice

- [x] Add PostgreSQL catalog tables for categories, brands, products, and product images with indexes and constraints.
- [x] Add Flyway migration and seed-free schema documentation for the catalog tables.
- [x] Add Java catalog entities, DTOs, repository interfaces, and service contracts.
- [x] Implement public catalog search, category/brand/price/rating/availability filtering, sorting, pagination, and product detail contracts.
- [x] Implement ADMIN-only product create/update/archive contracts without seeding fake customer content.
- [x] Document the catalog request flow and frontend integration boundary.
- [x] Add catalog-specific automated tests and run available source/repository validation; Java execution remains configured for CI because Maven/JDK are unavailable locally.

## Catalog authorization hardening

- [x] Add Spring Security role enforcement for `/api/v1/admin/products` using the exact ADMIN role.
- [x] Add forbidden-access coverage for non-admin product writes and document the authentication integration seam.

## Authentication and cart backend slice

- [x] Add PostgreSQL users, refresh_tokens, carts, and cart_items schema with ownership and uniqueness constraints.
- [x] Add authentication and cart DTO/domain contracts with exact CUSTOMER and ADMIN roles.
- [x] Add password-hashing and JWT/refresh-token service boundaries without committing secrets.
- [x] Add customer-owned cart read/add/update/remove contracts with server-side product lookup boundaries.
- [x] Enforce authenticated customer access on cart routes and document ownership rules.
- [x] Add auth/cart source-contract tests and validation checks.
- [x] Document the authentication and cart onboarding flow.

## Authentication and cart hardening

- [x] Split Java public entities and repositories into filename-matching classes.
- [x] Replace refresh-token hashCode storage with cryptographic hashing and implement revocation-aware token rotation.
- [x] Enforce exact CUSTOMER role access on `/api/v1/cart/**` and retain ADMIN protection.
- [x] Add backend security tests for unauthenticated, CUSTOMER, and ADMIN route access.
- [x] Update auth/cart documentation with the hardened token and role flow.

## Authentication enhancements

- [x] Add password-reset and email-verification token tables with hashed tokens, expiry, consumption, and user ownership constraints.
- [x] Add password-reset and email-verification request/response contracts with privacy-safe responses.
- [x] Implement provider-neutral token generation, hashing, expiry, and single-use consumption services.
- [x] Implement password-reset request/confirm and email-verification request/confirm endpoints.
- [x] Add server-side rate limiting for registration, login, refresh, password reset, and verification requests.
- [x] Document email delivery integration, token lifecycle, rate-limit policy, and operational configuration.
- [x] Add authentication-enhancement source-contract tests and validation checks.

## Authentication enhancement hardening

- [x] Revoke all active refresh tokens when a password reset is confirmed.
- [x] Add backend tests for password-reset and email-verification single-use, expiry, privacy, and confirmation behavior.
- [x] Add backend tests for registration, login, refresh, recovery, verification, and rate-limit rejection behavior.

## Authentication integration-test coverage

- [x] Add Spring service/controller tests for reset and verification success, expiry rejection, consumed-token rejection, and privacy-safe request behavior.
- [x] Add Spring tests for registration, login, unverified-user rejection, refresh rotation/revocation, and rate-limit 429 behavior.

## Authentication endpoint test completion

- [x] Add reset-confirm success, expiry rejection, and consumed-token rejection tests.
- [x] Add email-verification request/confirm success, expiry rejection, and consumed-token rejection tests.
- [x] Add registration success/duplicate-email, login success/unverified rejection, and refresh rotation/revocation tests.
- [x] Add 429 tests for registration, refresh, password-reset, and email-verification request endpoints.

## Final authentication test gaps

- [x] Add password-reset success and consumed-token rejection tests.
- [x] Add email-verification request success and expired-token rejection tests.
- [x] Add registration success and successful-login tests.

## Final verification request test

- [x] Add a test proving a normal email-verification request returns the privacy-safe accepted response and invokes the delivery flow.

## Verification request assertion hardening

- [x] Assert the privacy-safe accepted response body and verify the email-verification service handoff in the MVC test.

## Checkout and inventory backend slice

- [x] Add orders, order_items, inventory_reservations, and payment_events schema with exact order statuses and immutable item snapshot columns.
- [x] Add checkout and payment DTO/domain contracts with authoritative cents totals and webhook event idempotency.
- [x] Implement transactional cart validation and inventory reservation with server-side product prices and stock checks.
- [x] Complete webhook-confirmed order creation from a dedicated checkout draft; current implementation persists a dedicated draft and creates the durable order only after webhook confirmation.
- [x] Add Stripe Checkout Session and signed webhook provider boundaries without committing secrets.
- [x] Document checkout sequencing, status transitions, reservation release, and payment integration.
- [x] Add checkout/inventory source-contract tests and validation checks.

## Checkout lifecycle hardening

- [x] Separate pre-payment checkout drafts from webhook-confirmed orders or make webhook confirmation create the durable order record.
- [x] Implement reservation commit on successful payment and release on expiry/cancellation.
- [x] Add lifecycle tests for duplicate events, reservation commit, reservation release, inventory restoration, and cancellation transitions.

## Checkout lifecycle completion gaps

- [x] Restore product inventory when releasing expired or cancelled reservations.
- [x] Wire cancellation/expiry paths to release reservations and update order state.
- [x] Add tests for reservation COMMITTED/RELEASED state and restored inventory.
- [x] Decide and implement dedicated checkout drafts or webhook-created durable orders.

## Final checkout lifecycle tests

- [x] Transition expired pending orders to CANCELLED when releasing expired reservations.
- [x] Add service tests for RELEASED reservation state, COMMITTED webhook state, and releaseExpired/releaseForOrder behavior.

## Final webhook reservation test

- [x] Add a webhook test asserting a successful payment transitions reservations to COMMITTED.

## Checkout draft lifecycle completion

- [x] Route expired draft reservations through draft expiry state instead of order lookup.
- [x] Add explicit draft cancellation/release service flow.
- [x] Add draft lifecycle tests for expiry, cancellation, release, and conversion ownership transfer.

## Final draft cancellation and conversion coverage

- [x] Add explicit CANCELLED draft state and a cancellation/release service path.
- [x] Test draft cancellation releases reservations and marks the draft CANCELLED.
- [x] Test webhook conversion transfers reservation ownership from draftId to orderId.

## Final checkout delivery validation

- [x] Expose draft cancellation through a reachable authenticated checkout API flow and document the endpoint.
- [x] Add CI documentation/command coverage for executing Maven/Spring checkout tests when Java tooling is available.

## Order history and confirmation flow

- [x] Add ownership-aware order-history and order-detail read contracts with immutable item snapshots and exact status values.
- [x] Add customer order-history and order-detail controller endpoints with not-found/ownership protection.
- [x] Clear the paid customer's cart only after a verified, idempotently processed payment webhook.
- [x] Add backend tests for order ownership, detail snapshots, cart clearing, and duplicate webhook behavior.
- [x] Build a user-friendly order confirmation page with order number, amount, item details, shipping summary, and current status.
- [x] Add order-history UI navigation and confirmation-page route handling.
- [x] Document the payment-to-confirmation flow and validate frontend/backend contracts.

## Confirmation UI verification fixes

- [x] Register the confirmation route with an optional order identifier so confirmation links do not fall through to 404.
- [x] Fix order-timeline status spacing and numbering so each exact status is legible on desktop and mobile.

## Final order-history hardening

- [x] Protect `/api/v1/orders` as authenticated CUSTOMER-only routes and map missing/foreign orders to explicit not-found responses.
- [x] Add backend source-contract coverage for owned-vs-foreign order access and not-found detail behavior.
- [x] Replace hardcoded confirmation/order-history demo data with an API-backed integration seam and safe no-id confirmation behavior.
- [x] Capture mobile screenshots for order timeline and confirmation layouts after the responsive fixes.

## Final order-history contract corrections

- [x] Add explicit backend contract assertions for foreign-order rejection and missing-order detail responses.
- [x] Replace order-history demo rows with API-backed loading, empty, and error states.
- [x] Fix confirmation route parsing for `/checkout/confirmation` without an id and align frontend mapping to the structured order-detail contract.

## Final confirmation contract polish

- [x] Add Spring MVC/controller contract coverage for owned, foreign, and missing order detail responses.
- [x] Render an explicit no-id or unavailable confirmation state instead of indefinite loading.
- [x] Map confirmation item image and presentation from immutable order snapshot data without hardcoded item copy.

## Final verification corrections

- [x] Add MVC-level order controller response coverage for owned, foreign, and missing order detail requests.
- [x] Render an explicit unavailable confirmation state when the order-detail request fails or returns 404.

## Final MVC case distinction

- [x] Split foreign-order and nonexistent-order MVC assertions into separate 404 tests.

## Order API reliability improvements

- [x] Add authenticated order API request handling with session credentials and safe 401/403 messaging.
- [x] Add retry controls for order history and confirmation fetch failures, including retry-in-progress feedback.
- [x] Validate authenticated and retryable order flows with type checks, tests, and responsive screenshots.

## Auth transport and retry verification

- [x] Wire order requests to a JWT-compatible authenticated transport or documented backend proxy rather than relying only on cookies.
- [x] Add focused frontend tests for 401/403 handling and retry success/failure transitions.
- [x] Capture screenshots of authenticated and retry-error states after the transport is wired.

## End-to-end reliability verification

- [x] Connect the order client to an actually issued and refreshed JWT session token or add a documented authenticated proxy bridge.
- [x] Add and execute frontend tests covering order-history and confirmation auth-required states plus retry transitions.
- [x] Add deterministic failure-mode controls for preview verification and capture the resulting 401/403 and retry-error screenshots.

## Production integration completion

- [x] Audit live frontend-to-Java API integration boundaries for auth, catalog, cart, checkout, Stripe, admin, and AI services.
- [x] Implement remaining integration code that can be completed without user-provided secrets.
- [x] Add or document mandatory credential configuration and safe setup instructions for user-owned services.
- [x] Run available frontend and source validation, then document the remaining user actions and external-service verification steps.

## Remaining live integrations

- [ ] Wire storefront catalog and cart flows to live Java `/api/v1` endpoints instead of local/demo data.
- [x] Replace the Java `NoopPaymentGateway` boundary with a real Razorpay adapter and signed webhook verification.
- [ ] Connect admin surfaces to authoritative backend APIs for products, inventory, orders, customers, categories, and reviews.
- [ ] Connect the AI assistant to authoritative backend catalog tools while preserving the exact tool names.

## Razorpay payment migration

- [x] Replace Stripe-specific payment configuration, naming, and documentation with Razorpay equivalents.
- [x] Implement Razorpay order creation and signature/webhook verification while preserving idempotent payment events and server-authoritative totals.
- [x] Update checkout frontend and backend tests for Razorpay payment handoff and webhook confirmation.
- [x] Document required Razorpay credentials, dashboard webhook setup, test flow, and remaining user actions.

## CI failure fixes

- [x] Remove the duplicate pnpm version declaration between GitHub Actions and package metadata.
- [x] Fix the AuthApplicationService JWT header construction so Maven Java 21 compilation succeeds.
- [x] Run frontend checks and the Java Maven verification path, then review remaining CI failures.

## CI configuration test fix

- [x] Make `server/api-config.test.ts` pass when GitHub Actions does not inject deployment secrets, while still validating configured values when present without exposing them.
- [x] Rerun `pnpm test` and review the CI workflow after the configuration-test fix.

## Java API container deployment

- [x] Add a production multi-stage Dockerfile for the Java 21 Spring Boot API with non-root runtime and dynamic port support.
- [x] Validate the Dockerfile build/run contract and document Railway/Render deployment usage.

## Deployment schema mismatch fix

- [x] Align `checkout_drafts.currency` PostgreSQL type with the Java entity and add a forward-safe migration for existing Neon databases.
- [x] Run Java verification and provide the exact Neon migration/deployment retry steps.

## Render schema mismatch follow-up

- [x] Convert all checkout draft and order snapshot fixed-width CHAR columns that map to Java String fields, including `shipping_country`.
- [x] Run Java tests and provide Render redeploy guidance after the migration update.
