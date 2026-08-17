package com.luma.commerce.catalog;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
  private final ProductRepository products;
  private final CategoryRepository categories;
  private final BrandRepository brands;
  private final ProductImageRepository images;

  public CatalogService(ProductRepository products, CategoryRepository categories, BrandRepository brands, ProductImageRepository images) {
    this.products = products;
    this.categories = categories;
    this.brands = brands;
    this.images = images;
  }

  public CatalogContracts.ProductPage search(CatalogContracts.ProductQuery query) {
    var sort = switch (query.sort() == null ? "featured" : query.sort()) {
      case "price-low" -> Sort.by("priceCents").ascending();
      case "price-high" -> Sort.by("priceCents").descending();
      case "rating" -> Sort.by("ratingAverage").descending();
      default -> Sort.by("createdAt").descending();
    };
    var page = products.findAll(specification(query), PageRequest.of(query.page(), query.size(), sort));
    var items = page.getContent().stream().map(this::summary).toList();
    return new CatalogContracts.ProductPage(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
  }

  public CatalogContracts.ProductDetail getBySlug(String slug) {
    return products.findBySlugAndActiveTrue(slug).map(this::detail)
        .orElseThrow(() -> new NoSuchElementException("Product not found"));
  }

  private Specification<ProductEntity> specification(CatalogContracts.ProductQuery query) {
    Specification<ProductEntity> spec = (root, q, cb) -> cb.isTrue(root.get("active"));
    if (query.search() != null && !query.search().isBlank()) {
      var term = "%" + query.search().trim().toLowerCase() + "%";
      spec = spec.and((root, q, cb) -> cb.or(cb.like(cb.lower(root.get("name")), term), cb.like(cb.lower(root.get("sku")), term), cb.like(cb.lower(root.get("slug")), term)));
    }
    if (query.minPriceCents() != null) spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), query.minPriceCents()));
    if (query.maxPriceCents() != null) spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), query.maxPriceCents()));
    if (query.minRating() != null) spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("ratingAverage"), query.minRating()));
    if (query.available() != null) spec = spec.and((root, q, cb) -> query.available() ? cb.greaterThan(root.get("inventoryQuantity"), 0) : cb.equal(root.get("inventoryQuantity"), 0));
    if (query.category() != null) spec = uuidPredicate(spec, "categoryId", query.category());
    if (query.brand() != null) spec = uuidPredicate(spec, "brandId", query.brand());
    return spec;
  }

  private Specification<ProductEntity> uuidPredicate(Specification<ProductEntity> spec, String field, String value) {
    try {
      var id = UUID.fromString(value);
      return spec.and((root, q, cb) -> cb.equal(root.get(field), id));
    } catch (IllegalArgumentException ignored) {
      return spec.and((root, q, cb) -> cb.disjunction());
    }
  }

  private CatalogContracts.ProductSummary summary(ProductEntity product) {
    return new CatalogContracts.ProductSummary(product.getId(), product.getSlug(), product.getSku(), product.getName(),
        brands.findById(product.getBrandId()).map(BrandEntity::getName).orElse(product.getBrandId().toString()), categories.findById(product.getCategoryId()).map(CategoryEntity::getName).orElse(product.getCategoryId().toString()), product.getPriceCents(), product.getCompareAtCents(), product.getRatingAverage(), product.getRatingCount(),
        product.getInventoryQuantity(), product.getInventoryQuantity() > 0, images.findByProductIdOrderBySortOrderAsc(product.getId()).stream().findFirst().map(ProductImageEntity::getImageUrl).orElse(""));
  }

  private CatalogContracts.ProductDetail detail(ProductEntity product) {
    return new CatalogContracts.ProductDetail(product.getId(), product.getSlug(), product.getSku(), product.getName(),
        product.getDescription(), brands.findById(product.getBrandId()).map(BrandEntity::getName).orElse(product.getBrandId().toString()), categories.findById(product.getCategoryId()).map(CategoryEntity::getName).orElse(product.getCategoryId().toString()), product.getPriceCents(), product.getCompareAtCents(), product.getRatingAverage(),
        product.getRatingCount(), product.getInventoryQuantity(), product.getInventoryQuantity() > 0, images.findByProductIdOrderBySortOrderAsc(product.getId()).stream().map(ProductImageEntity::getImageUrl).toList());
  }
}
