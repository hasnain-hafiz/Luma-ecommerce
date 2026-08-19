package com.luma.commerce.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "auth_rate_limit_buckets")
public class RateLimitBucketEntity {
  @Id private String bucketKey;
  private Instant windowStartedAt;
  private int requestCount;
  private Instant updatedAt;
  protected RateLimitBucketEntity() {}
  static RateLimitBucketEntity create(String key, Instant now) { var bucket = new RateLimitBucketEntity(); bucket.bucketKey = key; bucket.windowStartedAt = now; bucket.requestCount = 0; bucket.updatedAt = now; return bucket; }
  public Instant getWindowStartedAt() { return windowStartedAt; }
  public int getRequestCount() { return requestCount; }
  public void reset(Instant now) { windowStartedAt = now; requestCount = 0; updatedAt = now; }
  public void increment(Instant now) { requestCount++; updatedAt = now; }
}

