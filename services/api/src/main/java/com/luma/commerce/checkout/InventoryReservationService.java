package com.luma.commerce.checkout;

import com.luma.commerce.catalog.ProductRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryReservationService {
  private final InventoryReservationRepository reservations;
  private final ProductRepository products;
  private final OrderRepository orders;
  public InventoryReservationService(InventoryReservationRepository reservations, ProductRepository products, OrderRepository orders) { this.reservations = reservations; this.products = products; this.orders = orders; }

  @Transactional
  public int releaseExpired() {
    var expired = reservations.findByStatusAndExpiresAtBefore("RESERVED", Instant.now());
    expired.forEach(reservation -> { releaseReservation(reservation); orders.findById(reservation.getOrderId()).ifPresent(order -> { if (order.getStatus() == CheckoutContracts.OrderStatus.PENDING_PAYMENT) { order.cancel(); orders.save(order); } }); });
    return expired.size();
  }

  @Transactional
  public void releaseForOrder(java.util.UUID orderId) {
    reservations.findByOrderId(orderId).forEach(this::releaseReservation);
    orders.findById(orderId).ifPresent(order -> { order.cancel(); orders.save(order); });
  }

  private void releaseReservation(InventoryReservationEntity reservation) {
    if (!"RESERVED".equals(reservation.getStatus())) return;
    products.findLockedActive(reservation.getProductId()).ifPresent(product -> { product.release(reservation.getQuantity()); products.save(product); });
    reservation.release(); reservations.save(reservation);
  }
}
