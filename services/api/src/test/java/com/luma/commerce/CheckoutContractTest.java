package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.luma.commerce.catalog.ProductEntity;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.OrderEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CheckoutContractTest {
  @Test
  void exactOrderStatusVocabularyIsPreserved() {
    assertEquals(java.util.List.of("PENDING_PAYMENT", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED"), java.util.Arrays.stream(CheckoutContracts.OrderStatus.values()).map(Enum::name).toList());
  }

  @Test
  void orderOnlyMovesToPaidFromPendingPayment() {
    var total = new CheckoutContracts.AuthoritativeTotal(1000, 0, 0, 1000, "USD");
    var order = OrderEntity.pending(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), total);
    order.markPaid(); assertEquals(CheckoutContracts.OrderStatus.PAID, order.getStatus());
    assertThrows(IllegalStateException.class, order::markPaid);
  }

  @Test
  void productReservationRejectsInsufficientStock() {
    var request = new com.luma.commerce.catalog.CatalogContracts.ProductUpsertRequest("SKU", "slug", "Name", "Description", UUID.randomUUID(), UUID.randomUUID(), 1000, null, 1, java.util.List.of());
    var product = ProductEntity.create(request);
    assertThrows(IllegalArgumentException.class, () -> product.reserve(2));
  }
}
