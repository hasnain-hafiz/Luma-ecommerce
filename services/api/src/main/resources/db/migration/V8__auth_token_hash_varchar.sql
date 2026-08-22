-- Hash values are Java Strings; use variable-width text instead of PostgreSQL CHAR padding.
ALTER TABLE password_reset_tokens
  ALTER COLUMN token_hash TYPE VARCHAR(64)
  USING BTRIM(token_hash);

ALTER TABLE email_verification_tokens
  ALTER COLUMN token_hash TYPE VARCHAR(64)
  USING BTRIM(token_hash);
