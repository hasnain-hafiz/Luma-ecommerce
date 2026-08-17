# API contract map

The backend API is versioned under `/api/v1`. This document is the implementation map; a controller should be added only when its service and authorization rules are defined.

| Area | Representative endpoints | Required authority |
|---|---|---|
| Auth | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` | Secure session or HttpOnly refresh token |
| Catalog | `GET /products`, `GET /products/{slug}`, `GET /categories` | Public reads; admin writes |
| Cart | `GET /cart`, `POST /cart/items`, `PATCH /cart/items/{id}`, `DELETE /cart/items/{id}` | Customer ownership and server recalculation |
| Orders | `POST /orders/checkout`, `GET /orders`, `GET /orders/{id}` | Customer ownership; webhook finalization |
| Payments | `POST /payments/stripe/session`, `POST /payments/stripe/webhook` | Server secret and signature verification |
| Reviews | `POST /products/{id}/reviews`, `GET /products/{id}/reviews` | Verified purchase for writes; moderation for publication |
| Admin | `GET /admin/analytics`, `POST /admin/products`, `PATCH /admin/orders/{id}` | `ADMIN` role enforced in backend |
| AI | `POST /ai/assist` | Rate limited; controlled tool allow-list only |

## Controlled AI tools

The only catalog tools permitted to the AI layer are `searchProducts`, `getProduct`, `compareProducts`, `getProductsByCategory`, and `getProductAvailability`. Tool parameters must be schema-validated by the API. Tool output should contain only public catalog fields. The model must not receive raw SQL access, payment secrets, private customer data, internal prompts, or administrative information.

## Order states

The order state machine uses the exact labels `PENDING_PAYMENT`, `PAID`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, and `REFUNDED`. The service layer must reject invalid transitions. Stripe webhook handling must be idempotent and must be the authoritative path from pending payment to paid order.

## Error shape

All errors should follow the shape already represented by `ApiContracts.ApiError`:

```json
{
  "timestamp": "2026-08-17T00:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/products",
  "errors": [{ "field": "price", "message": "must be positive" }]
}
```

Internal stack traces, database messages, credentials, tokens, and provider details must never be returned to the browser.
