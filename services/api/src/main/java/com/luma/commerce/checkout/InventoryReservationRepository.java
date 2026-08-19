package com.luma.commerce.checkout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservationEntity, UUID> {
  List<InventoryReservationEntity> findByOrderId(UUID orderId);
  List<InventoryReservationEntity> findByStatusAndExpiresAtBefore(String status, Instant expiresAt);
}
