package com.luma.commerce.catalog;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
  Optional<ProductEntity> findBySlugAndActiveTrue(String slug);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select product from ProductEntity product where product.id = :id and product.active = true")
  Optional<ProductEntity> findLockedActive(@Param("id") UUID id);
  Page<ProductEntity> findByActiveTrue(Pageable pageable);
}
