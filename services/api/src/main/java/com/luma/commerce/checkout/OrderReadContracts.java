package com.luma.commerce.checkout;

import java.util.List;
import java.util.UUID;

public final class OrderReadContracts {
  private OrderReadContracts() {}
  public record OrderSummary(UUID id, CheckoutContracts.OrderStatus status, int totalCents, String currency, int itemCount, java.time.Instant createdAt) {}
  public record OrderDetail(UUID id, CheckoutContracts.OrderStatus status, int subtotalCents, int shippingCents, int taxCents, int totalCents, String currency, ShippingSnapshot shipping, List<ItemSnapshot> items, java.time.Instant createdAt) {}
  public record ShippingSnapshot(String name, String line1, String line2, String city, String region, String postalCode, String country) {}
  public record ItemSnapshot(UUID productId, String name, String sku, int unitPriceCents, int quantity, int lineTotalCents, String imageUrl) {}
}
