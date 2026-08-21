CREATE TYPE order_status AS ENUM ('PENDING_PAYMENT', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED');

CREATE TABLE orders (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_users(id),
  status order_status NOT NULL DEFAULT 'PENDING_PAYMENT',
  currency CHAR(3) NOT NULL DEFAULT 'USD',
  subtotal_cents INTEGER NOT NULL CHECK (subtotal_cents >= 0),
  shipping_cents INTEGER NOT NULL CHECK (shipping_cents >= 0),
  tax_cents INTEGER NOT NULL CHECK (tax_cents >= 0),
  total_cents INTEGER NOT NULL CHECK (total_cents >= 0),
  shipping_name VARCHAR(160) NOT NULL,
  shipping_line1 VARCHAR(255) NOT NULL,
  shipping_line2 VARCHAR(255),
  shipping_city VARCHAR(120) NOT NULL,
  shipping_region VARCHAR(120) NOT NULL,
  shipping_postal_code VARCHAR(32) NOT NULL,
  shipping_country CHAR(2) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  product_name_snapshot VARCHAR(255) NOT NULL,
  sku_snapshot VARCHAR(120) NOT NULL,
  unit_price_cents_snapshot INTEGER NOT NULL CHECK (unit_price_cents_snapshot >= 0),
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  line_total_cents_snapshot INTEGER NOT NULL CHECK (line_total_cents_snapshot >= 0),
  image_url_snapshot TEXT
);

CREATE TABLE inventory_reservations (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  status VARCHAR(16) NOT NULL CHECK (status IN ('RESERVED', 'RELEASED', 'COMMITTED')),
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (order_id, product_id)
);

CREATE TABLE payment_events (
  id UUID PRIMARY KEY,
  provider VARCHAR(32) NOT NULL,
  provider_event_id VARCHAR(255) NOT NULL UNIQUE,
  order_id UUID REFERENCES orders(id),
  event_type VARCHAR(120) NOT NULL,
  received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  processed_at TIMESTAMPTZ,
  payload_hash CHAR(64) NOT NULL
);

CREATE INDEX orders_user_created_idx ON orders(user_id, created_at DESC);
CREATE INDEX orders_status_idx ON orders(status, updated_at);
CREATE INDEX order_items_order_idx ON order_items(order_id);
CREATE INDEX inventory_reservation_expiry_idx ON inventory_reservations(status, expires_at);

ALTER TABLE products ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
