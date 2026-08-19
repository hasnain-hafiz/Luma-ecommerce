package com.luma.commerce.auth;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

class AuthEnhancementFlowTest {
  @Test
  void resetTokenIsSingleUseAfterConsumption() {
    var token = PasswordResetTokenEntity.create(java.util.UUID.randomUUID(), "digest", Instant.now().plusSeconds(60));
    assertTrue(token.getConsumedAt() == null);
    token.consume();
    assertNotNull(token.getConsumedAt());
  }

  @Test
  void oneTimeTokenDigestDoesNotEqualRawToken() {
    var service = new OneTimeTokenService(new AuthServices(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()));
    var issued = service.issue("user@example.com", "email-verification", 60);
    assertNotEquals(issued.rawToken(), issued.tokenHash());
    assertTrue(issued.expiresAt().isAfter(Instant.now()));
  }

  @Test
  void rateLimitBucketResetsAfterWindowBoundary() {
    var start = Instant.now().minusSeconds(120);
    var bucket = RateLimitBucketEntity.create("login:client", start);
    bucket.increment(Instant.now());
    bucket.reset(Instant.now());
    assertTrue(bucket.getRequestCount() == 0);
  }
}
