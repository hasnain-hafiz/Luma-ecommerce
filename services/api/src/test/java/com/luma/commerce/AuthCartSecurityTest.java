package com.luma.commerce;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.luma.commerce.cart.CartController;
import com.luma.commerce.cart.CartService;
import com.luma.commerce.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
class AuthCartSecurityTest {
  @Autowired MockMvc mockMvc;
  @MockBean CartService carts;

  @Test
  void unauthenticatedCartReadIsRejected() throws Exception {
    mockMvc.perform(get("/api/v1/cart")).andExpect(status().isUnauthorized());
  }

  @Test
  void customerCanReachCartRoute() throws Exception {
    mockMvc.perform(get("/api/v1/cart").with(jwt().jwt(token -> token.subject(UUID.randomUUID().toString())).authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .andExpect(status().isOk());
  }

  @Test
  void adminCannotUseCustomerCartRoute() throws Exception {
    mockMvc.perform(get("/api/v1/cart").with(jwt().jwt(token -> token.subject(UUID.randomUUID().toString())).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        .andExpect(status().isForbidden());
  }
}
