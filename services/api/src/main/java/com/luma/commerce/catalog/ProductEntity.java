package com.luma.commerce.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {
  @Id private UUID id;
  @Column(name = "category_id", nullable = false) private UUID categoryId;
  @Column(name = "brand_id", nullable = false) private UUID brandId;
  @Column(nullable = false, unique = true) private String sku;
  @Column(nullable = false, unique = true) private String slug;
  @Column(nullable = false) private String name;
  @Column(nullable = false, columnDefinition = "TEXT") private String description;
  @Column(name = "price_cents", nullable = false) private int priceCents;
  @Column(name = "compare_at_cents") private Integer compareAtCents;
  @Column(name = "rating_average", nullable = false) private double ratingAverage;
  @Column(name = "rating_count", nullable = false) private int ratingCount;
  @Column(name = "inventory_quantity", nullable = false) private int inventoryQuantity;
  @Column(nullable = false) private boolean active;
  @Version private long version;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;

  protected ProductEntity() {}

  public static ProductEntity create(CatalogContracts.ProductUpsertRequest request) {
    var product = new ProductEntity();
    product.id = UUID.randomUUID();
    product.apply(request);
    product.active = true;
    product.createdAt = Instant.now();
    product.updatedAt = Instant.now();
    return product;
  }

  public void apply(CatalogContracts.ProductUpsertRequest request) {
    this.categoryId = request.categoryId(); this.brandId = request.brandId(); this.sku = request.sku(); this.slug = request.slug();
    this.name = request.name(); this.description = request.description(); this.priceCents = request.priceCents();
    this.compareAtCents = request.compareAtCents(); this.inventoryQuantity = request.inventoryQuantity(); this.updatedAt = Instant.now();
  }

  public void archive() { this.active = false; this.updatedAt = Instant.now(); }

  public UUID getId() { return id; }
  public UUID getCategoryId() { return categoryId; }
  public UUID getBrandId() { return brandId; }
  public String getSku() { return sku; }
  public String getSlug() { return slug; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public int getPriceCents() { return priceCents; }
  public Integer getCompareAtCents() { return compareAtCents; }
  public double getRatingAverage() { return ratingAverage; }
  public int getRatingCount() { return ratingCount; }
  public int getInventoryQuantity() { return inventoryQuantity; }
  public void reserve(int quantity) { if (quantity <= 0 || inventoryQuantity < quantity) throw new IllegalArgumentException("Insufficient inventory"); inventoryQuantity -= quantity; }
  public void release(int quantity) { inventoryQuantity += quantity; }
  public boolean isActive() { return active; }
}
