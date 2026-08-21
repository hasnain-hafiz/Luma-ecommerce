-- Hibernate maps currency snapshots to String/VARCHAR; normalize the original CHAR(3) columns.
-- BTRIM removes PostgreSQL CHAR padding while preserving the stored currency code.
ALTER TABLE checkout_drafts
  ALTER COLUMN currency TYPE VARCHAR(3)
  USING BTRIM(currency);

ALTER TABLE orders
  ALTER COLUMN currency TYPE VARCHAR(3)
  USING BTRIM(currency);
