package com.luma.commerce.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthEnhancementServiceTest {
  private final UserRepository users = mock(UserRepository.class);
  private final PasswordResetTokenRepository resets = mock(PasswordResetTokenRepository.class);
  private final EmailVerificationTokenRepository verifications = mock(EmailVerificationTokenRepository.class);
  private final OneTimeTokenService tokens = new OneTimeTokenService(new AuthServices(mock(PasswordEncoder.class)));
  private final PasswordEncoder passwords = mock(PasswordEncoder.class);
  private final AuthDeliveryPort delivery = mock(AuthDeliveryPort.class);
  private final RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
  private final AuthEnhancementService service = new AuthEnhancementService(users, resets, verifications, tokens, passwords, delivery, refreshTokens);

  @Test
  void expiredResetTokenIsRejected() {
    var user = UserEntity.customer("user@example.com", "hash");
    var token = PasswordResetTokenEntity.create(user.getId(), "digest", Instant.now().minusSeconds(1));
    when(resets.findByTokenHash(any())).thenReturn(Optional.of(token)); when(users.findById(user.getId())).thenReturn(Optional.of(user));
    assertThrows(IllegalArgumentException.class, () -> service.confirmPasswordReset(new AuthEnhancementContracts.ConfirmPasswordReset("raw", "new-password-123")));
  }

  @Test
  void consumedVerificationTokenIsRejected() {
    var user = UserEntity.customer("user@example.com", "hash");
    var token = EmailVerificationTokenEntity.create(user.getId(), "digest", Instant.now().plusSeconds(60)); token.consume();
    when(verifications.findByTokenHash(any())).thenReturn(Optional.of(token));
    assertThrows(IllegalArgumentException.class, () -> service.confirmEmailVerification(new AuthEnhancementContracts.ConfirmEmailVerification("raw")));
  }

  @Test
  void validVerificationConsumesTokenAndVerifiesUser() {
    var user = UserEntity.customer("user@example.com", "hash");
    var token = EmailVerificationTokenEntity.create(user.getId(), "digest", Instant.now().plusSeconds(60));
    when(verifications.findByTokenHash(any())).thenReturn(Optional.of(token)); when(users.findById(user.getId())).thenReturn(Optional.of(user));
    service.confirmEmailVerification(new AuthEnhancementContracts.ConfirmEmailVerification("raw"));
    org.junit.jupiter.api.Assertions.assertTrue(user.isEmailVerified());
  }
}
