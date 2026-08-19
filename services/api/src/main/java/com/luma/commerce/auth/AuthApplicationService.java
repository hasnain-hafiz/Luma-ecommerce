package com.luma.commerce.auth;

import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService {
  private final UserRepository users;
  private final RefreshTokenRepository refreshTokens;
  private final PasswordEncoder passwords;
  private final AuthServices tokens;
  private final JwtEncoder jwtEncoder;

  public AuthApplicationService(UserRepository users, RefreshTokenRepository refreshTokens, PasswordEncoder passwords, AuthServices tokens, JwtEncoder jwtEncoder) {
    this.users = users; this.refreshTokens = refreshTokens; this.passwords = passwords; this.tokens = tokens; this.jwtEncoder = jwtEncoder;
  }

  public AuthContracts.UserView register(AuthContracts.RegisterRequest request) {
    users.findByEmailIgnoreCase(request.email()).ifPresent(existing -> { throw new IllegalArgumentException("Email already registered"); });
    var user = users.save(UserEntity.customer(request.email().trim().toLowerCase(), passwords.encode(request.password())));
    return view(user);
  }

  public AuthContracts.TokenPair login(AuthContracts.LoginRequest request) {
    var user = users.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!user.isActive() || !passwords.matches(request.password(), user.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
    return issue(user);
  }

  public AuthContracts.TokenPair refresh(AuthContracts.RefreshRequest request) {
    var stored = refreshTokens.findByTokenHash(tokens.hashRefreshToken(request.refreshToken())).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    if (stored.getRevokedAt() != null || stored.getExpiresAt().isBefore(Instant.now())) throw new IllegalArgumentException("Refresh token expired or revoked");
    stored.revoke(); refreshTokens.save(stored);
    var user = users.findById(stored.getUserId()).filter(UserEntity::isActive).orElseThrow(() -> new IllegalArgumentException("User is inactive"));
    return issue(user);
  }

  private AuthContracts.TokenPair issue(UserEntity user) {
    var accessExpiry = tokens.accessExpiry();
    var claims = JwtClaimsSet.builder().subject(user.getId().toString()).claim("email", user.getEmail()).claim("roles", java.util.List.of("ROLE_" + user.getRole().name())).issuedAt(Instant.now()).expiresAt(accessExpiry).build();
    var access = jwtEncoder.encode(JwtEncoderParameters.from(MacAlgorithm.HS256, claims)).getTokenValue();
    var refresh = tokens.newOpaqueRefreshToken();
    refreshTokens.save(RefreshTokenEntity.create(user.getId(), tokens.hashRefreshToken(refresh), tokens.refreshExpiry()));
    return new AuthContracts.TokenPair(access, refresh, accessExpiry, tokens.refreshExpiry(), view(user));
  }

  private AuthContracts.UserView view(UserEntity user) { return new AuthContracts.UserView(user.getId(), user.getEmail(), user.getRole()); }
}
