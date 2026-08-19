package com.luma.commerce.checkout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class NoopPaymentGateway implements PaymentGateway {
  @Override public CheckoutContracts.PaymentSession createCheckoutSession(UUID orderId, int totalCents, String currency, List<CheckoutContracts.OrderItemSnapshot> items) {
    return new CheckoutContracts.PaymentSession("stripe", "pending-configuration-" + orderId, "", Instant.now().plusSeconds(900));
  }
  @Override public CheckoutContracts.PaymentWebhook verifyWebhook(String signature, String rawPayload) {
    throw new UnsupportedOperationException("Stripe webhook verification requires STRIPE_WEBHOOK_SECRET and a real gateway adapter");
  }
}
