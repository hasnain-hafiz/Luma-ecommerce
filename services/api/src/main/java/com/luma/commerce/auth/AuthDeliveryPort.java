package com.luma.commerce.auth;

public interface AuthDeliveryPort {
  void send(AuthEnhancementContracts.DeliveryRequest request);
}
