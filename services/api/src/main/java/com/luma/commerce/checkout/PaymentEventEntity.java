package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_events")
public class PaymentEventEntity {
  @Id private UUID id;
  private String provider;
  private String providerEventId;
  private UUID orderId;
  private String eventType;
  private Instant receivedAt;
  private Instant processedAt;
  private String payloadHash;
  protected PaymentEventEntity() {}
  static PaymentEventEntity received(CheckoutContracts.PaymentWebhook webhook) { var event = new PaymentEventEntity(); event.id = UUID.randomUUID(); event.provider = webhook.provider(); event.providerEventId = webhook.providerEventId(); event.orderId = webhook.orderId(); event.eventType = webhook.eventType(); event.payloadHash = webhook.payloadHash(); event.receivedAt = Instant.now(); return event; }
  public void markProcessed() { processedAt = Instant.now(); }
}
