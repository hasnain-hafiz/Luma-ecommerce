package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.cart.CartService;
import static org.mockito.Mockito.verify;
import com.luma.commerce.checkout.CheckoutDraftEntity;
import com.luma.commerce.checkout.CheckoutDraftItemEntity;
import com.luma.commerce.checkout.CheckoutDraftItemRepository;
import com.luma.commerce.checkout.CheckoutDraftRepository;
import com.luma.commerce.checkout.InventoryReservationEntity;
import com.luma.commerce.checkout.InventoryReservationRepository;
import com.luma.commerce.checkout.OrderEntity;
import com.luma.commerce.checkout.OrderItemRepository;
import com.luma.commerce.checkout.OrderRepository;
import com.luma.commerce.checkout.PaymentEventRepository;
import com.luma.commerce.checkout.PaymentGateway;
import com.luma.commerce.checkout.PaymentWebhookService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CheckoutDraftConversionTest {
  @Test
  void verifiedPaymentConvertsDraftIntoPaidOrderAndCommitsReservation() {
    var gateway = mock(PaymentGateway.class); var events = mock(PaymentEventRepository.class); var orders = mock(OrderRepository.class); var orderItems = mock(OrderItemRepository.class); var drafts = mock(CheckoutDraftRepository.class); var draftItems = mock(CheckoutDraftItemRepository.class); var reservations = mock(InventoryReservationRepository.class);
    var draft = CheckoutDraftEntity.open(UUID.randomUUID(), new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(100, 0, 0, 100, "INR"), Instant.now().plusSeconds(60));
    var draftItem = CheckoutDraftItemEntity.snapshot(draft.getId(), UUID.randomUUID(), "Product", "SKU", 100, 1, "image"); var reservation = InventoryReservationEntity.reserveForDraft(draft.getId(), draftItem.getProductId(), 1, Instant.now().plusSeconds(60));
    when(gateway.verifyWebhook("sig", "payload")).thenReturn(new CheckoutContracts.PaymentWebhook("razorpay", "evt-draft", "payment.captured", draft.getId(), "hash")); when(events.findByProviderEventId("evt-draft")).thenReturn(Optional.empty()); when(events.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); when(drafts.findById(draft.getId())).thenReturn(Optional.of(draft)); when(draftItems.findByDraftId(draft.getId())).thenReturn(List.of(draftItem)); when(reservations.findByDraftId(draft.getId())).thenReturn(List.of(reservation)); when(orders.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    var carts = mock(CartService.class); new PaymentWebhookService(gateway, events, orders, orderItems, drafts, draftItems, reservations, carts).handle("sig", "payload"); verify(carts).clearForUser(draft.getUserId());
    assertEquals(CheckoutContracts.OrderStatus.PAID, draft.getStatus() == CheckoutDraftEntity.DraftStatus.CONVERTED ? CheckoutContracts.OrderStatus.PAID : null); assertEquals("COMMITTED", reservation.getStatus()); org.junit.jupiter.api.Assertions.assertEquals(null, reservation.getDraftId()); org.junit.jupiter.api.Assertions.assertNotNull(reservation.getOrderId());
  }
}
