package com.luma.commerce.cart;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
  private final CartService carts;
  public CartController(CartService carts) { this.carts = carts; }

  @GetMapping
  public CartContracts.CartView get(@AuthenticationPrincipal Jwt jwt) { return carts.get(userId(jwt)); }
  @PostMapping("/items")
  public CartContracts.CartView add(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CartContracts.AddItemRequest request) { return carts.add(userId(jwt), request); }
  @PatchMapping("/items/{productId}")
  public CartContracts.CartView update(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId, @Valid @RequestBody CartContracts.UpdateItemRequest request) { return carts.update(userId(jwt), productId, request); }
  @DeleteMapping("/items/{productId}")
  public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId) { carts.remove(userId(jwt), productId); }
  private UUID userId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
}
