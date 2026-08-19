package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "checkout_draft_items")
public class CheckoutDraftItemEntity {
  @Id private UUID id;
  private UUID draftId;
  private UUID productId;
  private String productNameSnapshot;
  private String skuSnapshot;
  private int unitPriceCentsSnapshot;
  private int quantity;
  private int lineTotalCentsSnapshot;
  private String imageUrlSnapshot;
  protected CheckoutDraftItemEntity() {}
  public static CheckoutDraftItemEntity snapshot(UUID draftId, UUID productId, String name, String sku, int unitPrice, int quantity, String imageUrl) { var item = new CheckoutDraftItemEntity(); item.id = UUID.randomUUID(); item.draftId = draftId; item.productId = productId; item.productNameSnapshot = name; item.skuSnapshot = sku; item.unitPriceCentsSnapshot = unitPrice; item.quantity = quantity; item.lineTotalCentsSnapshot = unitPrice * quantity; item.imageUrlSnapshot = imageUrl; return item; }
  public UUID getProductId() { return productId; }
  public String getProductNameSnapshot() { return productNameSnapshot; }
  public String getSkuSnapshot() { return skuSnapshot; }
  public int getUnitPriceCentsSnapshot() { return unitPriceCentsSnapshot; }
  public int getQuantity() { return quantity; }
  public String getImageUrlSnapshot() { return imageUrlSnapshot; }
}
