package com.luma.commerce.checkout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {
  private final PaymentWebhookService webhooks;
  public PaymentWebhookController(PaymentWebhookService webhooks) { this.webhooks = webhooks; }
  @PostMapping("/stripe/webhook")
  public ResponseEntity<Void> stripeWebhook(@RequestHeader("Stripe-Signature") String signature, @RequestBody String rawPayload) {
    webhooks.handle(signature, rawPayload); return ResponseEntity.ok().build();
  }
}
