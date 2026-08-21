package com.luma.commerce.checkout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RazorpayPaymentGateway implements PaymentGateway {
  private final RestClient client;
  private final ObjectMapper json;
  private final String keyId;
  private final String keySecret;
  private final String webhookSecret;

  public RazorpayPaymentGateway(
      ObjectMapper json,
      @Value("${RAZORPAY_KEY_ID:}") String keyId,
      @Value("${RAZORPAY_KEY_SECRET:}") String keySecret,
      @Value("${RAZORPAY_WEBHOOK_SECRET:}") String webhookSecret) {
    this.json = json;
    this.keyId = keyId;
    this.keySecret = keySecret;
    this.webhookSecret = webhookSecret;
    this.client = RestClient.builder().baseUrl("https://api.razorpay.com/v1").build();
  }

  @Override
  public CheckoutContracts.PaymentSession createCheckoutSession(UUID draftId, int totalCents, String currency, List<CheckoutContracts.OrderItemSnapshot> items) {
    requireConfigured();
    var body = json.createObjectNode();
    body.put("amount", totalCents);
    body.put("currency", currency == null || currency.isBlank() ? "INR" : currency);
    body.put("receipt", draftId.toString());
    body.put("payment_capture", 1);
    var notes = body.putObject("notes");
    notes.put("draft_id", draftId.toString());
    var response = client.post().uri("/orders").header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(headers -> headers.setBasicAuth(keyId, keySecret)).body(body).retrieve().body(JsonNode.class);
    if (response == null || response.path("id").isMissingNode()) throw new IllegalStateException("Razorpay order creation failed");
    var razorpayOrderId = response.path("id").asText();
    return new CheckoutContracts.PaymentSession("razorpay", razorpayOrderId, "", Instant.now().plusSeconds(900));
  }

  @Override
  public CheckoutContracts.PaymentWebhook verifyWebhook(String signature, String rawPayload) {
    requireConfigured();
    if (signature == null || signature.isBlank() || !constantTimeEquals(signature, hmacHex(webhookSecret, rawPayload))) {
      throw new IllegalArgumentException("Invalid Razorpay webhook signature");
    }
    try {
      var root = json.readTree(rawPayload);
      var eventId = root.path("id").asText("");
      var eventType = root.path("event").asText("");
      var payment = root.path("payload").path("payment").path("entity");
      var order = root.path("payload").path("order").path("entity");
      var notes = !payment.path("notes").isMissingNode() ? payment.path("notes") : order.path("notes");
      var draftId = notes.path("draft_id").asText("");
      if (draftId.isBlank()) draftId = order.path("receipt").asText("");
      UUID orderId = draftId.isBlank() ? null : UUID.fromString(draftId);
      return new CheckoutContracts.PaymentWebhook("razorpay", eventId, eventType, orderId, sha256(rawPayload));
    } catch (Exception ex) {
      if (ex instanceof IllegalArgumentException illegal) throw illegal;
      throw new IllegalArgumentException("Invalid Razorpay webhook payload", ex);
    }
  }

  private void requireConfigured() {
    if (keyId.isBlank() || keySecret.isBlank() || webhookSecret.isBlank()) throw new IllegalStateException("Razorpay credentials are not configured");
  }

  private static String hmacHex(String secret, String value) {
    try {
      var mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception ex) { throw new IllegalStateException("Unable to verify Razorpay signature", ex); }
  }

  private static String sha256(String value) {
    try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
    catch (Exception ex) { throw new IllegalStateException("Unable to hash Razorpay event", ex); }
  }

  private static boolean constantTimeEquals(String a, String b) { return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8)); }
}
