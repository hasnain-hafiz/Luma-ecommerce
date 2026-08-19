package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.luma.commerce.catalog.ProductRepository;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.CheckoutDraftEntity;
import com.luma.commerce.checkout.CheckoutDraftRepository;
import com.luma.commerce.checkout.InventoryReservationEntity;
import com.luma.commerce.checkout.InventoryReservationRepository;
import com.luma.commerce.checkout.InventoryReservationService;
import com.luma.commerce.checkout.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CheckoutDraftLifecycleTest {
  @Test
  void expiredDraftReleasesReservationAndMarksDraftExpired() {
    var reservations = mock(InventoryReservationRepository.class); var products = mock(ProductRepository.class); var orders = mock(OrderRepository.class); var drafts = mock(CheckoutDraftRepository.class); var draft = CheckoutDraftEntity.open(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "USD"), Instant.now().minusSeconds(1)); var reservation = InventoryReservationEntity.reserveForDraft(draft.getId(), UUID.randomUUID(), 1, Instant.now().minusSeconds(1));
    when(reservations.findByStatusAndExpiresAtBefore(any(), any())).thenReturn(List.of(reservation)); when(drafts.findById(draft.getId())).thenReturn(Optional.of(draft));
    new InventoryReservationService(reservations, products, orders, drafts).releaseExpired();
    assertEquals("RELEASED", reservation.getStatus()); assertEquals(CheckoutDraftEntity.DraftStatus.EXPIRED, draft.getStatus());
  }
}
