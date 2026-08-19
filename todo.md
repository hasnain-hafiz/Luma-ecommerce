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
