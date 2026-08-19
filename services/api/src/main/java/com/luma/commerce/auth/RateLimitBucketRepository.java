package com.luma.commerce.auth;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface RateLimitBucketRepository extends JpaRepository<RateLimitBucketEntity, String> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<RateLimitBucketEntity> findLockedByBucketKey(String bucketKey);
}
