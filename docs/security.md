# Security integration boundary

The API is stateless and configured as a JWT resource server. Public catalog reads and health documentation endpoints are permit-listed. Requests under `/api/v1/admin/**`, including product create, update, and archive, require the exact Spring Security role `ADMIN`, which corresponds to a JWT authority of `ROLE_ADMIN`.

The current project supplies JWT verification through `JWT_SECRET` and does not issue tokens yet. The authentication module should later own registration, password hashing, refresh-token rotation, revocation, and claims issuance. It must emit the `ROLE_CUSTOMER` or `ROLE_ADMIN` authority expected by this resource-server configuration.

Never use the placeholder JWT secret outside local development. The admin product controller is intentionally protected at the HTTP filter-chain boundary rather than relying on a UI check. A non-admin request must receive `403 Forbidden`; the frontend can hide admin navigation for usability, but that is not an authorization control.


## Frontend order API session bridge

The order client sends `credentials: "include"` for deployments that use an authenticated cookie or same-origin proxy. When the Java API is reached directly as a JWT resource server, the client also forwards `Authorization: Bearer <token>` from `sessionStorage` under the key `luma-access-token`. The authentication integration must populate that value only after a successful login and replace it after refresh-token rotation; it must remove it on logout or refresh-token revocation. The storefront never embeds a long-lived secret or uses a static token.

If neither a valid bearer token nor an authenticated cookie is available, the order views display an authentication-required state with a sign-in action. Network and server failures remain retryable through an explicit Retry control. A production deployment should connect the token population and refresh lifecycle to the Java auth endpoints or place an authenticated same-origin proxy in front of `/api/v1/orders`.
