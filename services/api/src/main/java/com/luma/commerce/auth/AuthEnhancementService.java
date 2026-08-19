package com.luma.commerce.auth;

import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthEnhancementService {
  private final UserRepository users;
  private final PasswordResetTokenRepository resets;
  private final EmailVerificationTokenRepository verifications;
  private final OneTimeTokenService tokens;
  private final PasswordEncoder passwords;
  private final AuthDeliveryPort delivery;
  private final RefreshTokenRepository refreshTokens;

  public AuthEnhancementService(UserRepository users, PasswordResetTokenRepository resets, EmailVerificationTokenRepository verifications, OneTimeTokenService tokens, PasswordEncoder passwords, AuthDeliveryPort delivery, RefreshTokenRepository refreshTokens) {
    this.users = users; this.resets = resets; this.verifications = verifications; this.tokens = tokens; this.passwords = passwords; this.delivery = delivery; this.refreshTokens = refreshTokens;
  }

  @Transactional
  public void requestPasswordReset(String email) {
    users.findByEmailIgnoreCase(email.trim().toLowerCase()).ifPresent(user -> {
      var issued = tokens.issue(user.getEmail(), "password-reset", 30);
      resets.save(PasswordResetTokenEntity.create(user.getId(), issued.tokenHash(), issued.expiresAt()));
      delivery.send(new AuthEnhancementContracts.DeliveryRequest(user.getEmail(), issued.rawToken(), issued.purpose()));
    });
  }

  @Transactional
  public void confirmPasswordReset(AuthEnhancementContracts.ConfirmPasswordReset request) {
    var reset = resets.findByTokenHash(tokens.issueHash(request.token())).orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
    if (reset.getConsumedAt() != null || reset.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Invalid or expired reset token");
    var user = users.findById(reset.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
    refreshTokens.revokeAllForUser(user.getId()); user.changePassword(passwords.encode(request.newPassword())); reset.consume(); users.save(user); resets.save(reset);
  }

  @Transactional
  public void requestEmailVerification(String email) {
    users.findByEmailIgnoreCase(email.trim().toLowerCase()).ifPresent(user -> {
      var issued = tokens.issue(user.getEmail(), "email-verification", 60);
      verifications.save(EmailVerificationTokenEntity.create(user.getId(), issued.tokenHash(), issued.expiresAt()));
      delivery.send(new AuthEnhancementContracts.DeliveryRequest(user.getEmail(), issued.rawToken(), issued.purpose()));
    });
  }

  @Transactional
  public void confirmEmailVerification(AuthEnhancementContracts.ConfirmEmailVerification request) {
    var verification = verifications.findByTokenHash(tokens.issueHash(request.token())).orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));
    if (verification.getConsumedAt() != null || verification.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Invalid or expired verification token");
    var user = users.findById(verification.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
    user.verifyEmail(); verification.consume(); users.save(user); verifications.save(verification);
  }
}
