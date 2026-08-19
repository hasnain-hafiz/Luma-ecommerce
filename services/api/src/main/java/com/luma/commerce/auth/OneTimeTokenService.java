package com.luma.commerce.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class OneTimeTokenService {
  private final SecureRandom random = new SecureRandom();
  private final AuthServices hashing;

  public OneTimeTokenService(AuthServices hashing) { this.hashing = hashing; }

  public IssuedToken issue(String email, String purpose, int lifetimeMinutes) {
    var bytes = new byte[32];
    random.nextBytes(bytes);
    var raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    return new IssuedToken(email, purpose, raw, hashing.hashRefreshToken(raw), Instant.now().plus(lifetimeMinutes, ChronoUnit.MINUTES));
  }

  public String issueHash(String raw) { return hashing.hashRefreshToken(raw); }
  public boolean matches(String raw, String expectedHash) { return issueHash(raw).equals(expectedHash); }
  public record IssuedToken(String recipientEmail, String purpose, String rawToken, String tokenHash, Instant expiresAt) {}
}
