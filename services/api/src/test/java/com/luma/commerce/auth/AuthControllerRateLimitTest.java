package com.luma.commerce.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.luma.commerce.config.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerRateLimitTest {
  @Autowired MockMvc mockMvc;
  @MockBean AuthApplicationService auth;
  @MockBean AuthRateLimiter limiter;

  @Test
  void registrationReturnsTooManyRequestsWhenRateLimitIsExceeded() throws Exception {
    reject(); mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.com\",\"password\":\"a-long-password\"}"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void loginReturnsTooManyRequestsWhenRateLimitIsExceeded() throws Exception {
    reject(); mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.com\",\"password\":\"a-long-password\"}"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void refreshReturnsTooManyRequestsWhenRateLimitIsExceeded() throws Exception {
    reject(); mockMvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"opaque-token\"}"))
        .andExpect(status().isTooManyRequests());
  }

  private void reject() {
    doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Too many requests"))
        .when(limiter).check(anyString(), anyString(), any(Integer.class), any());
  }
}
