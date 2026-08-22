# Vercel Frontend Deployment

Luma Commerce is a Vite-powered React storefront. The repository also contains the local Manus Express development server and the Java API source, so the Vercel project must be configured as a static frontend deployment rather than a full-stack Node deployment.

## Recommended Vercel settings

| Setting | Value |
|---|---|
| Framework preset | Vite |
| Root directory | Repository root (`.`) |
| Install command | `pnpm install --frozen-lockfile` |
| Build command | `pnpm exec vite build` |
| Output directory | `dist/public` |
| Production branch | `main` |
|

These settings are also committed in `vercel.json`. The SPA rewrite sends client-side routes such as `/shop`, `/checkout`, and `/orders` to `index.html` so the React router can resolve them in the browser.

## Environment variables

Add the following public variables in Vercel under **Project Settings → Environment Variables**. Apply them to Preview and Production as appropriate.

| Variable | Value or purpose |
|---|---|
| `VITE_ORDER_API_BASE_URL` | `https://luma-ecommerce.onrender.com` |
| `VITE_APP_ID` | The existing public Manus application ID, if Manus OAuth is retained on the Vercel frontend |
| `VITE_OAUTH_PORTAL_URL` | The existing public Manus OAuth portal URL, if Manus OAuth is retained |
| `VITE_FRONTEND_FORGE_API_URL` | Existing public frontend Forge API URL, if map or built-in frontend services remain enabled |
| `VITE_FRONTEND_FORGE_API_KEY` | Existing public frontend Forge key, only if the deployed UI requires that browser-side service |
| `VITE_ANALYTICS_ENDPOINT` | Existing analytics endpoint, if analytics is enabled |
| `VITE_ANALYTICS_WEBSITE_ID` | Existing analytics website ID, if analytics is enabled |

Do not add server-only values such as `RAZORPAY_KEY_SECRET`, `RAZORPAY_WEBHOOK_SECRET`, database credentials, `JWT_SECRET`, or the Java API's OAuth client secrets to Vercel frontend variables. Razorpay checkout should receive only the public key and a server-created order; signature verification remains server-side.

## Backend and CORS follow-up

The browser will call the Render API from the Vercel origin. Confirm that the Java API allows the final Vercel production domain and preview domains in its CORS configuration, and that cookies are configured appropriately if cookie-based authentication is used. The frontend already supports the Render API origin through `VITE_ORDER_API_BASE_URL`.

## Publish procedure

Connect the `hasnain-hafiz/Luma-ecommerce` GitHub repository to a Vercel project owned by the connected Vercel account. Select the `main` branch and apply the settings above. Vercel will create a preview deployment from the branch and can subsequently promote the verified build to production. Before production promotion, test catalog loading, login/session behavior, cart mutations, Razorpay order creation, and the return path to order confirmation.

Manus built-in hosting remains an alternative with fewer external configuration steps; choosing Vercel is supported, but the Vercel origin must be allowed by the Render API and any authentication provider.
