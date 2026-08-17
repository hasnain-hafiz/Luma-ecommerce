package com.luma.commerce.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, UUID> {
  List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(UUID productId);
}
