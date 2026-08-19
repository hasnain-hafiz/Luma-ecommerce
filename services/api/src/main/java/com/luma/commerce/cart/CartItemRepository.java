package com.luma.commerce.cart;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {
  List<CartItemEntity> findByCartId(UUID cartId);
  java.util.Optional<CartItemEntity> findByCartIdAndProductId(UUID cartId, UUID productId);
}
