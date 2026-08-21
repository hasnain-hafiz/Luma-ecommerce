package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luma.commerce.checkout.RazorpayPaymentGateway;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class RazorpayPaymentGatewayTest {
  private static final String SECRET = "webhook-test-secret";

  @Test
  void verifiesSignatureAndExtractsDraftFromPaymentNotes() throws Exception {
    var draftId = java.util.UUID.randomUUID();
    var payload = "{\"id\":\"evt_123\",\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"entity\":{\"notes\":{\"draft_id\":\"" + draftId + "\"}}}}}";
    var gateway = new RazorpayPaymentGateway(new ObjectMapper(), "rzp_test_key", "key-secret", SECRET);
    var webhook = gateway.verifyWebhook(sign(SECRET, payload), payload);
    assertEquals("razorpay", webhook.provider());
    assertEquals("evt_123", webhook.providerEventId());
    assertEquals("payment.captured", webhook.eventType());
    assertEquals(draftId, webhook.orderId());
  }

  @Test
  void rejectsInvalidSignature() {
    var gateway = new RazorpayPaymentGateway(new ObjectMapper(), "rzp_test_key", "key-secret", SECRET);
    assertThrows(IllegalArgumentException.class, () -> gateway.verifyWebhook("wrong", "{}"));
  }

  private static String sign(String secret, String payload) throws Exception {
    var mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
  }
}
