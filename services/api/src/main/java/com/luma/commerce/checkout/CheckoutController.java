package com.luma.commerce.checkout;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
  private final CheckoutService checkout;
  public CheckoutController(CheckoutService checkout) { this.checkout = checkout; }
  @PostMapping
  public CheckoutContracts.CheckoutResponse start(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CheckoutContracts.StartCheckoutRequest request) { return checkout.start(UUID.fromString(jwt.getSubject()), request); }
}
