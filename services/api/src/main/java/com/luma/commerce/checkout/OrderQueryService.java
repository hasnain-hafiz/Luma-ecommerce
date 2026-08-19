package com.luma.commerce.checkout;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderQueryService {
  private final OrderRepository orders; private final OrderItemRepository items;
  public OrderQueryService(OrderRepository orders, OrderItemRepository items) { this.orders = orders; this.items = items; }

  @Transactional(readOnly = true)
  public List<OrderReadContracts.OrderSummary> history(UUID userId) { return orders.findByUserIdOrderByCreatedAtDesc(userId).stream().map(order -> new OrderReadContracts.OrderSummary(order.getId(), order.getStatus(), order.getTotalCents(), order.getCurrency(), items.findByOrderId(order.getId()).stream().mapToInt(OrderItemEntity::getQuantity).sum(), order.getCreatedAt())).toList(); }

  @Transactional(readOnly = true)
  public OrderReadContracts.OrderDetail detail(UUID userId, UUID orderId) { var order = orders.findByIdAndUserId(orderId, userId).orElseThrow(() -> new NoSuchElementException("Order not found")); var shipping = new OrderReadContracts.ShippingSnapshot(order.getShippingName(), order.getShippingLine1(), order.getShippingLine2(), order.getShippingCity(), order.getShippingRegion(), order.getShippingPostalCode(), order.getShippingCountry()); var snapshots = items.findByOrderId(orderId).stream().map(item -> new OrderReadContracts.ItemSnapshot(item.getProductId(), item.getProductNameSnapshot(), item.getSkuSnapshot(), item.getUnitPriceCentsSnapshot(), item.getQuantity(), item.getLineTotalCentsSnapshot(), item.getImageUrlSnapshot())).toList(); return new OrderReadContracts.OrderDetail(order.getId(), order.getStatus(), order.getSubtotalCents(), order.getShippingCents(), order.getTaxCents(), order.getTotalCents(), order.getCurrency(), shipping, snapshots, order.getCreatedAt()); }
}
