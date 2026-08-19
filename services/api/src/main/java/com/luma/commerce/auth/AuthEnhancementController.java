package com.luma.commerce.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthEnhancementController {
  private final AuthEnhancementService auth;
  private final AuthRateLimiter limiter;

  public AuthEnhancementController(AuthEnhancementService auth, AuthRateLimiter limiter) { this.auth = auth; this.limiter = limiter; }

  @PostMapping("/password-reset/request")
  public AuthEnhancementContracts.AcceptedResponse requestReset(@Valid @RequestBody AuthEnhancementContracts.RequestEmail request, HttpServletRequest http) {
    limiter.check("password-reset", clientKey(http, request.email()), 3, Duration.ofHours(1)); auth.requestPasswordReset(request.email()); return new AuthEnhancementContracts.AcceptedResponse(true);
  }

  @PostMapping("/password-reset/confirm")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void confirmReset(@Valid @RequestBody AuthEnhancementContracts.ConfirmPasswordReset request, HttpServletRequest http) {
    limiter.check("password-reset-confirm", clientKey(http, request.token()), 10, Duration.ofHours(1)); auth.confirmPasswordReset(request);
  }

  @PostMapping("/email-verification/request")
  public AuthEnhancementContracts.AcceptedResponse requestVerification(@Valid @RequestBody AuthEnhancementContracts.RequestEmail request, HttpServletRequest http) {
    limiter.check("email-verification", clientKey(http, request.email()), 5, Duration.ofHours(1)); auth.requestEmailVerification(request.email()); return new AuthEnhancementContracts.AcceptedResponse(true);
  }

  @PostMapping("/email-verification/confirm")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void confirmVerification(@Valid @RequestBody AuthEnhancementContracts.ConfirmEmailVerification request, HttpServletRequest http) {
    limiter.check("email-verification-confirm", clientKey(http, request.token()), 10, Duration.ofHours(1)); auth.confirmEmailVerification(request);
  }

  private String clientKey(HttpServletRequest request, String subject) { return request.getRemoteAddr() + ":" + Integer.toHexString(subject.toLowerCase().hashCode()); }
}
