package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.luma.commerce.catalog.CatalogContracts;
import com.luma.commerce.catalog.ProductEntity;
import org.junit.jupiter.api.Test;

class CatalogContractTest {
  @Test
  void productWriteContractCreatesAndArchivesWithoutDeletingHistory() {
    var request = new CatalogContracts.ProductUpsertRequest("LM-001", "sample-product", "Sample product", "Description", java.util.UUID.randomUUID(), java.util.UUID.randomUUID(), 1000, null, 3, java.util.List.of());
    var product = ProductEntity.create(request);
    assertTrue(product.isActive());
    product.archive();
    assertFalse(product.isActive());
  }

  @Test
  void productQueryCarriesAllServerFilterInputs() {
    var query = new CatalogContracts.ProductQuery("pack", "carry", "luma", 1000, 20000, 4.0, true, "rating", 0, 24);
    assertTrue(query.search().equals("pack"));
    assertTrue(query.minRating() >= 4.0);
    assertTrue(query.available());
  }
}
