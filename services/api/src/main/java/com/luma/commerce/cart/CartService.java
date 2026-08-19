package com.luma.commerce.cart;

import com.luma.commerce.catalog.ProductEntity;
import com.luma.commerce.catalog.ProductRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final CartRepository carts;
  private final CartItemRepository items;
  private final ProductRepository products;

  public CartService(CartRepository carts, CartItemRepository items, ProductRepository products) { this.carts = carts; this.items = items; this.products = products; }

  @Transactional(readOnly = true)
  public CartContracts.CartView get(UUID userId) { var cart = carts.findByUserId(userId).orElseGet(() -> CartEntity.create(userId)); return view(cart); }

  @Transactional
  public CartContracts.CartView add(UUID userId, CartContracts.AddItemRequest request) {
    var product = products.findById(request.productId()).orElseThrow();
    var cart = carts.findByUserId(userId).orElseGet(() -> carts.save(CartEntity.create(userId)));
    var item = items.findByCartIdAndProductId(cart.getId(), product.getId()).orElseGet(() -> CartItemEntity.create(cart.getId(), product.getId(), 0));
    item.setQuantity(item.getQuantity() + request.quantity()); items.save(item); return view(cart);
  }

  @Transactional
  public CartContracts.CartView update(UUID userId, UUID productId, CartContracts.UpdateItemRequest request) {
    var cart = ownedCart(userId); var item = items.findByCartIdAndProductId(cart.getId(), productId).orElseThrow(); item.setQuantity(request.quantity()); items.save(item); return view(cart);
  }

  @Transactional
  public void remove(UUID userId, UUID productId) { var cart = ownedCart(userId); items.findByCartIdAndProductId(cart.getId(), productId).ifPresent(items::delete); }

  private CartEntity ownedCart(UUID userId) { return carts.findByUserId(userId).orElseThrow(); }
  private CartContracts.CartView view(CartEntity cart) { var lines = items.findByCartId(cart.getId()).stream().map(item -> products.findById(item.getProductId()).map(product -> line(item, product)).orElse(null)).filter(java.util.Objects::nonNull).toList(); return new CartContracts.CartView(cart.getId(), lines, lines.stream().mapToInt(CartContracts.CartLine::lineTotalCents).sum(), true); }
  private CartContracts.CartLine line(CartItemEntity item, ProductEntity product) { return new CartContracts.CartLine(item.getId(), product.getId(), product.getName(), product.getSku(), product.getPriceCents(), item.getQuantity(), product.getPriceCents() * item.getQuantity(), product.getInventoryQuantity() >= item.getQuantity()); }
}
