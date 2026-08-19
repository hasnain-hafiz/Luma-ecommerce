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
public class AuthController {
  private final AuthApplicationService auth;
  private final AuthRateLimiter limiter;

  public AuthController(AuthApplicationService auth, AuthRateLimiter limiter) { this.auth = auth; this.limiter = limiter; }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthContracts.UserView register(@Valid @RequestBody AuthContracts.RegisterRequest request, HttpServletRequest http) {
    limiter.check("register", clientKey(http, request.email()), 5, Duration.ofHours(1)); return auth.register(request);
  }

  @PostMapping("/login")
  public AuthContracts.TokenPair login(@Valid @RequestBody AuthContracts.LoginRequest request, HttpServletRequest http) {
    limiter.check("login", clientKey(http, request.email()), 10, Duration.ofMinutes(15)); return auth.login(request);
  }

  @PostMapping("/refresh")
  public AuthContracts.TokenPair refresh(@Valid @RequestBody AuthContracts.RefreshRequest request, HttpServletRequest http) {
    limiter.check("refresh", clientKey(http, request.refreshToken()), 20, Duration.ofHours(1)); return auth.refresh(request);
  }

  private String clientKey(HttpServletRequest request, String subject) { return request.getRemoteAddr() + ":" + Integer.toHexString(subject.toLowerCase().hashCode()); }
}
