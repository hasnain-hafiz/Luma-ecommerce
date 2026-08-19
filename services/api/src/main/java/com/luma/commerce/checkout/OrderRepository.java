package com.luma.commerce.checkout;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
  java.util.List<OrderEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
  java.util.Optional<OrderEntity> findByIdAndUserId(UUID id, UUID userId);
}
