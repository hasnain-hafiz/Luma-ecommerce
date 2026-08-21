package com.luma.commerce.checkout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
/**
 * Retained as a test fixture only. Production Spring wiring uses RazorpayPaymentGateway.
 */
public class NoopPaymentGateway implements PaymentGateway {
  @Override public CheckoutContracts.PaymentSession createCheckoutSession(UUID orderId, int totalCents, String currency, List<CheckoutContracts.OrderItemSnapshot> items) {
    return new CheckoutContracts.PaymentSession("noop", "pending-configuration-" + orderId, "", Instant.now().plusSeconds(900));
  }
  @Override public CheckoutContracts.PaymentWebhook verifyWebhook(String signature, String rawPayload) {
    throw new UnsupportedOperationException("Noop gateway does not verify webhooks");
  }
}
