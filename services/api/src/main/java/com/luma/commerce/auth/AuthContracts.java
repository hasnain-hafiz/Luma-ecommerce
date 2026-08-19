package com.luma.commerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class AuthContracts {
  private AuthContracts() {}
  public enum Role { CUSTOMER, ADMIN }
  public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 12, max = 128) String password) {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record RefreshRequest(@NotBlank String refreshToken) {}
  public record UserView(UUID id, String email, Role role) {}
  public record TokenPair(String accessToken, String refreshToken, Instant accessExpiresAt, Instant refreshExpiresAt, UserView user) {}
}
