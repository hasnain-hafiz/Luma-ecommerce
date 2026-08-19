package com.luma.commerce.checkout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class CheckoutContracts {
  private CheckoutContracts() {}
  public enum OrderStatus { PENDING_PAYMENT, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED }
  public enum ReservationStatus { RESERVED, RELEASED, COMMITTED }
  public record ShippingAddress(@NotBlank String name, @NotBlank String line1, String line2, @NotBlank String city, @NotBlank String region, @NotBlank String postalCode, @NotBlank String country) {}
  public record StartCheckoutRequest(@NotNull UUID cartId, @Valid @NotNull ShippingAddress shippingAddress) {}
  public record AuthoritativeTotal(int subtotalCents, int shippingCents, int taxCents, int totalCents, String currency) {}
  public record PaymentSession(String provider, String sessionId, String checkoutUrl, Instant expiresAt) {}
  public record CheckoutResponse(UUID orderId, OrderStatus status, AuthoritativeTotal total, PaymentSession paymentSession) {}
  public record OrderItemSnapshot(UUID productId, String productName, String sku, int unitPriceCents, int quantity, int lineTotalCents, String imageUrl) {}
  public record OrderView(UUID orderId, UUID userId, OrderStatus status, AuthoritativeTotal total, List<OrderItemSnapshot> items) {}
  public record PaymentWebhook(String provider, @NotBlank String providerEventId, @NotBlank String eventType, UUID orderId, @NotBlank String payloadHash) {}
}
