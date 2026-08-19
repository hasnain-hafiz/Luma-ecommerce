package com.luma.commerce.checkout;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
  private final OrderQueryService queries;
  public OrderQueryController(OrderQueryService queries) { this.queries = queries; }
  @GetMapping
  public List<OrderReadContracts.OrderSummary> history(@AuthenticationPrincipal Jwt jwt) { return queries.history(UUID.fromString(jwt.getSubject())); }
  @GetMapping("/{orderId}")
  public OrderReadContracts.OrderDetail detail(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID orderId) { return queries.detail(UUID.fromString(jwt.getSubject()), orderId); }
}
