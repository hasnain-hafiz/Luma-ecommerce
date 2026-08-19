package com.luma.commerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luma.commerce.cart.CartItemRepository;
import com.luma.commerce.cart.CartRepository;
import com.luma.commerce.cart.CartService;
import com.luma.commerce.catalog.ProductRepository;
import com.luma.commerce.checkout.CheckoutContracts;
import com.luma.commerce.checkout.OrderEntity;
import com.luma.commerce.checkout.OrderItemEntity;
import com.luma.commerce.checkout.OrderItemRepository;
import com.luma.commerce.checkout.OrderQueryService;
import com.luma.commerce.checkout.OrderRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderHistoryContractTest {
  @Test
  void detailMapsPersistedSnapshotsThroughOwnedOrderLookup() {
    var orders = mock(OrderRepository.class); var items = mock(OrderItemRepository.class); var userId = UUID.randomUUID(); var order = OrderEntity.pending(userId, new CheckoutContracts.ShippingAddress("Name", "Line", null, "City", "Region", "00000", "US"), new CheckoutContracts.AuthoritativeTotal(299, 0, 0, 299, "USD")); var item = OrderItemEntity.snapshot(order.getId(), UUID.randomUUID(), "Headphones", "SKU-1", 299, 1, "image");
    when(orders.findByIdAndUserId(order.getId(), userId)).thenReturn(Optional.of(order)); when(items.findByOrderId(order.getId())).thenReturn(List.of(item));
    var detail = new OrderQueryService(orders, items).detail(userId, order.getId());
    assertEquals("Headphones", detail.items().getFirst().name()); assertEquals(299, detail.totalCents());
  }

  @Test
  void foreignOrMissingOrderIsReturnedAsNotFound() {
    var orders = mock(OrderRepository.class); var items = mock(OrderItemRepository.class); var owner = UUID.randomUUID(); var foreignOrder = UUID.randomUUID();
    when(orders.findByIdAndUserId(foreignOrder, owner)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> new OrderQueryService(orders, items).detail(owner, foreignOrder));
  }

  @Test
  void cartClearTargetsOnlyTheOwnedCart() {
    var carts = mock(CartRepository.class); var items = mock(CartItemRepository.class); var products = mock(ProductRepository.class); var userId = UUID.randomUUID(); var cart = com.luma.commerce.cart.CartEntity.create(userId);
    when(carts.findByUserId(userId)).thenReturn(Optional.of(cart)); new CartService(carts, items, products).clearForUser(userId); verify(items).deleteByCartId(cart.getId());
  }
}
