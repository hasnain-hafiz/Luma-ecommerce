-- ProductEntity maps rating_average to Java double; normalize NUMERIC values to PostgreSQL DOUBLE PRECISION.
ALTER TABLE products
  ALTER COLUMN rating_average TYPE DOUBLE PRECISION
  USING rating_average::DOUBLE PRECISION;
