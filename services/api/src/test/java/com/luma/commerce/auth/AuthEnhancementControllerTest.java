package com.luma.commerce.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

@WebMvcTest(AuthEnhancementController.class)
@Import(SecurityConfig.class)
class AuthEnhancementControllerTest {
  @Autowired MockMvc mockMvc;
  @MockBean AuthEnhancementService auth;
  @MockBean AuthRateLimiter limiter;

  @Test
  void recoveryRequestReturnsAcceptedForUnknownOrKnownEmail() throws Exception {
    mockMvc.perform(post("/api/v1/auth/password-reset/request").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"unknown@example.com\"}"))
        .andExpect(status().isOk());
    mockMvc.perform(post("/api/v1/auth/password-reset/request").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"known@example.com\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void verificationRequestReturnsAcceptedForNormalRequest() throws Exception {
    mockMvc.perform(post("/api/v1/auth/email-verification/request").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.com\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.accepted").value(true));
    verify(auth).requestEmailVerification("user@example.com");
  }

  @Test
  void verificationRequestReturnsTooManyRequestsWhenLimiterRejects() throws Exception {
    doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Too many requests"))
        .when(limiter).check(anyString(), anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    mockMvc.perform(post("/api/v1/auth/email-verification/request").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.com\"}"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void recoveryRequestReturnsTooManyRequestsWhenLimiterRejects() throws Exception {
    doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Too many requests"))
        .when(limiter).check(anyString(), anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    mockMvc.perform(post("/api/v1/auth/password-reset/request").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"user@example.com\"}"))
        .andExpect(status().isTooManyRequests());
  }
}
