package com.luma.commerce.catalog;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
  Optional<ProductEntity> findBySlugAndActiveTrue(String slug);
  Page<ProductEntity> findByActiveTrue(Pageable pageable);
}
