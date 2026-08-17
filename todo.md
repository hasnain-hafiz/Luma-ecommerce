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
