# Authentication and cart onboarding

The authentication and cart slice adds PostgreSQL ownership primitives for users, refresh tokens, carts, and cart items. User roles are the exact values `CUSTOMER` and `ADMIN`. A cart belongs to exactly one user through `carts.user_id`, and a cart may contain a product only once through the `(cart_id, product_id)` uniqueness constraint.

## Authentication flow

Registration accepts an email and a password of at least twelve characters. Passwords are passed through Spring Security's Argon2 encoder before persistence. Login verifies the password, creates a short-lived signed access token, and creates a long-lived opaque refresh token whose hash is stored rather than the raw token. The access token carries the user subject, email, and role authority claim.

The refresh endpoint atomically checks the hashed token, rejects expired or revoked records, revokes the presented token, and issues a replacement pair. The database transaction boundary should be tightened further with an explicit service-level transaction and reuse-detection audit event before production launch.

## Cart flow

Authenticated requests to `/api/v1/cart` derive the user ID from the verified JWT subject. Spring Security maps the JWT `roles` claim to authorities, and the filter chain requires the exact `CUSTOMER` role for cart routes; `ADMIN` is reserved for `/api/v1/admin/**`. The server loads or creates the user's cart, resolves each product through the catalog repository, calculates line totals from persisted cents values, and marks the response as requiring authoritative validation before checkout. The client must never send a trusted unit price or final total.

Cart updates are ownership-aware because every mutation first resolves the cart by the authenticated user ID. Product inventory is displayed as an availability signal only; checkout must re-check inventory inside a transaction and reserve stock before payment.

## Required next hardening

Implement refresh-token rotation with revocation and reuse detection, add rate limiting to registration/login, add password-reset tokens, and add integration tests with PostgreSQL/Testcontainers. Then add optimistic locking to carts and an inventory reservation table before Stripe checkout is enabled.
