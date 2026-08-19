package com.luma.commerce.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationTokenEntity {
  @Id private UUID id;
  private UUID userId;
  private String tokenHash;
  private Instant expiresAt;
  private Instant consumedAt;
  private Instant createdAt;
  protected EmailVerificationTokenEntity() {}
  static EmailVerificationTokenEntity create(UUID userId, String tokenHash, Instant expiresAt) { var token = new EmailVerificationTokenEntity(); token.id = UUID.randomUUID(); token.userId = userId; token.tokenHash = tokenHash; token.expiresAt = expiresAt; token.createdAt = Instant.now(); return token; }
  public UUID getUserId() { return userId; }
  public String getTokenHash() { return tokenHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public Instant getConsumedAt() { return consumedAt; }
  public void consume() { consumedAt = Instant.now(); }
}
