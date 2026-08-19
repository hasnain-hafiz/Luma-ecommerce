package com.luma.commerce.auth;

import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthRateLimiter {
  private final RateLimitBucketRepository buckets;
  public AuthRateLimiter(RateLimitBucketRepository buckets) { this.buckets = buckets; }

  @Transactional
  public void check(String action, String clientKey, int maxRequests, Duration window) {
    var now = Instant.now();
    var key = action + ":" + clientKey;
    var bucket = buckets.findLockedByBucketKey(key).orElseGet(() -> buckets.save(RateLimitBucketEntity.create(key, now)));
    if (Duration.between(bucket.getWindowStartedAt(), now).compareTo(window) >= 0) bucket.reset(now);
    if (bucket.getRequestCount() >= maxRequests) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
    bucket.increment(now); buckets.save(bucket);
  }
}
