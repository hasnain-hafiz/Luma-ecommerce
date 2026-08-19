package com.luma.commerce.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
public class CartItemEntity {
  @Id private UUID id;
  private UUID cartId;
  private UUID productId;
  private int quantity;
  protected CartItemEntity() {}
  static CartItemEntity create(UUID cartId, UUID productId, int quantity) { var item = new CartItemEntity(); item.id = UUID.randomUUID(); item.cartId = cartId; item.productId = productId; item.quantity = quantity; return item; }
  void setQuantity(int quantity) { this.quantity = quantity; }
  public UUID getId() { return id; }
  public UUID getCartId() { return cartId; }
  public UUID getProductId() { return productId; }
  public int getQuantity() { return quantity; }
}
