# Render deployment verification

Verified on 2026-08-22:

- Public API URL: https://luma-ecommerce.onrender.com
- API health URL: https://luma-ecommerce.onrender.com/api/v1/health
- API response: `{"service":"luma-commerce-api","status":"UP","version":"0.1.0-SNAPSHOT"}`
- Actuator health URL: https://luma-ecommerce.onrender.com/actuator/health
- Actuator response: `{"status":"UP","groups":["liveness","readiness"]}`

The deployed Java/Spring Boot API is reachable and reports healthy. The frontend should use `VITE_ORDER_API_BASE_URL=https://luma-ecommerce.onrender.com`.
