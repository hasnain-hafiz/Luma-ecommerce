package com.luma.commerce.checkout;

import com.luma.commerce.catalog.ProductRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryReservationService {
  private final InventoryReservationRepository reservations; private final ProductRepository products; private final OrderRepository orders; private final CheckoutDraftRepository drafts;
  public InventoryReservationService(InventoryReservationRepository reservations, ProductRepository products, OrderRepository orders) { this(reservations, products, orders, null); }
  @Autowired
  public InventoryReservationService(InventoryReservationRepository reservations, ProductRepository products, OrderRepository orders, CheckoutDraftRepository drafts) { this.reservations = reservations; this.products = products; this.orders = orders; this.drafts = drafts; }

  @Transactional
  public int releaseExpired() {
    var expired = reservations.findByStatusAndExpiresAtBefore("RESERVED", Instant.now());
    expired.forEach(reservation -> { releaseReservation(reservation); if (reservation.getDraftId() != null && drafts != null) drafts.findById(reservation.getDraftId()).ifPresent(draft -> { draft.expire(); drafts.save(draft); }); else if (reservation.getOrderId() != null) orders.findById(reservation.getOrderId()).ifPresent(order -> { if (order.getStatus() == CheckoutContracts.OrderStatus.PENDING_PAYMENT) { order.cancel(); orders.save(order); } }); });
    return expired.size();
  }

  @Transactional
  public void releaseForOrder(java.util.UUID orderId) { reservations.findByOrderId(orderId).forEach(this::releaseReservation); orders.findById(orderId).ifPresent(order -> { order.cancel(); orders.save(order); }); }

  @Transactional
  public void releaseForDraft(java.util.UUID draftId) { reservations.findByDraftId(draftId).forEach(this::releaseReservation); if (drafts != null) drafts.findById(draftId).ifPresent(draft -> { draft.expire(); drafts.save(draft); }); }

  @Transactional
  public void cancelDraft(java.util.UUID draftId) { reservations.findByDraftId(draftId).forEach(this::releaseReservation); if (drafts != null) drafts.findById(draftId).ifPresent(draft -> { draft.cancel(); drafts.save(draft); }); }

  @Transactional
  public void cancelDraft(java.util.UUID userId, java.util.UUID draftId) { if (drafts == null) throw new IllegalStateException("Draft repository is not configured"); var draft = drafts.findById(draftId).filter(existing -> existing.getUserId().equals(userId)).orElseThrow(() -> new IllegalArgumentException("Checkout draft not found")); reservations.findByDraftId(draftId).forEach(this::releaseReservation); draft.cancel(); drafts.save(draft); }

  private void releaseReservation(InventoryReservationEntity reservation) { if (!"RESERVED".equals(reservation.getStatus())) return; products.findLockedActive(reservation.getProductId()).ifPresent(product -> { product.release(reservation.getQuantity()); products.save(product); }); reservation.release(); reservations.save(reservation); }
}
