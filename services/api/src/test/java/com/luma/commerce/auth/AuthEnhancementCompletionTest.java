package com.luma.commerce.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

class AuthEnhancementCompletionTest {
  @Test
  void resetSuccessChangesPasswordConsumesTokenAndRevokesSessions() {
    var users = mock(UserRepository.class); var resets = mock(PasswordResetTokenRepository.class); var verifications = mock(EmailVerificationTokenRepository.class); var refresh = mock(RefreshTokenRepository.class); var passwords = mock(PasswordEncoder.class); var user = UserEntity.customer("user@example.com", "old"); var token = PasswordResetTokenEntity.create(user.getId(), "digest", Instant.now().plusSeconds(60));
    when(resets.findByTokenHash(any())).thenReturn(Optional.of(token)); when(users.findById(user.getId())).thenReturn(Optional.of(user)); when(passwords.encode(any())).thenReturn("new-hash");
    var service = new AuthEnhancementService(users, resets, verifications, new OneTimeTokenService(new AuthServices(passwords)), passwords, mock(AuthDeliveryPort.class), refresh);
    service.confirmPasswordReset(new AuthEnhancementContracts.ConfirmPasswordReset("raw", "new-password-123"));
    verify(refresh).revokeAllForUser(user.getId()); verify(users).save(user); verify(resets).save(token);
  }

  @Test
  void consumedResetTokenIsRejected() {
    var token = PasswordResetTokenEntity.create(java.util.UUID.randomUUID(), "digest", Instant.now().plusSeconds(60)); token.consume(); var resets = mock(PasswordResetTokenRepository.class);
    when(resets.findByTokenHash(any())).thenReturn(Optional.of(token));
    var service = new AuthEnhancementService(mock(UserRepository.class), resets, mock(EmailVerificationTokenRepository.class), mock(OneTimeTokenService.class), mock(PasswordEncoder.class), mock(AuthDeliveryPort.class), mock(RefreshTokenRepository.class));
    assertThrows(IllegalArgumentException.class, () -> service.confirmPasswordReset(new AuthEnhancementContracts.ConfirmPasswordReset("raw", "new-password-123")));
  }

  @Test
  void expiredVerificationTokenIsRejected() {
    var token = EmailVerificationTokenEntity.create(java.util.UUID.randomUUID(), "digest", Instant.now().minusSeconds(1)); var verifications = mock(EmailVerificationTokenRepository.class);
    when(verifications.findByTokenHash(any())).thenReturn(Optional.of(token));
    var service = new AuthEnhancementService(mock(UserRepository.class), mock(PasswordResetTokenRepository.class), verifications, mock(OneTimeTokenService.class), mock(PasswordEncoder.class), mock(AuthDeliveryPort.class), mock(RefreshTokenRepository.class));
    assertThrows(IllegalArgumentException.class, () -> service.confirmEmailVerification(new AuthEnhancementContracts.ConfirmEmailVerification("raw")));
  }

  @Test
  void registrationSuccessSavesCustomerAndSendsVerification() {
    var users = mock(UserRepository.class); var verificationRepo = mock(EmailVerificationTokenRepository.class); var delivery = mock(AuthDeliveryPort.class); var encoder = mock(PasswordEncoder.class); var tokenService = mock(OneTimeTokenService.class); var issued = new OneTimeTokenService.IssuedToken("user@example.com", "email-verification", "raw", "digest", Instant.now().plusSeconds(60));
    when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.empty()); when(encoder.encode(any())).thenReturn("hash"); when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); when(tokenService.issue(any(), any(), any(Integer.class))).thenReturn(issued);
    var service = new AuthApplicationService(users, mock(RefreshTokenRepository.class), encoder, mock(AuthServices.class), mock(JwtEncoder.class), tokenService, verificationRepo, delivery);
    assertTrue(service.register(new AuthContracts.RegisterRequest("user@example.com", "new-password-123")).email().equals("user@example.com")); verify(delivery).send(any());
  }

  @Test
  void verifiedUserCanLogin() {
    var users = mock(UserRepository.class); var encoder = mock(PasswordEncoder.class); var user = UserEntity.customer("user@example.com", "hash"); user.verifyEmail(); var tokenService = mock(AuthServices.class); var jwtEncoder = mock(JwtEncoder.class); var jwt = mock(org.springframework.security.oauth2.jwt.Jwt.class);
    when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user)); when(encoder.matches("password", "hash")).thenReturn(true); when(tokenService.accessExpiry()).thenReturn(Instant.now().plusSeconds(60)); when(tokenService.newOpaqueRefreshToken()).thenReturn("refresh"); when(tokenService.refreshExpiry()).thenReturn(Instant.now().plusSeconds(60)); when(tokenService.hashRefreshToken("refresh")).thenReturn("digest"); when(jwtEncoder.encode(any())).thenReturn(jwt); when(jwt.getTokenValue()).thenReturn("access");
    var service = new AuthApplicationService(users, mock(RefreshTokenRepository.class), encoder, tokenService, jwtEncoder, mock(OneTimeTokenService.class), mock(EmailVerificationTokenRepository.class), mock(AuthDeliveryPort.class));
    assertTrue(service.login(new AuthContracts.LoginRequest("user@example.com", "password")).accessToken().equals("access"));
  }
}
