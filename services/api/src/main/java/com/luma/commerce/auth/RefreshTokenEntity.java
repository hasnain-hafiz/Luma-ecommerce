package com.luma.commerce.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
  @Id private UUID id;
  private UUID userId;
  private String tokenHash;
  private Instant expiresAt;
  private Instant revokedAt;
  private Instant createdAt;
  protected RefreshTokenEntity() {}
  static RefreshTokenEntity create(UUID userId, String tokenHash, Instant expiresAt) { var token = new RefreshTokenEntity(); token.id = UUID.randomUUID(); token.userId = userId; token.tokenHash = tokenHash; token.expiresAt = expiresAt; token.createdAt = Instant.now(); return token; }
  public UUID getUserId() { return userId; }
  public Instant getExpiresAt() { return expiresAt; }
  public Instant getRevokedAt() { return revokedAt; }
  public void revoke() { revokedAt = Instant.now(); }
}
