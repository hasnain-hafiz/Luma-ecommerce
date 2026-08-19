package com.luma.commerce.checkout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentGateway {
  CheckoutContracts.PaymentSession createCheckoutSession(UUID orderId, int totalCents, String currency, List<CheckoutContracts.OrderItemSnapshot> items);
  CheckoutContracts.PaymentWebhook verifyWebhook(String signature, String rawPayload);
  record Session(String provider, String sessionId, String checkoutUrl, Instant expiresAt) {}
}
