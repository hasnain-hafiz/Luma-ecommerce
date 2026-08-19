package com.luma.commerce.checkout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutDraftRepository extends JpaRepository<CheckoutDraftEntity, UUID> {
  List<CheckoutDraftEntity> findByStatusAndExpiresAtBefore(CheckoutDraftEntity.DraftStatus status, Instant expiresAt);
}
