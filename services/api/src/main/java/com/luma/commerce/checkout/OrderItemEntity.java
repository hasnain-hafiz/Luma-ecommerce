package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
  @Id private UUID id;
  private UUID orderId;
  private UUID productId;
  private String productNameSnapshot;
  private String skuSnapshot;
  private int unitPriceCentsSnapshot;
  private int quantity;
  private int lineTotalCentsSnapshot;
  private String imageUrlSnapshot;
  protected OrderItemEntity() {}
  public static OrderItemEntity snapshot(UUID orderId, UUID productId, String name, String sku, int unitPrice, int quantity, String imageUrl) { var item = new OrderItemEntity(); item.id = UUID.randomUUID(); item.orderId = orderId; item.productId = productId; item.productNameSnapshot = name; item.skuSnapshot = sku; item.unitPriceCentsSnapshot = unitPrice; item.quantity = quantity; item.lineTotalCentsSnapshot = unitPrice * quantity; item.imageUrlSnapshot = imageUrl; return item;   }
  public UUID getProductId() { return productId; }
  public String getProductNameSnapshot() { return productNameSnapshot; }
  public String getSkuSnapshot() { return skuSnapshot; }
  public int getUnitPriceCentsSnapshot() { return unitPriceCentsSnapshot; }
  public int getQuantity() { return quantity; }
  public int getLineTotalCentsSnapshot() { return lineTotalCentsSnapshot; }
  public String getImageUrlSnapshot() { return imageUrlSnapshot; }
}
