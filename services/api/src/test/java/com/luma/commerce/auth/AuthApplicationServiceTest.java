package com.luma.commerce.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

class AuthApplicationServiceTest {
  @Test
  void duplicateRegistrationIsRejected() {
    var users = mock(UserRepository.class); var existing = UserEntity.customer("user@example.com", "hash");
    when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(existing));
    var service = new AuthApplicationService(users, mock(RefreshTokenRepository.class), mock(PasswordEncoder.class), mock(AuthServices.class), mock(JwtEncoder.class), mock(OneTimeTokenService.class), mock(EmailVerificationTokenRepository.class), mock(AuthDeliveryPort.class));
    assertThrows(IllegalArgumentException.class, () -> service.register(new AuthContracts.RegisterRequest("user@example.com", "long-password-123")));
  }

  @Test
  void unverifiedUserCannotLogin() {
    var users = mock(UserRepository.class); var user = UserEntity.customer("user@example.com", "hash");
    when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
    var service = new AuthApplicationService(users, mock(RefreshTokenRepository.class), mock(PasswordEncoder.class), mock(AuthServices.class), mock(JwtEncoder.class), mock(OneTimeTokenService.class), mock(EmailVerificationTokenRepository.class), mock(AuthDeliveryPort.class));
    assertThrows(IllegalArgumentException.class, () -> service.login(new AuthContracts.LoginRequest("user@example.com", "long-password-123")));
  }

  @Test
  void refreshRevokesPresentedTokenBeforeIssuingReplacement() {
    var users = mock(UserRepository.class); var refreshTokens = mock(RefreshTokenRepository.class); var tokenService = mock(AuthServices.class); var encoder = mock(JwtEncoder.class); var jwt = mock(Jwt.class); var stored = mock(RefreshTokenEntity.class); var user = UserEntity.customer("user@example.com", "hash"); user.verifyEmail();
    when(tokenService.hashRefreshToken("raw")).thenReturn("digest"); when(refreshTokens.findByTokenHash("digest")).thenReturn(Optional.of(stored)); when(stored.getRevokedAt()).thenReturn(null); when(stored.getExpiresAt()).thenReturn(Instant.now().plusSeconds(60)); when(stored.getUserId()).thenReturn(user.getId()); when(users.findById(user.getId())).thenReturn(Optional.of(user)); when(tokenService.accessExpiry()).thenReturn(Instant.now().plusSeconds(60)); when(tokenService.newOpaqueRefreshToken()).thenReturn("replacement"); when(tokenService.refreshExpiry()).thenReturn(Instant.now().plusSeconds(3600)); when(encoder.encode(any())).thenReturn(jwt); when(jwt.getTokenValue()).thenReturn("access");
    var service = new AuthApplicationService(users, refreshTokens, mock(PasswordEncoder.class), tokenService, encoder, mock(OneTimeTokenService.class), mock(EmailVerificationTokenRepository.class), mock(AuthDeliveryPort.class));
    assertTrue(service.refresh(new AuthContracts.RefreshRequest("raw")).accessToken().equals("access"));
    org.mockito.Mockito.verify(stored).revoke();
  }
}
