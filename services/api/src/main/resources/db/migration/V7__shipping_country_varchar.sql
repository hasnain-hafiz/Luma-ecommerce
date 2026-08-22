-- Java String mappings require variable-width text for country snapshots.
-- Trim PostgreSQL CHAR padding before converting the existing values.
ALTER TABLE checkout_drafts
  ALTER COLUMN shipping_country TYPE VARCHAR(2)
  USING BTRIM(shipping_country);

ALTER TABLE orders
  ALTER COLUMN shipping_country TYPE VARCHAR(2)
  USING BTRIM(shipping_country);
