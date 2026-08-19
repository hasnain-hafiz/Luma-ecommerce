package com.luma.commerce.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public final class CartContracts {
  private CartContracts() {}
  public record AddItemRequest(@NotNull UUID productId, @Min(1) int quantity) {}
  public record UpdateItemRequest(@Min(1) int quantity) {}
  public record CartLine(UUID id, UUID productId, String productName, String sku, int unitPriceCents, int quantity, int lineTotalCents, boolean available) {}
  public record CartView(UUID id, List<CartLine> items, int subtotalCents, boolean validationRequired) {}
}
