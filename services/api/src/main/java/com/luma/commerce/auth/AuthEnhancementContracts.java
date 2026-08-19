package com.luma.commerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthEnhancementContracts {
  private AuthEnhancementContracts() {}
  public record RequestEmail(@Email @NotBlank String email) {}
  public record ConfirmPasswordReset(@NotBlank String token, @NotBlank @Size(min = 12, max = 128) String newPassword) {}
  public record ConfirmEmailVerification(@NotBlank String token) {}
  public record AcceptedResponse(boolean accepted) {}
  public record DeliveryRequest(String recipientEmail, String token, String purpose) {}
}
