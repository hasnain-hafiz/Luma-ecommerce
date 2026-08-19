# Authentication enhancements

The authentication layer now includes password-reset and email-verification token flows. Both flows generate cryptographically random opaque tokens, store only SHA-256 digests, enforce expiry, and consume each token once. Recovery endpoints return an accepted response without revealing whether an email address exists, which reduces account-enumeration risk.

## Email delivery boundary

`AuthDeliveryPort` is the provider-neutral seam for transactional email. The current `NoopAuthDeliveryPort` prevents raw tokens from being logged or returned while keeping local startup deterministic. A production adapter should send password-reset and verification messages through an approved transactional-email provider, with secrets supplied through deployment configuration. It should never persist or log the raw token.

## Rate limiting

Authentication endpoints use database-backed, pessimistic-lock rate-limit buckets keyed by action, client address, and a normalized account-derived fingerprint. The initial policies are intentionally conservative and can be tuned through configuration in a later increment.

| Endpoint family | Initial limit | Window |
|---|---:|---:|
| Registration | 5 requests | 1 hour |
| Login | 10 requests | 15 minutes |
| Refresh | 20 requests | 1 hour |
| Password-reset request | 3 requests | 1 hour |
| Verification request | 5 requests | 1 hour |
| Token confirmation | 10 requests | 1 hour |

The rate limiter is a backend control, not a frontend convenience. Production operations should add proxy-level throttling, metrics, alerting, and cleanup for stale bucket rows. The database bucket implementation is suitable for a single relational deployment; a high-volume multi-region deployment should move the hot path to a shared low-latency store.

## Operational requirements

Before production launch, configure a real delivery adapter, add email templates, add Testcontainers coverage for token consumption and concurrent rate-limit updates, and define support workflows for expired reset links. Password-reset confirmation must also revoke all existing refresh tokens for the user in the same transaction.
