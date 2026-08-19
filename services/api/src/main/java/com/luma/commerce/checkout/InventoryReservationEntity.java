package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservationEntity {
  @Id private UUID id;
  private UUID orderId;
  private UUID productId;
  private int quantity;
  private String status;
  private Instant expiresAt;
  protected InventoryReservationEntity() {}
  public static InventoryReservationEntity reserve(UUID orderId, UUID productId, int quantity, Instant expiresAt) { var reservation = new InventoryReservationEntity(); reservation.id = UUID.randomUUID(); reservation.orderId = orderId; reservation.productId = productId; reservation.quantity = quantity; reservation.status = "RESERVED"; reservation.expiresAt = expiresAt; return reservation; }
  public String getStatus() { return status; }
  public UUID getOrderId() { return orderId; }
  public UUID getProductId() { return productId; }
  public int getQuantity() { return quantity; }
  public void commit() { if (!"RESERVED".equals(status)) throw new IllegalStateException("Reservation is not active"); status = "COMMITTED"; }
  public void release() { if ("RESERVED".equals(status)) status = "RELEASED"; }
}
