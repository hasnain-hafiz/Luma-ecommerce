# Security integration boundary

The API is stateless and configured as a JWT resource server. Public catalog reads and health documentation endpoints are permit-listed. Requests under `/api/v1/admin/**`, including product create, update, and archive, require the exact Spring Security role `ADMIN`, which corresponds to a JWT authority of `ROLE_ADMIN`.

The current project supplies JWT verification through `JWT_SECRET` and does not issue tokens yet. The authentication module should later own registration, password hashing, refresh-token rotation, revocation, and claims issuance. It must emit the `ROLE_CUSTOMER` or `ROLE_ADMIN` authority expected by this resource-server configuration.

Never use the placeholder JWT secret outside local development. The admin product controller is intentionally protected at the HTTP filter-chain boundary rather than relying on a UI check. A non-admin request must receive `403 Forbidden`; the frontend can hide admin navigation for usability, but that is not an authorization control.
