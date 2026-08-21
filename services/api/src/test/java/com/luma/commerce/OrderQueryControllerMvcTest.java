package com.luma.commerce;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.luma.commerce.api.ApiExceptionHandler;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.OrderQueryController;
import com.luma.commerce.checkout.OrderQueryService;
import com.luma.commerce.checkout.OrderReadContracts;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OrderQueryControllerMvcTest {
  private final OrderQueryService queries = mock(OrderQueryService.class);
  private MockMvc mvc;
  private final UUID userId = UUID.randomUUID();
  private final UUID orderId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(new OrderQueryController(queries)).setControllerAdvice(new ApiExceptionHandler()).setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
      @Override public boolean supportsParameter(MethodParameter parameter) { return parameter.getParameterType().equals(Jwt.class); }
      @Override public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        return Jwt.withTokenValue("test").subject(request.getHeader("X-Test-User")).header("alg", "none").build();
      }
    }).build();
  }

  @Test
  void ownedOrderDetailIsReturned() throws Exception {
    var detail = new OrderReadContracts.OrderDetail(orderId, CheckoutContracts.OrderStatus.PAID, 100, 0, 0, 100, "USD", new OrderReadContracts.ShippingSnapshot("Name", "Line", null, "City", "Region", "00000", "US"), List.of(), Instant.now());
    when(queries.detail(userId, orderId)).thenReturn(detail);
    mvc.perform(get("/api/v1/orders/{id}", orderId).header("X-Test-User", userId.toString())).andExpect(status().isOk());
  }

  @Test
  void foreignOrderIsNotFound() throws Exception {
    var foreignOwner = UUID.randomUUID();
    when(queries.detail(foreignOwner, orderId)).thenThrow(new NoSuchElementException("Order not found"));
    mvc.perform(get("/api/v1/orders/{id}", orderId).header("X-Test-User", foreignOwner.toString())).andExpect(status().isNotFound());
  }

  @Test
  void nonexistentOrderIsNotFound() throws Exception {
    var nonexistentOrder = UUID.randomUUID();
    when(queries.detail(userId, nonexistentOrder)).thenThrow(new NoSuchElementException("Order not found"));
    mvc.perform(get("/api/v1/orders/{id}", nonexistentOrder).header("X-Test-User", userId.toString())).andExpect(status().isNotFound());
  }
}
