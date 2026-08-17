package com.luma.commerce;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.luma.commerce.catalog.AdminCatalogController;
import com.luma.commerce.catalog.ProductRepository;
import com.luma.commerce.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminCatalogController.class)
@Import(SecurityConfig.class)
class AdminCatalogSecurityTest {
  @Autowired MockMvc mockMvc;
  @MockBean ProductRepository products;

  @Test
  void unauthenticatedAdminWriteIsRejected() throws Exception {
    mockMvc.perform(post("/api/v1/admin/products").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void customerAdminWriteIsForbidden() throws Exception {
    mockMvc.perform(post("/api/v1/admin/products").with(jwt().jwt(token -> token.claim("scope", "ROLE_CUSTOMER")))
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isForbidden());
  }
}
