package com.luma.commerce.checkout;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventRepository extends JpaRepository<PaymentEventEntity, UUID> {
  Optional<PaymentEventEntity> findByProviderEventId(String providerEventId);
}
