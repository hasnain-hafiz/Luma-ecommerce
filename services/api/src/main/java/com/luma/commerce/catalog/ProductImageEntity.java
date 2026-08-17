package com.luma.commerce.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {
  @Id private UUID id;
  private UUID productId;
  private String imageUrl;
  private String altText;
  private int sortOrder;
  protected ProductImageEntity() {}
  public UUID getId() { return id; }
  public UUID getProductId() { return productId; }
  public String getImageUrl() { return imageUrl; }
  public String getAltText() { return altText; }
  public int getSortOrder() { return sortOrder; }
}

