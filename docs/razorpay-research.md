# Razorpay integration findings

Razorpay’s official Standard Checkout flow requires creating a Razorpay Order on the server, opening Checkout with the returned order identifier, and verifying the returned payment signature on the server before treating payment as successful. Razorpay’s API base is `https://api.razorpay.com/v1` and uses key ID/secret credentials.

Razorpay webhooks are HTTP POST requests signed with the webhook secret. The service must verify the signature against the raw request body before processing events, and the payment event identifier should be persisted for idempotency. The payment gateway must retain the existing server-authoritative totals, inventory reservations, and webhook-confirmed order conversion.

Official references:

- https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/integration-steps/
- https://razorpay.com/docs/api/?preferred-country=US
- https://razorpay.com/docs/webhooks/validate-test/?preferred-country=US
- https://razorpay.com/docs/payments/server-integration/java/integration-steps/?preferred-country=US
