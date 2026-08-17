# Catalog backend slice

The catalog slice is the first production-oriented API module added after the storefront foundation. It establishes PostgreSQL tables for `categories`, `brands`, `products`, and `product_images`, with constraints for non-negative prices and inventory, rating bounds, unique SKUs/slugs, and indexes for common active-product queries.

## Request flow

`CatalogController` accepts versioned public requests under `/api/v1/products`. It validates pagination bounds, converts query parameters into `CatalogContracts.ProductQuery`, and delegates to `CatalogService`. The service selects a stable sort, asks `ProductRepository` for a page of active products, and maps persistence objects into response DTOs. The controller never exposes JPA entities directly.

The current repository method provides the safe active-product pagination path. The next implementation increment should replace the simple repository method with a composed `Specification` or dedicated query implementation so `search`, `category`, `brand`, price range, rating, and availability are all applied in SQL rather than filtered in memory. This is intentionally documented rather than hidden behind a false claim of completion.

## Frontend boundary

The existing storefront currently reads a local catalog adapter so the preview remains deterministic. The integration point is the same product vocabulary represented by `ProductSummary` and `ProductDetail`: slug, SKU, name, price in cents, compare-at price, rating aggregate, inventory availability, brand/category labels, and image URLs. When the API is connected, client-side display data can be replaced by `/api/v1/products` and `/api/v1/products/{slug}` while checkout continues to recalculate all monetary and inventory values server-side.

## Admin safety

Product writes should be introduced behind a dedicated `ADMIN` authorization layer and audited. Product archival should set `active = false` rather than deleting records referenced by historical order snapshots. Inventory updates should use optimistic locking or an atomic update. Customer reviews and testimonials must never be seeded or fabricated as catalog content.
