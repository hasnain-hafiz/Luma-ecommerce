package com.luma.commerce.checkout;

import com.luma.commerce.cart.CartItemEntity;
import com.luma.commerce.cart.CartItemRepository;
import com.luma.commerce.cart.CartRepository;
import com.luma.commerce.catalog.ProductEntity;
import com.luma.commerce.catalog.ProductRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {
  private final CartRepository carts;
  private final CartItemRepository cartItems;
  private final ProductRepository products;
  private final OrderRepository orders;
  private final OrderItemRepository orderItems;
  private final InventoryReservationRepository reservations;
  private final PaymentGateway payments;

  public CheckoutService(CartRepository carts, CartItemRepository cartItems, ProductRepository products, OrderRepository orders, OrderItemRepository orderItems, InventoryReservationRepository reservations, PaymentGateway payments) {
    this.carts = carts; this.cartItems = cartItems; this.products = products; this.orders = orders; this.orderItems = orderItems; this.reservations = reservations; this.payments = payments;
  }

  @Transactional
  public CheckoutContracts.CheckoutResponse start(UUID userId, CheckoutContracts.StartCheckoutRequest request) {
    var cart = carts.findById(request.cartId()).filter(existing -> existing.getUserId().equals(userId)).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    var items = cartItems.findByCartId(cart.getId());
    if (items.isEmpty()) throw new IllegalArgumentException("Cart is empty");
    var total = new int[] {0};
    var order = orders.save(OrderEntity.pending(userId, request.shippingAddress(), new CheckoutContracts.AuthoritativeTotal(0, 0, 0, 0, "USD")));
    for (CartItemEntity cartItem : items) {
      ProductEntity product = products.findLockedActive(cartItem.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product unavailable"));
      product.reserve(cartItem.getQuantity());
      int lineTotal = product.getPriceCents() * cartItem.getQuantity(); total[0] += lineTotal;
      orderItems.save(OrderItemEntity.snapshot(order.getId(), product.getId(), product.getName(), product.getSku(), product.getPriceCents(), cartItem.getQuantity(), ""));
      reservations.save(InventoryReservationEntity.reserve(order.getId(), product.getId(), cartItem.getQuantity(), Instant.now().plusSeconds(900)));
      products.save(product);
    }
    var authoritative = new CheckoutContracts.AuthoritativeTotal(total[0], 0, 0, total[0], "USD");
    order.applyTotal(authoritative); orders.save(order);
    var session = payments.createCheckoutSession(order.getId(), authoritative.totalCents(), authoritative.currency(), java.util.List.of());
    return new CheckoutContracts.CheckoutResponse(order.getId(), order.getStatus(), authoritative, session);
  }
}
