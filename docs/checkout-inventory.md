# Checkout and inventory backend

Checkout is intentionally server-authoritative. The customer submits a cart identifier and shipping address; the backend resolves the owned cart, locks active products, validates current inventory, calculates subtotal in integer cents from persisted prices, reserves stock, creates a `PENDING_PAYMENT` order, writes immutable order-item snapshots, and creates a payment-session handoff.

> The browser may display totals, but it never establishes the amount charged or the quantity reserved.

The current slice persists a dedicated `checkout_drafts` record before payment so reservations and provider metadata have a durable identifier. A verified webhook converts the open draft into the durable order, copies immutable item snapshots, transfers reservations to the order, commits them, and marks the order `PAID`.

| Sequence | Backend responsibility | Durable result |
|---|---|---|
| Cart review | Resolve the cart by authenticated user | Owned cart only |
| Inventory validation | Pessimistically lock active products and compare quantity | Reservation or transaction failure |
| Authoritative total | Calculate subtotal/shipping/tax in cents | Persisted order total |
| Payment session | Call the provider adapter with the persisted order total | `PENDING_PAYMENT` order |
| Webhook | Verify raw payload signature and deduplicate provider event ID | `PAID` order |
| Fulfillment | Transition only through the exact status vocabulary | Auditable order history |

The required order states are `PENDING_PAYMENT`, `PAID`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, and `REFUNDED`. Order items copy product name, SKU, unit price, quantity, line total, and image URL at purchase time, so later catalog edits cannot rewrite customer history.

## Stripe boundary

`PaymentGateway` is the provider-neutral seam. The current `NoopPaymentGateway` returns a clearly non-production placeholder session and rejects webhook verification. A production adapter must use Stripe Checkout Sessions, verify `Stripe-Signature` against the raw request body, store the provider event ID uniquely, and treat duplicate delivery as a no-op. The webhook—not a browser callback—creates the paid state.

## Remaining operational work

The remaining operational work is to clear cart items after successful payment, implement shipping/tax calculation, add order-history read APIs, and run the Java/PostgreSQL Testcontainers suite in CI. Expired or cancelled draft reservations now restore product inventory, mark reservations `RELEASED`, and transition the draft to `EXPIRED`; successful verified payment transfers reservations to the durable order and marks them `COMMITTED`. Never seed fake reviews or customer-generated content as order or catalog data.

## Draft cancellation API

Authenticated customers can cancel an open draft with `POST /api/v1/checkout/drafts/{draftId}/cancel`. The service verifies that the JWT subject owns the draft, restores reserved inventory, marks reservations `RELEASED`, and transitions the draft to `CANCELLED`. Expiry handling uses the same release behavior but transitions the draft to `EXPIRED`.

## Java validation

The sandbox does not include Maven/JDK execution. CI should run `cd services/api && ./mvnw -B test` (or `mvn -B test` when Maven is installed) and should provision PostgreSQL for integration tests. The repository's frontend checks remain `pnpm check && pnpm test`; source-contract checks complement, but do not replace, the Java test run.
