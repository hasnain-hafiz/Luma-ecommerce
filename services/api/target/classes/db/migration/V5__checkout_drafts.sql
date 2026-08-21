CREATE TYPE checkout_draft_status AS ENUM ('OPEN', 'CONVERTED', 'EXPIRED', 'CANCELLED');

CREATE TABLE checkout_drafts (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_users(id),
  status checkout_draft_status NOT NULL DEFAULT 'OPEN',
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
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE checkout_draft_items (
  id UUID PRIMARY KEY,
  draft_id UUID NOT NULL REFERENCES checkout_drafts(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  product_name_snapshot VARCHAR(255) NOT NULL,
  sku_snapshot VARCHAR(120) NOT NULL,
  unit_price_cents_snapshot INTEGER NOT NULL CHECK (unit_price_cents_snapshot >= 0),
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  line_total_cents_snapshot INTEGER NOT NULL CHECK (line_total_cents_snapshot >= 0),
  image_url_snapshot TEXT
);

ALTER TABLE inventory_reservations ALTER COLUMN order_id DROP NOT NULL;
ALTER TABLE inventory_reservations ADD COLUMN draft_id UUID REFERENCES checkout_drafts(id);
ALTER TABLE inventory_reservations ADD CONSTRAINT reservation_owner_check CHECK (order_id IS NOT NULL OR draft_id IS NOT NULL);
CREATE INDEX checkout_drafts_user_idx ON checkout_drafts(user_id, created_at DESC);
CREATE INDEX checkout_draft_items_draft_idx ON checkout_draft_items(draft_id);
CREATE INDEX inventory_reservation_draft_idx ON inventory_reservations(draft_id);
