package com.luma.commerce.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "carts")
public class CartEntity {
  @Id private UUID id;
  private UUID userId;
  private long version;
  private Instant createdAt;
  private Instant updatedAt;
  protected CartEntity() {}
  static CartEntity create(UUID userId) { var cart = new CartEntity(); cart.id = UUID.randomUUID(); cart.userId = userId; cart.createdAt = Instant.now(); cart.updatedAt = Instant.now(); return cart; }
  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
}
