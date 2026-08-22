-- Webhook payload hashes are Java Strings; remove PostgreSQL CHAR padding.
ALTER TABLE payment_events
  ALTER COLUMN payload_hash TYPE VARCHAR(64)
  USING BTRIM(payload_hash);
