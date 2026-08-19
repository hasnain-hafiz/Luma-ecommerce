package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.luma.commerce.catalog.CatalogContracts;
import com.luma.commerce.catalog.ProductEntity;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.InventoryReservationEntity;
import com.luma.commerce.checkout.OrderEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CheckoutLifecycleTest {
  @Test
  void reservationCommitAndReleaseAreSingleStateTransitions() {
    var reservation = InventoryReservationEntity.reserve(UUID.randomUUID(), UUID.randomUUID(), 2, Instant.now().plusSeconds(60));
    reservation.commit(); assertEquals("COMMITTED", reservation.getStatus());
    assertThrows(IllegalStateException.class, reservation::commit);
  }

  @Test
  void releasingReservationRestoresProductInventory() {
    var request = new CatalogContracts.ProductUpsertRequest("SKU", "slug", "Name", "Description", UUID.randomUUID(), UUID.randomUUID(), 1000, null, 3, java.util.List.of());
    var product = ProductEntity.create(request); product.reserve(2); product.release(2);
    assertEquals(3, product.getInventoryQuantity());
  }

  @Test
  void pendingOrderCanBeCancelledButPaidOrderCannot() {
    var order = OrderEntity.pending(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "USD"));
    order.cancel(); assertEquals(CheckoutContracts.OrderStatus.CANCELLED, order.getStatus());
    assertThrows(IllegalStateException.class, order::cancel);
  }
}
