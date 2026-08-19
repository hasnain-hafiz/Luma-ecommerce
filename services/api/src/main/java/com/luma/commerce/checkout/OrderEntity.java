package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id private UUID id;
  private UUID userId;
  @Enumerated(EnumType.STRING) private CheckoutContracts.OrderStatus status;
  private String currency;
  private int subtotalCents;
  private int shippingCents;
  private int taxCents;
  private int totalCents;
  private String shippingName;
  private String shippingLine1;
  private String shippingLine2;
  private String shippingCity;
  private String shippingRegion;
  private String shippingPostalCode;
  private String shippingCountry;
  private Instant createdAt;
  private Instant updatedAt;
  protected OrderEntity() {}
  public static OrderEntity fromDraft(CheckoutDraftEntity draft) { return pending(draft.getUserId(), draft.shippingAddress(), new CheckoutContracts.AuthoritativeTotal(draft.getSubtotalCents(), draft.getShippingCents(), draft.getTaxCents(), draft.getTotalCents(), draft.getCurrency())); }
  public static OrderEntity pending(UUID userId, CheckoutContracts.ShippingAddress shipping, CheckoutContracts.AuthoritativeTotal total) { var order = new OrderEntity(); order.id = UUID.randomUUID(); order.userId = userId; order.status = CheckoutContracts.OrderStatus.PENDING_PAYMENT; order.currency = total.currency(); order.subtotalCents = total.subtotalCents(); order.shippingCents = total.shippingCents(); order.taxCents = total.taxCents(); order.totalCents = total.totalCents(); order.shippingName = shipping.name(); order.shippingLine1 = shipping.line1(); order.shippingLine2 = shipping.line2(); order.shippingCity = shipping.city(); order.shippingRegion = shipping.region(); order.shippingPostalCode = shipping.postalCode(); order.shippingCountry = shipping.country(); order.createdAt = Instant.now(); order.updatedAt = Instant.now(); return order; }
  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public CheckoutContracts.OrderStatus getStatus() { return status; }
  public int getTotalCents() { return totalCents; }
  public void applyTotal(CheckoutContracts.AuthoritativeTotal total) { this.currency = total.currency(); this.subtotalCents = total.subtotalCents(); this.shippingCents = total.shippingCents(); this.taxCents = total.taxCents(); this.totalCents = total.totalCents(); this.updatedAt = Instant.now(); }
  public void markPaid() { if (status != CheckoutContracts.OrderStatus.PENDING_PAYMENT) throw new IllegalStateException("Invalid payment transition"); status = CheckoutContracts.OrderStatus.PAID; updatedAt = Instant.now(); }
  public void cancel() { if (status != CheckoutContracts.OrderStatus.PENDING_PAYMENT) throw new IllegalStateException("Only pending orders can be cancelled"); status = CheckoutContracts.OrderStatus.CANCELLED; updatedAt = Instant.now(); }
}
