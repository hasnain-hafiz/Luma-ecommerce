package com.luma.commerce.checkout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentWebhookService {
  private final PaymentGateway payments;
  private final PaymentEventRepository events;
  private final OrderRepository orders;
  private final InventoryReservationRepository reservations;

  public PaymentWebhookService(PaymentGateway payments, PaymentEventRepository events, OrderRepository orders, InventoryReservationRepository reservations) { this.payments = payments; this.events = events; this.orders = orders; this.reservations = reservations; }

  @Transactional
  public void handle(String signature, String rawPayload) {
    var webhook = payments.verifyWebhook(signature, rawPayload);
    if (events.findByProviderEventId(webhook.providerEventId()).isPresent()) return;
    var event = events.save(PaymentEventEntity.received(webhook));
    if (webhook.orderId() != null && (webhook.eventType().equals("checkout.session.completed") || webhook.eventType().equals("payment_intent.succeeded"))) {
      var order = orders.findById(webhook.orderId()).orElseThrow(); order.markPaid(); orders.save(order); reservations.findByOrderId(order.getId()).forEach(reservation -> { reservation.commit(); reservations.save(reservation); });
    }
    event.markProcessed(); events.save(event);
  }
}
