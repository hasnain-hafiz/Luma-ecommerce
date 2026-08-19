package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.luma.commerce.catalog.ProductRepository;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.InventoryReservationEntity;
import com.luma.commerce.checkout.InventoryReservationRepository;
import com.luma.commerce.checkout.InventoryReservationService;
import com.luma.commerce.checkout.OrderEntity;
import com.luma.commerce.checkout.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InventoryReservationServiceTest {
  @Test
  void releaseForOrderMarksReservationReleasedAndOrderCancelled() {
    var reservations = mock(InventoryReservationRepository.class); var products = mock(ProductRepository.class); var orders = mock(OrderRepository.class); var orderId = UUID.randomUUID(); var order = OrderEntity.pending(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "USD")); var reservation = InventoryReservationEntity.reserve(orderId, UUID.randomUUID(), 1, Instant.now().plusSeconds(60));
    when(reservations.findByOrderId(orderId)).thenReturn(List.of(reservation)); when(orders.findById(orderId)).thenReturn(Optional.of(order));
    new InventoryReservationService(reservations, products, orders).releaseForOrder(orderId);
    assertEquals("RELEASED", reservation.getStatus()); assertEquals(CheckoutContracts.OrderStatus.CANCELLED, order.getStatus());
  }

  @Test
  void releaseExpiredCancelsPendingOrder() {
    var reservations = mock(InventoryReservationRepository.class); var products = mock(ProductRepository.class); var orders = mock(OrderRepository.class); var orderId = UUID.randomUUID(); var order = OrderEntity.pending(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "USD")); var reservation = InventoryReservationEntity.reserve(orderId, UUID.randomUUID(), 1, Instant.now().minusSeconds(1));
    when(reservations.findByStatusAndExpiresAtBefore(any(), any())).thenReturn(List.of(reservation)); when(orders.findById(orderId)).thenReturn(Optional.of(order));
    new InventoryReservationService(reservations, products, orders).releaseExpired();
    assertEquals("RELEASED", reservation.getStatus()); assertEquals(CheckoutContracts.OrderStatus.CANCELLED, order.getStatus());
  }
}
