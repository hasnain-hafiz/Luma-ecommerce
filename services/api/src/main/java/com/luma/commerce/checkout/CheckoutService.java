package com.luma.commerce.checkout;

import com.luma.commerce.cart.CartItemEntity;
import com.luma.commerce.cart.CartItemRepository;
import com.luma.commerce.cart.CartRepository;
import com.luma.commerce.catalog.ProductEntity;
import com.luma.commerce.catalog.ProductRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {
  private final CartRepository carts; private final CartItemRepository cartItems; private final ProductRepository products; private final CheckoutDraftRepository drafts; private final CheckoutDraftItemRepository draftItems; private final InventoryReservationRepository reservations; private final PaymentGateway payments;
  public CheckoutService(CartRepository carts, CartItemRepository cartItems, ProductRepository products, CheckoutDraftRepository drafts, CheckoutDraftItemRepository draftItems, InventoryReservationRepository reservations, PaymentGateway payments) { this.carts = carts; this.cartItems = cartItems; this.products = products; this.drafts = drafts; this.draftItems = draftItems; this.reservations = reservations; this.payments = payments; }

  @Transactional
  public CheckoutContracts.CheckoutResponse start(UUID userId, CheckoutContracts.StartCheckoutRequest request) {
    var cart = carts.findById(request.cartId()).filter(existing -> existing.getUserId().equals(userId)).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
    var cartLines = cartItems.findByCartId(cart.getId()); if (cartLines.isEmpty()) throw new IllegalArgumentException("Cart is empty");
    var lines = new ArrayList<Line>(); int subtotal = 0;
    for (CartItemEntity cartLine : cartLines) { var product = products.findLockedActive(cartLine.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product unavailable")); if (product.getInventoryQuantity() < cartLine.getQuantity()) throw new IllegalArgumentException("Insufficient inventory"); lines.add(new Line(product, cartLine.getQuantity())); subtotal += product.getPriceCents() * cartLine.getQuantity(); }
    var total = new CheckoutContracts.AuthoritativeTotal(subtotal, 0, 0, subtotal, "USD"); var expiresAt = Instant.now().plusSeconds(900); var draft = drafts.save(CheckoutDraftEntity.open(userId, request.shippingAddress(), total, expiresAt));
    for (Line line : lines) { line.product().reserve(line.quantity()); products.save(line.product()); draftItems.save(CheckoutDraftItemEntity.snapshot(draft.getId(), line.product().getId(), line.product().getName(), line.product().getSku(), line.product().getPriceCents(), line.quantity(), "")); reservations.save(InventoryReservationEntity.reserveForDraft(draft.getId(), line.product().getId(), line.quantity(), expiresAt)); }
    var session = payments.createCheckoutSession(draft.getId(), total.totalCents(), total.currency(), java.util.List.of());
    return new CheckoutContracts.CheckoutResponse(draft.getId(), CheckoutContracts.OrderStatus.PENDING_PAYMENT, total, session);
  }
  private record Line(ProductEntity product, int quantity) {}
}
