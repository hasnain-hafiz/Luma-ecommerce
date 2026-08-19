package com.luma.commerce.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Configuration
class AuthConfiguration {
  @Bean PasswordEncoder passwordEncoder() { return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(); }
}

@Service
public class AuthServices {
  private final PasswordEncoder passwordEncoder;
  public AuthServices(PasswordEncoder passwordEncoder) { this.passwordEncoder = passwordEncoder; }
  public String hashPassword(String rawPassword) { return passwordEncoder.encode(rawPassword); }
  public boolean matchesPassword(String rawPassword, String encodedPassword) { return passwordEncoder.matches(rawPassword, encodedPassword); }
  public Instant accessExpiry() { return Instant.now().plus(15, ChronoUnit.MINUTES); }
  public Instant refreshExpiry() { return Instant.now().plus(30, ChronoUnit.DAYS); }
  public String newOpaqueRefreshToken() { return UUID.randomUUID() + "." + UUID.randomUUID(); }
  public String hashRefreshToken(String token) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
      var output = new StringBuilder();
      for (byte value : digest) output.append(String.format("%02x", value));
      return output.toString();
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is unavailable", exception);
    }
  }
}
