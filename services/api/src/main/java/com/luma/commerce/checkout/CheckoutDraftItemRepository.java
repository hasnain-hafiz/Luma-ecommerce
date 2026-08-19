package com.luma.commerce.checkout;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutDraftItemRepository extends JpaRepository<CheckoutDraftItemEntity, UUID> {
  List<CheckoutDraftItemEntity> findByDraftId(UUID draftId);
}
