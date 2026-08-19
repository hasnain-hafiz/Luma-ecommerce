package com.luma.commerce.checkout;

import com.luma.commerce.cart.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWebhookService {
  private final PaymentGateway payments; private final PaymentEventRepository events; private final OrderRepository orders; private final OrderItemRepository orderItems; private final CheckoutDraftRepository drafts; private final CheckoutDraftItemRepository draftItems; private final InventoryReservationRepository reservations; private final CartService carts;
  public PaymentWebhookService(PaymentGateway payments, PaymentEventRepository events, OrderRepository orders, OrderItemRepository orderItems, CheckoutDraftRepository drafts, CheckoutDraftItemRepository draftItems, InventoryReservationRepository reservations, CartService carts) { this.payments = payments; this.events = events; this.orders = orders; this.orderItems = orderItems; this.drafts = drafts; this.draftItems = draftItems; this.reservations = reservations; this.carts = carts; }

  @Transactional
  public void handle(String signature, String rawPayload) {
    var webhook = payments.verifyWebhook(signature, rawPayload); if (events.findByProviderEventId(webhook.providerEventId()).isPresent()) return; var event = events.save(PaymentEventEntity.received(webhook));
    if (webhook.orderId() != null && (webhook.eventType().equals("checkout.session.completed") || webhook.eventType().equals("payment_intent.succeeded"))) {
      var existing = orders.findById(webhook.orderId());
      if (existing.isPresent()) { markPaid(existing.get()); }
      else { convertDraft(webhook.orderId()); }
    }
    event.markProcessed(); events.save(event);
  }

  private void markPaid(OrderEntity order) { order.markPaid(); orders.save(order); reservations.findByOrderId(order.getId()).forEach(reservation -> { reservation.commit(); reservations.save(reservation); }); carts.clearForUser(order.getUserId()); }

  private void convertDraft(java.util.UUID draftId) {
    var draft = drafts.findById(draftId).filter(CheckoutDraftEntity::isOpen).orElseThrow(() -> new IllegalArgumentException("Checkout draft not found or expired"));
    var order = orders.save(OrderEntity.fromDraft(draft));
    draftItems.findByDraftId(draftId).forEach(item -> orderItems.save(OrderItemEntity.snapshot(order.getId(), item.getProductId(), item.getProductNameSnapshot(), item.getSkuSnapshot(), item.getUnitPriceCentsSnapshot(), item.getQuantity(), item.getImageUrlSnapshot())));
    reservations.findByDraftId(draftId).forEach(reservation -> { reservation.attachOrder(order.getId()); reservation.commit(); reservations.save(reservation); });
    draft.convert(); drafts.save(draft); order.markPaid(); orders.save(order); carts.clearForUser(order.getUserId());
  }
}
