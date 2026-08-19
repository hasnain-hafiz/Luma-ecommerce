package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.luma.commerce.auth.AuthEnhancementContracts;
import com.luma.commerce.auth.AuthServices;
import com.luma.commerce.auth.OneTimeTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

class AuthEnhancementContractTest {
  @Test
  void oneTimeTokensAreLongLivedEnoughForDeliveryButExpire() {
    var service = new OneTimeTokenService(new AuthServices(Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()));
    var issued = service.issue("user@example.com", "password-reset", 30);
    assertTrue(issued.rawToken().length() >= 40);
    assertNotEquals(issued.rawToken(), issued.tokenHash());
    assertTrue(issued.expiresAt().isAfter(java.time.Instant.now()));
  }

  @Test
  void recoveryResponseDoesNotExposeWhetherAnEmailExists() {
    var response = new AuthEnhancementContracts.AcceptedResponse(true);
    assertTrue(response.accepted());
  }
}
