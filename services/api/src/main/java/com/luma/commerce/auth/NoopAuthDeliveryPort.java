package com.luma.commerce.auth;

import org.springframework.stereotype.Component;

@Component
public class NoopAuthDeliveryPort implements AuthDeliveryPort {
  @Override public void send(AuthEnhancementContracts.DeliveryRequest request) {
    // Intentionally does not log or expose raw tokens. Replace with a provider adapter in deployment.
  }
}
