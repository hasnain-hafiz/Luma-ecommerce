# Luma Commerce

Luma Commerce is a portfolio-quality ecommerce storefront experience built as a new application rather than an adaptation of the previously selected repositories. The current project focuses on a carefully designed customer-facing experience with a local catalog boundary that can be replaced by the existing server procedures and payment integrations.

## What is implemented

The storefront includes a home page, catalog browsing, search entry, product detail pages, related products, wishlist affordances, a persistent local bag, quantity controls, calculated subtotals, account/order surfaces, an operations dashboard, and a catalog-aware AI concierge. The UI uses the exact product-domain vocabulary requested: roles **CUSTOMER** and **ADMIN**, order states **PENDING_PAYMENT**, **PAID**, **PROCESSING**, **SHIPPED**, **DELIVERED**, **CANCELLED**, and **REFUNDED**, and the controlled AI tools `searchProducts`, `getProduct`, `compareProducts`, `getProductsByCategory`, and `getProductAvailability`.

The concierge currently runs through a controlled local catalog layer. It never accepts arbitrary SQL or invents catalog records; each recommendation is generated from product records returned by the tool functions. In production, these functions should become server-side procedures backed by the database and LLM provider, with Stripe webhook confirmation remaining the only authoritative payment-to-order transition.

## Visual direction

Luma uses a warm ivory canvas, charcoal typography, cobalt blue as the primary action color, and a small yellow signal color. The combination of DM Sans, DM Mono, and Playfair Display creates a restrained editorial identity. Product imagery uses hosted storage paths for the selected reference assets and remote licensed/placeholder image URLs for the demo catalog. Motion is subtle and respects reduced-motion preferences.

## Local development

```bash
pnpm install
pnpm dev
pnpm check
pnpm test
```

The managed project template supplies the existing authentication, database, storage, and tRPC foundations. The UI is intentionally organized around a public storefront shell plus contextual `/shop`, `/product/:slug`, `/assistant`, `/account/:section`, and `/admin/:section` routes. The current local catalog is a presentational/demo boundary; production data should be read through typed server procedures and never trusted from the browser for price, inventory, discount, tax, shipping, or final order totals.

## Security and payments boundary

Authentication and authorization must remain backend-enforced. Passwords should be hashed using the backend's strong password hashing configuration, sessions should use secure HttpOnly cookies or an equivalent revocable mechanism, and customer order access must perform ownership checks. Stripe test-mode checkout should create a server-side payment session, verify webhook signatures, use idempotency keys, and create/finalize orders only after a valid webhook. Order items must snapshot product name, SKU, price, quantity, and applicable discount at purchase time.

## Testing

The repository includes the starter authentication logout test plus focused controlled-catalog contract tests. The next production step is to add server integration coverage around inventory locking, checkout, Stripe webhook idempotency, role enforcement, and review ownership, plus Playwright flows for browse, search, cart, checkout, order history, and admin management.

## Honest limitations

This checkpoint is a polished working frontend and interaction prototype inside the managed full-stack template. Dedicated authentication forms, database-backed catalog CRUD, Stripe credentials, live LLM provider calls, and the complete Spring Boot/PostgreSQL infrastructure from the original brief remain integration work rather than being fabricated as if complete. The code and visual surfaces make those boundaries explicit so they can be implemented without changing the storefront design system.
