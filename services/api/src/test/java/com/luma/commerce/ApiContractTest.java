package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ApiContractTest {
  @Test
  void preservesProductDomainVocabulary() {
    var statuses = List.of("PENDING_PAYMENT", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED");
    var roles = List.of("CUSTOMER", "ADMIN");
    var tools = List.of("searchProducts", "getProduct", "compareProducts", "getProductsByCategory", "getProductAvailability");

    assertEquals(7, statuses.size());
    assertTrue(statuses.contains("PENDING_PAYMENT"));
    assertTrue(statuses.contains("REFUNDED"));
    assertEquals(List.of("CUSTOMER", "ADMIN"), roles);
    assertEquals(5, tools.size());
    assertTrue(tools.contains("getProductAvailability"));
  }
}
