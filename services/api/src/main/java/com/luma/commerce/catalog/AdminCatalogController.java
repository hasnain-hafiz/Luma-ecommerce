package com.luma.commerce.catalog;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminCatalogController {
  private final ProductRepository products;

  public AdminCatalogController(ProductRepository products) { this.products = products; }

  // Security middleware must require ADMIN before this controller is exposed in production.
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CatalogContracts.ProductDetail create(@Valid @RequestBody CatalogContracts.ProductUpsertRequest request) {
    return toDetail(products.save(ProductEntity.create(request)));
  }

  @PutMapping("/{id}")
  public CatalogContracts.ProductDetail update(@PathVariable UUID id, @Valid @RequestBody CatalogContracts.ProductUpsertRequest request) {
    var product = products.findById(id).orElseThrow();
    product.apply(request);
    return toDetail(products.save(product));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void archive(@PathVariable UUID id) {
    var product = products.findById(id).orElseThrow();
    product.archive();
    products.save(product);
  }

  private CatalogContracts.ProductDetail toDetail(ProductEntity product) {
    return new CatalogContracts.ProductDetail(product.getId(), product.getSlug(), product.getSku(), product.getName(), product.getDescription(),
        product.getBrandId().toString(), product.getCategoryId().toString(), product.getPriceCents(), product.getCompareAtCents(), product.getRatingAverage(), product.getRatingCount(),
        product.getInventoryQuantity(), product.getInventoryQuantity() > 0, java.util.List.of());
  }
}
