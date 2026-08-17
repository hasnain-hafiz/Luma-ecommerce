package com.luma.commerce.catalog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public final class CatalogContracts {
  private CatalogContracts() {}

  public record ProductSummary(UUID id, String slug, String sku, String name, String brand, String category,
                               int priceCents, Integer compareAtCents, double ratingAverage,
                               int ratingCount, int inventoryQuantity, boolean available, String imageUrl) {}

  public record ProductDetail(UUID id, String slug, String sku, String name, String description, String brand,
                              String category, int priceCents, Integer compareAtCents, double ratingAverage,
                              int ratingCount, int inventoryQuantity, boolean available, List<String> imageUrls) {}

  public record ProductPage(List<ProductSummary> items, int page, int size, long totalItems, int totalPages) {}

  public record ProductQuery(String search, String category, String brand, Integer minPriceCents, Integer maxPriceCents,
                             Double minRating, Boolean available, String sort, @Min(0) int page, @Min(1) int size) {}

  public record ProductUpsertRequest(@NotBlank String sku, @NotBlank String slug, @NotBlank String name,
                                     @NotBlank String description, UUID categoryId, UUID brandId,
                                     @Min(0) int priceCents, Integer compareAtCents, @Min(0) int inventoryQuantity,
                                     List<String> imageUrls) {}
}
