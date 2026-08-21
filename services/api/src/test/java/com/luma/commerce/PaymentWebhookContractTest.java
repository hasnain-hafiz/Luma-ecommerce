package com.luma.commerce;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.OrderEntity;
import com.luma.commerce.checkout.OrderRepository;
import com.luma.commerce.checkout.PaymentEventRepository;
import com.luma.commerce.checkout.PaymentGateway;
import com.luma.commerce.checkout.PaymentWebhookService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentWebhookContractTest {
  @Test
  void duplicateProviderEventIsIgnored() {
    var gateway = mock(PaymentGateway.class); var events = mock(PaymentEventRepository.class); var orders = mock(OrderRepository.class); var reservations = mock(com.luma.commerce.checkout.InventoryReservationRepository.class);
    when(gateway.verifyWebhook("sig", "payload")).thenReturn(new CheckoutContracts.PaymentWebhook("razorpay", "evt-1", "payment.captured", UUID.randomUUID(), "hash"));
    when(events.findByProviderEventId("evt-1")).thenReturn(Optional.of(mock(com.luma.commerce.checkout.PaymentEventEntity.class)));
    new PaymentWebhookService(gateway, events, orders, mock(com.luma.commerce.checkout.OrderItemRepository.class), mock(com.luma.commerce.checkout.CheckoutDraftRepository.class), mock(com.luma.commerce.checkout.CheckoutDraftItemRepository.class), reservations, mock(com.luma.commerce.cart.CartService.class)).handle("sig", "payload");
    verify(orders, never()).save(any());
  }

  @Test
  void successfulPaymentMovesPendingOrderToPaid() {
    var gateway = mock(PaymentGateway.class); var events = mock(PaymentEventRepository.class); var orders = mock(OrderRepository.class); var reservations = mock(com.luma.commerce.checkout.InventoryReservationRepository.class); var order = OrderEntity.pending(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "INR"));
    when(gateway.verifyWebhook("sig", "payload")).thenReturn(new CheckoutContracts.PaymentWebhook("razorpay", "evt-2", "payment.captured", order.getId(), "hash"));
    var reservation = com.luma.commerce.checkout.InventoryReservationEntity.reserve(order.getId(), UUID.randomUUID(), 1, java.time.Instant.now().plusSeconds(60));
    when(events.findByProviderEventId("evt-2")).thenReturn(Optional.empty()); when(orders.findById(order.getId())).thenReturn(Optional.of(order)); when(reservations.findByOrderId(order.getId())).thenReturn(java.util.List.of(reservation)); when(events.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    new PaymentWebhookService(gateway, events, orders, mock(com.luma.commerce.checkout.OrderItemRepository.class), mock(com.luma.commerce.checkout.CheckoutDraftRepository.class), mock(com.luma.commerce.checkout.CheckoutDraftItemRepository.class), reservations, mock(com.luma.commerce.cart.CartService.class)).handle("sig", "payload");
    verify(orders).save(order);
    org.junit.jupiter.api.Assertions.assertEquals(CheckoutContracts.OrderStatus.PAID, order.getStatus());
    org.junit.jupiter.api.Assertions.assertEquals("COMMITTED", reservation.getStatus());
  }
}
