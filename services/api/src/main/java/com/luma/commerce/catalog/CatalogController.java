package com.luma.commerce.catalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/products")
public class CatalogController {
  private final CatalogService catalog;

  public CatalogController(CatalogService catalog) { this.catalog = catalog; }

  @GetMapping
  public CatalogContracts.ProductPage search(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) Integer minPriceCents,
      @RequestParam(required = false) Integer maxPriceCents,
      @RequestParam(required = false) Double minRating,
      @RequestParam(required = false) Boolean available,
      @RequestParam(defaultValue = "featured") String sort,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "24") @Min(1) @Max(100) int size) {
    return catalog.search(new CatalogContracts.ProductQuery(search, category, brand, minPriceCents, maxPriceCents, minRating, available, sort, page, size));
  }

  @GetMapping("/{slug}")
  public CatalogContracts.ProductDetail getBySlug(@PathVariable String slug) { return catalog.getBySlug(slug); }
}
