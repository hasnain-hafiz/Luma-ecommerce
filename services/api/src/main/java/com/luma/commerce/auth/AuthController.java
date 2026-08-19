package com.luma.commerce.auth;

import jakarta.validation.Valid;
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

  public AuthController(AuthApplicationService auth) { this.auth = auth; }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthContracts.UserView register(@Valid @RequestBody AuthContracts.RegisterRequest request) {
    return auth.register(request);
  }

  @PostMapping("/login")
  public AuthContracts.TokenPair login(@Valid @RequestBody AuthContracts.LoginRequest request) {
    return auth.login(request);
  }

  @PostMapping("/refresh")
  public AuthContracts.TokenPair refresh(@Valid @RequestBody AuthContracts.RefreshRequest request) {
    return auth.refresh(request);
  }
}
